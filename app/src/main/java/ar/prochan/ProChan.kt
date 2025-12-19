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

    // —————————————————————————————
    // طلبات الشبكة المطلوبة من ParsedHttpSource
    // —————————————————————————————

    override fun popularMangaRequest(page: Int): Request {
        // مثال: صفحات الشعبية
        // عدّل المسارات حسب موقعك الفعلي
        return Request.Builder()
            .url("$baseUrl/popular?page=$page")
            .build()
    }

    override fun latestUpdatesRequest(page: Int): Request {
        return Request.Builder()
            .url("$baseUrl/latest?page=$page")
            .build()
    }

    override fun searchMangaRequest(page: Int, query: String, filters: List<Filter>): Request {
        // إن كان الموقع يعتمد استعلامات GET للبحث
        val url = if (query.isBlank()) {
            "$baseUrl/search?page=$page"
        } else {
            "$baseUrl/search?q=${query.trim()}&page=$page"
        }
        return Request.Builder()
            .url(url)
            .build()
    }

    // —————————————————————————————
    // البحث
    // —————————————————————————————

    override fun searchMangaSelector(): String = "div.manga-item"

    override fun searchMangaFromElement(element: Element): SManga {
        return SManga.create().apply {
            title = element.selectFirst("h3.title")?.text().orEmpty()
            thumbnail_url = element.selectFirst("img")?.absUrl("src").ifEmpty {
                element.selectFirst("img")?.attr("src").orEmpty()
            }
            url = element.selectFirst("a")?.absUrl("href").ifEmpty {
                element.selectFirst("a")?.attr("href").orEmpty()
            }
        }
    }

    override fun searchMangaNextPageSelector(): String? = "a.next"

    // —————————————————————————————
    // قائمة الشعبية
    // —————————————————————————————

    override fun popularMangaSelector(): String = "div.manga-item"

    override fun popularMangaFromElement(element: Element): SManga = searchMangaFromElement(element)

    override fun popularMangaNextPageSelector(): String? = "a.next"

    // —————————————————————————————
    // آخر التحديثات
    // —————————————————————————————

    override fun latestUpdatesSelector(): String = "div.manga-item"

    override fun latestUpdatesFromElement(element: Element): SManga = searchMangaFromElement(element)

    override fun latestUpdatesNextPageSelector(): String? = "a.next"

    // —————————————————————————————
    // تفاصيل المانجا
    // —————————————————————————————

    override fun mangaDetailsParse(document: Document): SManga {
        return SManga.create().apply {
            title = document.selectFirst("h1.title")?.text().orEmpty()
            author = document.selectFirst("span.author")?.text()
            artist = document.selectFirst("span.artist")?.text()
            genre = document.select("div.genres a").joinToString { it.text() }.ifEmpty { null }
            description = document.selectFirst("div.description")?.text()
            thumbnail_url = document.selectFirst("img.cover")?.absUrl("src").ifEmpty {
                document.selectFirst("img.cover")?.attr("src")
            }
        }
    }

    // —————————————————————————————
    // قائمة الفصول
    // —————————————————————————————

    override fun chapterListSelector(): String = "ul.chapters li"

    override fun chapterFromElement(element: Element): SChapter {
        return SChapter.create().apply {
            name = element.selectFirst("a")?.text().orEmpty()
            url = element.selectFirst("a")?.absUrl("href").ifEmpty {
                element.selectFirst("a")?.attr("href").orEmpty()
            }
        }
    }

    // —————————————————————————————
    // الصفحات داخل الفصل
    // —————————————————————————————

    // إصلاح الخطأ السابق: الدالة يجب أن تكون موجودة بتوقيع صحيح
    override fun chapterPageParse(response: Response): List<Page> {
        val document = response.asJsoupSafe()
        return document.select("img.page-image").mapIndexed { index, element ->
            val imageUrl = element.absUrl("src").ifEmpty { element.attr("src") }
            Page(index, "", imageUrl)
        }
    }

    // في ParsedHttpSource، إمّا أن تعيد pageListParse أو imageUrlParse حسب الموقع
    override fun pageListParse(document: Document): List<Page> {
        return document.select("img.page-image").mapIndexed { index, element ->
            val imageUrl = element.absUrl("src").ifEmpty { element.attr("src") }
            Page(index, "", imageUrl)
        }
    }

    override fun imageUrlParse(document: Document): String {
        return document.selectFirst("img.page-image")?.absUrl("src")
            .ifEmpty { document.selectFirst("img.page-image")?.attr("src") }
            ?: ""
    }

    // —————————————————————————————
    // أدوات مساعدة لتفادي مشاكل asJsoup
    // —————————————————————————————

    private fun Response.asJsoupSafe(): Document {
        val bodyStr = this.body?.string().orEmpty()
        return Jsoup.parse(bodyStr)
    }
}
