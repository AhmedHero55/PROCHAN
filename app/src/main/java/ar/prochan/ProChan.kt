package ar.prochan

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Prochan : ParsedHttpSource() {

    override val name = "Prochan"
    override val baseUrl = "https://prochan.net"
    override val lang = "ar"
    override val supportsLatest = true

    // ✅ Popular Manga
    override fun popularMangaRequest(page: Int): Request {
        return Request.Builder()
            .url("$baseUrl/series/" + if (page > 1) "?page=$page" else "")
            .get()
            .build()
    }

    override fun popularMangaSelector() = "div.listupd div.bsx"

    override fun popularMangaFromElement(element: Element): SManga {
        return SManga.create().apply {
            title = element.select("a").attr("title")
            setUrlWithoutDomain(element.select("a").first()!!.attr("href"))
            thumbnail_url = element.select("img").let {
                if (it.hasAttr("data-src")) it.attr("abs:data-src") else it.attr("abs:src")
            }
        }
    }

    override fun popularMangaNextPageSelector() = "a[rel=next]"

    // ✅ Latest Updates
    override fun latestUpdatesRequest(page: Int): Request {
        return Request.Builder()
            .url(baseUrl + if (page > 1) "?page=$page" else "")
            .get()
            .build()
    }

    override fun latestUpdatesSelector() = "div.last-chapter div.box"

    override fun latestUpdatesFromElement(element: Element): SManga {
        return SManga.create().apply {
            val linkElement = element.select("div.info a")
            title = linkElement.select("h3").text()
            setUrlWithoutDomain(linkElement.first()!!.attr("href"))
            thumbnail_url = element.select("div.imgu img").first()!!.absUrl("src")
        }
    }

    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    override fun latestUpdatesParse(response: Response): MangasPage {
        val doc = Jsoup.parse(response.body!!.string())
        val mangas = doc.select(latestUpdatesSelector()).map { latestUpdatesFromElement(it) }
        val hasNextPage = doc.select(latestUpdatesNextPageSelector()).isNotEmpty()
        return MangasPage(mangas, hasNextPage)
    }

    // ✅ Search Manga
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return Request.Builder()
            .url("$baseUrl/ajax/search?keyword=$query")
            .get()
            .build()
    }

    override fun searchMangaSelector() = "li.list-group-item"

    override fun searchMangaFromElement(element: Element): SManga {
        return SManga.create().apply {
            val urlAndText = element.select("div.ms-2 a")
            title = urlAndText.text()
            setUrlWithoutDomain(urlAndText.first()!!.absUrl("href"))
            thumbnail_url = element.select("a img").first()!!.absUrl("src")
        }
    }

    override fun searchMangaNextPageSelector(): String? = null

    // ✅ Manga Details
    override fun mangaDetailsParse(document: Document): SManga {
        return SManga.create().apply {
            title = document.select("div.author-info-title h1").text()
            description = document.select("div.review-content").text()
            if (description.isNullOrBlank()) {
                description = document.select("div.review-content p").text()
            }
            genre = document.select("div.review-author-info a").joinToString { it.text() }
            thumbnail_url = document.select("div.text-right img").first()!!.absUrl("src")
            status = SManga.UNKNOWN
        }
    }

    // ✅ Chapters
    override fun chapterListSelector() = "div.chapter-card a"

    override fun chapterFromElement(element: Element): SChapter {
        return SChapter.create().apply {
            val chpNum = element.select("div.chapter-info div.chapter-number").text()
            val chpTitle = element.select("div.chapter-info div.chapter-title").text()
            name = if (chpNum.isBlank()) chpTitle else "$chpNum - $chpTitle"
            setUrlWithoutDomain(element.attr("href"))
        }
    }

    // ✅ Pages (للنسخ الحديثة من الـ API)
    override fun pageListParse(document: Document): List<Page> {
        return document.select("div.image_list canvas[data-src], div.image_list img[src]")
            .mapIndexed { i, el ->
                val url = if (el.hasAttr("src")) el.absUrl("src") else el.absUrl("data-src")
                Page(i, "", url)
            }
    }

    override fun imageUrlParse(document: Document): String {
        throw UnsupportedOperationException()
    }

    // ✅ Chapter Page Parse (للنسخ القديمة من الـ API)
    // إذا مكتبتك تطلب هذه الدالة، أبقها. إذا لا، احذفها.
    override fun chapterPageParse(document: Document): List<Page> {
        return document.select("div.image_list img[src], div.image_list canvas[data-src]")
            .mapIndexed { i, el ->
                val url = if (el.hasAttr("src")) el.absUrl("src") else el.absUrl("data-src")
                Page(i, "", url)
            }
    }
}
