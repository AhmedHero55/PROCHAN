package ar.prochan

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ProChan : ParsedHttpSource() {

    override val name = "ProChan"
    override val baseUrl = "https://prochan.net"
    override val lang = "ar"
    override val supportsLatest = true

    override fun popularMangaRequest(page: Int): Request = GET(baseUrl, headers)
    override fun popularMangaSelector() = "div.post, article.post"
    override fun popularMangaFromElement(element: Element): SManga {
        return SManga.create().apply {
            title = element.selectFirst("h2 a, h1 a")?.text()?.trim().orEmpty()
            url = element.selectFirst("h2 a, h1 a")?.attr("href").orEmpty()
            thumbnail_url = element.selectFirst("img")?.attr("src")
        }
    }
    override fun popularMangaNextPageSelector() = "a.next, a.nextpostslink"

    override fun latestUpdatesRequest(page: Int): Request = GET(baseUrl, headers)
    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)
    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return GET("$baseUrl/?s=$query", headers)
    }
    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaFromElement(element: Element) = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector() = popularMangaNextPageSelector()

    override fun mangaDetailsParse(document: Document): SManga {
        return SManga.create().apply {
            title = document.selectFirst("h1.entry-title, h1.post-title, h1")?.text()?.trim().orEmpty()
            description = document.select("div.entry-content, div.post-content, article").text().trim()
            thumbnail_url = document.selectFirst("article img, .entry-content img, img")?.attr("src")
        }
    }

    override fun chapterListSelector() = "div.entry-content a, article a"
    override fun chapterFromElement(element: Element): SChapter {
        return SChapter.create().apply {
            name = element.text().trim().ifEmpty {
                element.attr("href").substringAfterLast('/').ifEmpty { "Chapter" }
            }
            url = element.attr("href")
        }
    }

    override fun pageListParse(document: Document): List<Page> {
        val imgs = document.select("div.entry-content img, article img, img")
        return imgs.mapIndexed { i, img -> Page(i, "", img.attr("src")) }
    }

    override fun imageUrlParse(document: Document) = ""

    override fun chapterPageParse(response: Response): SChapter {
        return SChapter.create().apply {
            name = "Chapter"
            url = response.request.url.toString()
        }
    }
}
