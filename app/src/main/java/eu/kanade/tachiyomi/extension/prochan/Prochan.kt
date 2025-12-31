package eu.kanade.tachiyomi.extension.prochan

import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Prochan : ParsedHttpSource() {
    override val name = "ProChan"
    override val baseUrl = "https://prochan.net"
    override val lang = "ar"
    override val supportsLatest = true

    // Popular
    override fun popularMangaRequest(page: Int): Request =
        Request.Builder().url("$baseUrl/popular?page=$page").build()

    override fun popularMangaSelector(): String = "div.manga-item"
    override fun popularMangaFromElement(element: Element): SManga = searchMangaFromElement(element)
    override fun popularMangaNextPageSelector(): String? = "a.next"

    // Latest
    override fun latestUpdatesRequest(page: Int): Request =
        Request.Builder().url("$baseUrl/latest?page=$page").build()

    override fun latestUpdatesSelector(): String = "div.manga-item"
    override fun latestUpdatesFromElement(element: Element): SManga = searchMangaFromElement(element)
    override fun latestUpdatesNextPageSelector(): String? = "a.next"

    // Search
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val url = if (query.isBlank()) {
            "$baseUrl/search?page=$page"
        } else {
            "$baseUrl/search?q=${query.trim()}&page=$page"
        }
        return Request.Builder().url(url).build()
    }

    override fun searchMangaSelector(): String = "div.manga-item"

    override fun searchMangaFromElement(element: Element): SManga =
        SManga.create().apply {
            title = element.selectFirst("h3.title")?.text().orEmpty()
            thumbnail_url = element.selectFirst("img")?.absUrl("src")
            url = element.selectFirst("a")?.attr("href").orEmpty() // رابط نسبي
        }

    override fun searchMangaNextPageSelector(): String? = "a.next"

    // Details
    override fun mangaDetailsParse(document: Document): SManga =
        SManga.create().apply {
            title = document.selectFirst("h1.title")?.text().orEmpty()
            author = document.selectFirst("span.author")?.text()
            artist = document.selectFirst("span.artist")?.text()
            genre = document.select("div.genres a").joinToString { it.text() }
            description = document.selectFirst("div.description")?.text()
            thumbnail_url = document.selectFirst("img.cover")?.absUrl("src")
        }

    // Chapters
    override fun chapterListSelector(): String = "ul.chapters li"

    override fun chapterFromElement(element: Element): SChapter =
        SChapter.create().apply {
            name = element.selectFirst("a")?.text().orEmpty()
            url = element.selectFirst("a")?.attr("href").orEmpty() // رابط نسبي
        }

    // Pages
    override fun pageListParse(document: Document): List<Page> =
        document.select("img.page-image").mapIndexed { index, img ->
            Page(index, "", img.absUrl("src"))
        }

    override fun imageUrlParse(document: Document): String =
        document.selectFirst("img.page-image")?.absUrl("src").orEmpty()
}
