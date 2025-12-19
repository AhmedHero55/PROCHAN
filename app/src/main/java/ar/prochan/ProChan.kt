package ar.prochan

import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Prochan : ParsedHttpSource() {

    override val name = "ProChan"
    override val baseUrl = "https://prochan.org"
    override val lang = "ar"
    override val supportsLatest = true

    // ✅ طلبات الشبكة
    override fun popularMangaRequest(page: Int): Request =
        Request.Builder().url("$baseUrl/popular?page=$page").build()

    override fun latestUpdatesRequest(page: Int): Request =
        Request.Builder().url("$baseUrl/latest?page=$page").build()

    override fun searchMangaRequest(page: Int, query: String): Request {
        val url = if (query.isBlank()) {
            "$baseUrl/search?page=$page"
        } else {
            "$baseUrl/search?q=${query.trim()}&page=$page"
        }
        return Request.Builder().url(url).build()
    }

    // ✅ البحث
    override fun searchMangaSelector(): String = "div.manga-item"
    override fun searchMangaFromElement(element: Element): SManga {
        return SManga.create().apply {
            title = element.selectFirst("h3.title")?.text() ?: ""
            thumbnail_url = element.selectFirst("img")?.attr("src") ?: ""
            url = element.selectFirst("a")?.attr("href") ?: ""
        }
    }
    override fun searchMangaNextPageSelector(): String? = "a.next"

    // ✅ الشعبية
    override fun popularMangaSelector(): String = "div.manga-item"
    override fun popularMangaFromElement(element: Element): SManga = searchMangaFromElement(element)
    override fun popularMangaNextPageSelector(): String? = "a.next"

    // ✅ آخر التحديثات
    override fun latestUpdatesSelector(): String = "div.manga-item"
    override fun latestUpdatesFromElement(element: Element): SManga = searchMangaFromElement(element)
    override fun latestUpdatesNextPageSelector(): String? = "a.next"

    // ✅ تفاصيل المانجا
    override fun mangaDetailsParse(document: Document): SManga {
        return SManga.create().apply {
            title = document.selectFirst("h1.title")?.text() ?: ""
            author = document.selectFirst("span.author")?.text()
            artist = document.selectFirst("span.artist")?.text()
            genre = document.select("div.genres a").joinToString { it.text() }
            description = document.selectFirst("div.description")?.text()
            thumbnail_url = document.selectFirst("img.cover")?.attr("src")
        }
    }

    // ✅ قائمة الفصول
    override fun chapterListSelector(): String = "ul.chapters li"
    override fun chapterFromElement(element: Element): SChapter {
        return SChapter.create().apply {
            name = element.selectFirst("a")?.text() ?: ""
            url = element.selectFirst("a")?.attr("href") ?: ""
        }
    }

    // ✅ الدالة المطلوبة من الـ AAR
    override fun chapterPageParse(response: Response): SChapter {
        val document = response.asJsoupSafe()
        return SChapter.create().apply {
            name = document.selectFirst("h1.chapter-title")?.text() ?: ""
            url = response.request.url.toString()
        }
    }

    // ✅ الصفحات
    override fun pageListParse(document: Document): List<Page> {
        return document.select("img.page-image").mapIndexed { index, element ->
            Page(index, "", element.attr("src"))
        }
    }

    override fun imageUrlParse(document: Document): String {
        return document.selectFirst("img.page-image")?.attr("src") ?: ""
    }

    // ✅ أداة مساعدة
    private fun Response.asJsoupSafe(): Document {
        val bodyStr = this.body?.string() ?: ""
        return Jsoup.parse(bodyStr)
    }
}
