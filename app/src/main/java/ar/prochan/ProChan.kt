package ar.prochan

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Prochan : ParsedHttpSource() {

    override val name = "Prochan"
    override val baseUrl = "https://prochan.net"
    override val lang = "ar"
    override val supportsLatest = true

    // ✅ Popular Manga
    override fun popularMangaRequest(page: Int): Request {
        val url = "$baseUrl/series/" + if (page > 1) "?page=$page" else ""
        return Request.Builder().url(url).get().build()
    }

    override fun popularMangaSelector() = "div.listupd div.bsx"

    override fun popularMangaFromElement(element: Element): SManga {
        return SManga.create().apply {
            title = element.select("a").attr("title")
            setUrlWithoutDomain(element.selectFirst("a")!!.attr("href"))
            val img = element.selectFirst("img")!!
            thumbnail_url = if (img.hasAttr("data-src")) img.attr("abs:data-src") else img.attr("abs:src")
        }
    }

    override fun popularMangaNextPageSelector() = "a[rel=next]"

    // ✅ Latest Updates
    override fun latestUpdatesRequest(page: Int): Request {
        val url = baseUrl + if (page > 1) "?page=$page" else ""
        return Request.Builder().url(url).get().build()
    }

    override fun latestUpdatesSelector() = "div.last-chapter div.box"

    override fun latestUpdatesFromElement(element: Element): SManga {
        return SManga.create().apply {
            val link = element.selectFirst("div.info a")!!
            title = link.selectFirst("h3")!!.text()
            setUrlWithoutDomain(link.attr("href"))
            thumbnail_url = element.selectFirst("div.imgu img")!!.absUrl("src")
        }
    }

    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    // ✅ Search Manga
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val url = "$baseUrl/ajax/search?keyword=$query"
        return Request.Builder().url(url).get().build()
    }

    override fun searchMangaSelector() = "li.list-group-item"

    override fun searchMangaFromElement(element: Element): SManga {
        return SManga.create().apply {
            val a = element.selectFirst("div.ms-2 a")!!
            title = a.text()
            setUrlWithoutDomain(a.absUrl("href"))
            thumbnail_url = element.selectFirst("a img")!!.absUrl("src")
        }
    }

    override fun searchMangaNextPageSelector(): String? = null

    // ✅ Manga Details
    override fun mangaDetailsParse(document: Document): SManga {
        return SManga.create().apply {
            title = document.select("div.author-info-title h1").text()
            description = document.select("div.review-content").let { el ->
                val txt = el.text()
                if (txt.isNullOrBlank()) document.select("div.review-content p").text() else txt
            }
            genre = document.select("div.review-author-info a").joinToString { it.text() }
            thumbnail_url = document.selectFirst("div.text-right img")!!.absUrl("src")
            status = SManga.UNKNOWN
        }
    }

    // ✅ Chapters
    override fun chapterListSelector() = "div.chapter-card a"

    override fun chapterFromElement(element: Element): SChapter {
        val num = element.select("div.chapter-info div.chapter-number").text()
        val title = element.select("div.chapter-info div.chapter-title").text()
        return SChapter.create().apply {
            name = if (num.isBlank()) title else "$num - $title"
            setUrlWithoutDomain(element.attr("href"))
        }
    }

    // ✅ Pages (كل الصفحات)
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

    // ✅ Chapter Page Parse (صفحة واحدة فقط حسب الـ API)
    override fun chapterPageParse(document: Document): Page {
        val img = document.select("div.image_list img[src], div.image_list canvas[data-src]").firstOrNull()
        val url = when {
            img == null -> ""
            img.hasAttr("src") -> img.absUrl("src")
            else -> img.absUrl("data-src")
        }
        return Page(0, "", url)
    }
}
