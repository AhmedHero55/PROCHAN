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
    override val baseUrl = "https://prochan.example"
    override val lang = "ar"
    override val supportsLatest = true

    // ✅ Popular
    override fun popularMangaRequest(page: Int): Request {
        return Request.Builder()
            .url("$baseUrl/series?page=$page".toHttpUrl())
            .get()
            .build()
    }

    override fun popularMangaSelector() = "div.listupd div.bsx"

    override fun popularMangaFromElement(element: Element): SManga {
        return SManga.create().apply {
            title = element.select("a").attr("title")
            setUrlWithoutDomain(element.select("a").first()!!.attr("href"))
            thumbnail_url = element.select("img").attr("abs:src")
        }
    }

    override fun popularMangaNextPageSelector() = "a[rel=next]"

    // ✅ Search
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return Request.Builder()
            .url("$baseUrl/ajax/search?keyword=$query".toHttpUrl())
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

    // ✅ Details
    override fun mangaDetailsParse(document: Document): SManga {
        return SManga.create().apply {
            title = document.select("div.author-info-title h1").text()
            description = document.select("div.review-content").text()
            thumbnail_url = document.select("div.text-right img").first()!!.absUrl("src")
            status = SManga.UNKNOWN
        }
    }

    // ✅ Chapters
    override fun chapterListSelector() = "div.chapter-card a"

    override fun chapterFromElement(element: Element): SChapter {
        return SChapter.create().apply {
            name = element.text()
            setUrlWithoutDomain(element.attr("href"))
        }
    }

    // ✅ Pages
    override fun pageListParse(document: Document): List<Page> {
        return document.select("div.image_list img[src]").mapIndexed { i, el ->
            Page(i, "", el.absUrl("src"))
        }
    }

    override fun imageUrlParse(document: Document): String {
        throw UnsupportedOperationException()
    }
}
