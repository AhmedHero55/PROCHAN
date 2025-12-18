package ar.prochan

import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.Page
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Prochan : ParsedHttpSource() {
    override val name = "Prochan"
    override val baseUrl = "https://prochan.net"
    override val lang = "ar"
    override val supportsLatest = true

    // Popular
    override fun popularMangaRequest(page: Int): Request =
        GET("$baseUrl/series?sort=popular&page=$page")

    override fun popularMangaSelector(): String = ".manga-card, .grid > a" // عدّل حسب القائمة الفعلية

    override fun popularMangaFromElement(element: Element): SManga {
        val title = element.selectFirst(".title, h3")?.text().orEmpty()
        val url = element.selectFirst("a")?.attr("href").orEmpty()
        val thumb = element.selectFirst("img")?.absUrl("src")
        return SManga.create().apply {
            this.title = title
            this.url = url
            this.thumbnail_url = thumb
        }
    }

    override fun popularMangaNextPageSelector(): String? = ".pagination .next:not(.disabled)"

    // Latest
    override fun latestUpdatesRequest(page: Int): Request =
        GET("$baseUrl/series?sort=latest&page=$page")

    override fun latestUpdatesSelector(): String = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element): SManga = popularMangaFromElement(element)
    override fun latestUpdatesNextPageSelector(): String? = popularMangaNextPageSelector()

    // Search
    override fun searchMangaRequest(page: Int, query: String, filters: List<Filter>): Request =
        GET("$baseUrl/search?q=$query&page=$page")

    override fun searchMangaSelector(): String = popularMangaSelector()
    override fun searchMangaFromElement(element: Element): SManga = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector(): String? = popularMangaNextPageSelector()

    // Details (من صورك)
    override fun mangaDetailsParse(document: Document): SManga {
        val title = document.selectFirst("h1.text-3xl")?.text().orEmpty()
        val altTitle = document.selectFirst("p.text-muted-foreground")?.text()
        val thumbnail = document.selectFirst("img.rounded-xl")?.absUrl("src")

        val statusText = document.selectFirst(".status, [data-status]")?.text()?.lowercase()
        val status = when {
            statusText?.contains("مستمر") == true || statusText?.contains("ongoing") == true -> SManga.ONGOING
            statusText?.contains("مكتملة") == true || statusText?.contains("completed") == true -> SManga.COMPLETED
            else -> SManga.UNKNOWN
        }

        val genre = document.select(".genres a, .tags a").eachText().joinToString()

        return SManga.create().apply {
            this.title = title
            this.author = document.selectFirst(".author")?.text()
            this.artist = document.selectFirst(".artist")?.text()
            this.genre = genre
            this.status = status
            this.description = document.selectFirst(".description")?.text() ?: altTitle
            this.thumbnail_url = thumbnail
        }
    }

    // Chapters (من لقطة الفصول)
    override fun chapterListSelector(): String = ".chapter-list .chapter, [data-chapter]"

    override fun chapterFromElement(element: Element): SChapter {
        val name = element.selectFirst("span.text-sm")?.text().orEmpty()
        val url = element.selectFirst("a")?.attr("href").orEmpty()
        val dateText = element.selectFirst("span.text-xs")?.text()

        return SChapter.create().apply {
            this.name = name
            this.url = url
            this.date_upload = parseRelativeDate(dateText)
        }
    }

    // Pages (من لقطة الفصل)
    override fun pageListParse(document: Document): List<Page> {
        val images = document.select("img.object-contain")
        return images.mapIndexed { index, img ->
            val imageUrl = img.absUrl("src")
            Page(index, document.location(), imageUrl)
        }
    }

    override fun imageUrlParse(document: Document): String = ""

    // Helpers
    private fun parseRelativeDate(text: String?): Long {
        if (text.isNullOrBlank()) return 0L
        val now = System.currentTimeMillis()
        val lower = text.lowercase()
        // أمثلة: "منذ يوم واحد", "منذ 23 يوم", "منذ ساعة", "منذ 3 ساعات", "منذ دقيقة"
        return try {
            when {
                lower.contains("دقيقة") -> now - 60_000L
                lower.contains("ساعت") -> {
                    val n = "\\d+".toRegex().find(lower)?.value?.toIntOrNull() ?: 1
                    now - n * 3_600_000L
                }
                lower.contains("يوم") -> {
                    val n = "\\d+".toRegex().find(lower)?.value?.toIntOrNull() ?: 1
                    now - n * 86_400_000L
                }
                lower.contains("أسبوع") || lower.contains("اسبوع") -> {
                    val n = "\\d+".toRegex().find(lower)?.value?.toIntOrNull() ?: 1
                    now - n * 7 * 86_400_000L
                }
                lower.contains("شهر") -> {
                    val n = "\\d+".toRegex().find(lower)?.value?.toIntOrNull() ?: 1
                    now - n * 30L * 86_400_000L
                }
                else -> 0L
            }
        } catch (e: Exception) { 0L }
    }
}
