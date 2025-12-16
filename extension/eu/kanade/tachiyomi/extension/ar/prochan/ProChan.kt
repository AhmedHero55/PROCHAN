package eu.kanade.tachiyomi.extension.prochan

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Headers
import okhttp3.OkHttpClient
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.jsoup.Jsoup
import android.content.Context
import androidx.preference.PreferenceScreen
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceManager

/**
 * ProChan extension for Tachiyomi (target: Tachiyomi 0.15.3 API)
 *
 * ملاحظة: تم اختيار selectors عامة ومرنة — قد تحتاج لتعديل طفيف إذا تغيّرت بنية صفحات prochan.net.
 * الهدف الرئيسي هنا: كود متكامل يعمل مع API ويمتاز بسهولة التعديل لاحقاً.
 */
class ProChan(private val context: Context) : ParsedHttpSource() {

    // Basic source info
    override val name = "ProChan"
    override val baseUrl: String
        get() = getPrefBaseUrl()
    override val lang = "ar"
    override val supportsLatest = true

    // HTTP client (use tachiyomi's client if available)
    override val client: OkHttpClient = network.cloudflareClient

    override fun headersBuilder(): Headers.Builder = Headers.Builder()
        .add("User-Agent", "Mozilla/5.0 (Android) Tachiyomi ProChan Extension")
        .add("Accept", "text/html,application/xhtml+xml")

    // Preferences: configurable base URL
    private fun getPrefBaseUrl(): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    }

    override fun setupPreferenceScreen(screen: PreferenceScreen) {
        val baseUrlPref = EditTextPreference(screen.context).apply {
            key = PREF_BASE_URL
            title = "Base URL"
            summary = "عنوان الموقع (Base URL) للـ ProChan"
            dialogTitle = "Base URL"
            this.setDefaultValue(DEFAULT_BASE_URL)
            setOnPreferenceChangeListener { pref, newValue ->
                pref.summary = newValue as String
                true
            }
        }
        screen.addPreference(baseUrlPref)
    }

    // --- Popular ---
    override fun fetchPopularManga(page: Int): Observable<MangasPage> {
        return super.fetchPopularManga(page)
    }

    override fun popularMangaRequest(page: Int) = GET("$baseUrl/", headers)

    // Flexible selector: try several likely container selectors
    override fun popularMangaSelector(): String = "article, .card, .post, .manga, .series"

    override fun popularMangaFromElement(element: Element): SManga {
        val manga = SManga.create()
        // try to find link and title
        val a = element.selectFirst("a[href]")
        val title = element.selectFirst("h3, h2, .title, .entry-title") ?: a
        manga.title = title?.text()?.trim() ?: a?.attr("title") ?: "Unknown"
        manga.setUrlWithoutDomain(a?.attr("href") ?: element.selectFirst("a")?.attr("href") ?: "")
        // thumbnail
        val img = element.selectFirst("img")
        manga.thumbnail_url = img?.attr("src") ?: img?.attr("data-src")
        return manga
    }

    // --- Latest ---
    override fun latestUpdatesRequest(page: Int) = GET("$baseUrl/", headers)
    override fun latestUpdatesSelector(): String = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element): SManga = popularMangaFromElement(element)

    // --- Search ---
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        // Try site search endpoint if exists, otherwise fallback to query param
        val searchUrl = "$baseUrl/?s=${java.net.URLEncoder.encode(query, "utf-8")}"
        return GET(searchUrl, headers)
    }
    override fun searchMangaSelector(): String = popularMangaSelector()
    override fun searchMangaFromElement(element: Element): SManga = popularMangaFromElement(element)

    // --- Details ---
    override fun mangaDetailsRequest(manga: SManga): Request = GET(manga.url, headers)
    override fun mangaDetailsParse(document: Document): SManga {
        val manga = SManga.create()
        // Title
        val title = document.selectFirst("h1, h2, .post-title, .entry-title")
        manga.title = title?.text()?.trim() ?: manga.title
        // Thumbnail
        val thumb = document.selectFirst(".post-thumbnail img, .thumb img, img")
        manga.thumbnail_url = thumb?.attr("src") ?: thumb?.attr("data-src")
        // Description
        val desc = document.selectFirst(".description, .entry-content, .post-content, .summary")
        manga.description = desc?.text()?.trim()
        // Author/Artist/Genre simple extraction
        val info = document.select(".post-meta, .meta, .info, .entry-meta").text()
        if (!info.isNullOrBlank()) {
            manga.author = info
            manga.artist = info
            manga.genre = info
        }
        return manga
    }

    // --- Chapters ---
    override fun chapterListSelector(): String = ".chapter, .entry-chapter, .chapters li, .chapters .chapter"
    override fun chapterFromElement(element: Element): SChapter {
        val chapter = SChapter.create()
        val a = element.selectFirst("a[href]")
        chapter.name = element.text().trim()
        chapter.setUrlWithoutDomain(a?.attr("href") ?: "")
        // try to parse date if present
        val dateElem = element.selectFirst("time, .date")
        chapter.date_upload = parseChapterDate(dateElem?.text())
        return chapter
    }
    override fun chapterListRequest(manga: SManga): Request = GET(manga.url, headers)

    // --- Pages ---
    override fun pageListRequest(chapter: SChapter): Request = GET(chapter.url, headers)
    override fun pageListParse(document: Document): List<Page> {
        val pages = mutableListOf<Page>()
        // try common patterns for images in chapter pages
        val imgs = document.select("img, .page img, .reader img")
        if (imgs.isNotEmpty()) {
            imgs.forEachIndexed { i, img ->
                val imgUrl = img.attr("src").ifEmpty { img.attr("data-src") }
                pages.add(Page(i, "", makeAbsoluteUrl(imgUrl)))
            }
            return pages
        }
        // fallback: try to extract scripts that contain image list (JSON)
        val scripts = document.select("script")
        scripts.forEach { s ->
            val text = s.data()
            // naive regex to find image urls in scripts
            val regex = "(https?:\\\\?/\\\\?/[^\\\"'\\s]+(jpg|jpeg|png|webp))".toRegex(RegexOption.IGNORE_CASE)
            regex.findAll(text).forEachIndexed { i, mr ->
                val url = mr.value.replace("\\/", "/")
                pages.add(Page(pages.size, "", makeAbsoluteUrl(url)))
            }
        }
        return pages
    }

    override fun imageUrlRequest(page: Page): Request = GET(page.imageUrl!!, headers)
    override fun imageUrlParse(document: Document): String = throw UnsupportedOperationException("Not used")

    // Utility
    private fun makeAbsoluteUrl(url: String?): String {
        if (url.isNullOrBlank()) return ""
        return if (url.startsWith("http")) url else "$baseUrl/${url.trimStart('/')}"
    }

    private fun parseChapterDate(dateStr: String?): Long {
        // Very simple date parse: try to parse epoch or ignore
        if (dateStr.isNullOrBlank()) return 0L
        return try {
            // try numbers inside string (YYYY or epoch)
            val digits = "\\d{4}-\\d{2}-\\d{2}".toRegex().find(dateStr)?.value
            if (digits != null) {
                val parts = digits.split("-")
                val y = parts[0].toInt()
                val m = parts[1].toInt()
                val d = parts[2].toInt()
                java.util.GregorianCalendar(y, m - 1, d).timeInMillis
            } else {
                0L
            }
        } catch (e: Exception) {
            0L
        }
    }

    companion object {
        const val PREF_BASE_URL = "prochan_base_url"
        const val DEFAULT_BASE_URL = "https://prochan.net"
    }
}
