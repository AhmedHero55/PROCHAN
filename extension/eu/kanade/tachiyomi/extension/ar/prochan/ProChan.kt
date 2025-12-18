package ar.prochan

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ProChan : ParsedHttpSource() {

    override val name = "ProChan"
    override val baseUrl = "https://prochan.net"
    override val lang = "ar"
    override val supportsLatest = true

    override fun popularMangaRequest(page: Int): Request =
        GET(baseUrl, headers)

    override fun popularMangaSelector() = "div.post"

    override fun popularMangaFromElement(element: Element): SManga =
        SManga.create().apply {
            title = element.select("h2 a").text()
            url = element.select("h2 a").attr("href")
            thumbnail_url = element.select("img").attr("src")
        }

    override fun popularMangaNextPageSelector() = null

    override fun latestUpdatesRequest(page: Int): Request =
        GET(baseUrl, headers)

    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element) =
        popularMangaFromElement(element)

    override fun latestUpdatesNextPageSelector() = null

    override fun searchMangaRequest(
        page: Int,
        query: String,
        filters: FilterList
    ): Request =
        GET("$baseUrl/?s=$query", headers)

    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaFromElement(element: Element) =
        popularMangaFromElement(element)

    override fun searchMangaNextPageSelector() = null

    override fun mangaDetailsParse(document: Document): SManga =
        SManga.create().apply {
            title = document.selectFirst("h1")?.text().orEmpty()
            description = document.select("div.description").text()
        }

    override fun chapterListSelector() = "li.chapter a"

    override fun chapterFromElement(element: Element): SChapter =
        SChapter.create().apply {
            name = element.text()
            url = element.attr("href")
        }

    override fun pageListParse(document: Document): List<Page> =
        document.select("img").mapIndexed { i, img ->
            Page(i, "", img.attr("src"))
        }

    override fun imageUrlParse(document: Document) = ""
}
