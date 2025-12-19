package ar.prochan

import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Prochan : ParsedHttpSource() {

    override val name = "ProChan"
    override val baseUrl = "https://prochan.org"
    override val lang = "ar"
    override val supportsLatest = true

    // ✅ البحث
    override fun searchMangaSelector(): String = "div.manga-item"
    override fun searchMangaFromElement(element: Element): SManga {
        return SManga.create().apply {
            title = element.select("h3.title").text()
            thumbnail_url = element.select("img").attr("src")
            url = element.select("a").attr("href")
        }
    }
    override fun searchMangaNextPageSelector(): String? = "a.next"

    // ✅ قائمة المانجا
    override fun popularMangaSelector(): String = "div.manga-item"
    override fun popularMangaFromElement(element: Element): SManga = searchMangaFromElement(element)
    override fun popularMangaNextPageSelector(): String? = "a.next"

    // ✅ أحدث المانجا
    override fun latestUpdatesSelector(): String = "div.manga-item"
    override fun latestUpdatesFromElement(element: Element): SManga = searchMangaFromElement(element)
    override fun latestUpdatesNextPageSelector(): String? = "a.next"

    // ✅ تفاصيل المانجا
    override fun mangaDetailsParse(document: Document): SManga {
        return SManga.create().apply {
            title = document.select("h1.title").text()
            author = document.select("span.author").text()
            artist = document.select("span.artist").text()
            genre = document.select("div.genres a").joinToString { it.text() }
            description = document.select("div.description").text()
            thumbnail_url = document.select("img.cover").attr("src")
        }
    }

    // ✅ الفصول
    override fun chapterListSelector(): String = "ul.chapters li"
    override fun chapterFromElement(element: Element): SChapter {
        return SChapter.create().apply {
            name = element.select("a").text()
            url = element.select("a").attr("href")
        }
    }

    // ✅ الصفحات داخل الفصل (حل مشكلة عدم وجود الدالة)
    override fun chapterPageParse(response: Response): List<Page> {
        val document = response.asJsoup()
        return document.select("img.page-image").mapIndexed { index, element ->
            Page(index, "", element.attr("src"))
        }
    }

    // ✅ قائمة الصفحات (متوافقة مع أي تغيير في chapterPageParse)
    override fun pageListParse(document: Document): List<Page> {
        return document.select("img.page-image").mapIndexed { index, element ->
            Page(index, "", element.attr("src"))
        }
    }

    // ✅ رابط الصورة
    override fun imageUrlParse(document: Document): String {
        return document.select("img.page-image").attr("src")
    }
}
