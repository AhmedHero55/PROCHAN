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

    // ----------------------
    // Popular / Latest
    // ----------------------
    override fun popularMangaRequest(page: Int): Request =
        GET(baseUrl, headers)

    override fun popularMangaSelector() =
        "body > div.flex.flex-col > div > div.flex-1.flex.flex-col.w-full.max-w-full.overflow-x-hidden > main > div > div.grid.grid-cols-2.sm\\:grid-cols-3.md\\:grid-cols-4.lg\\:grid-cols-6.gap-4 > a"

    override fun popularMangaFromElement(element: Element): SManga =
        SManga.create().apply {
            title = element.select("h3").text()
            url = element.attr("href")
            thumbnail_url = element.select("img").attr("src")
        }

    override fun popularMangaNextPageSelector(): String? = null

    override fun latestUpdatesRequest(page: Int): Request = popularMangaRequest(page)
    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)
    override fun latestUpdatesNextPageSelector(): String? = null

    // ----------------------
    // Search
    // ----------------------
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request =
        GET("$baseUrl/?s=$query", headers)

    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaFromElement(element: Element) = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector(): String? = null

    // ----------------------
    // Manga Details
    // ----------------------
    override fun mangaDetailsParse(document: Document): SManga =
        SManga.create().apply {
            title = document.selectFirst(
                "body > div.flex.flex-col > div > div.flex-1.flex.flex-col.w-full.max-w-full.overflow-x-hidden > main > div > div > section.relative.overflow-hidden.rounded-3xl.border.border-border\\/60.bg-card\\/80.p-4.shadow-lg.shadow-black\\/5.backdrop-blur-sm.sm\\:p-6 > div.flex.flex-col.gap-6.min-\\[720px\\]\\:flex-row.min-\\[720px\\]\\:items-start.min-\\[720px\\]\\:flex-row-reverse > div.flex-1.space-y-6 > div.space-y-4 > div.flex.flex-col.gap-3.min-\\[720px\\]\\:flex-row.min-\\[720px\\]\\:items-start.min-\\[720px\\]\\:justify-between > div > h1"
            )?.text().orEmpty()
            thumbnail_url = document.selectFirst(
                "body > div.flex.flex-col > div > div.flex-1.flex.flex-col.w-full.max-w-full.overflow-x-hidden > main > div > div > section.relative.overflow-hidden.rounded-3xl.border.border-border\\/60.bg-card\\/80.p-4.shadow-lg.shadow-black\\/5.backdrop-blur-sm.sm\\:p-6 > div.flex.flex-col.gap-6.min-\\[720px\\]\\:flex-row.min-\\[720px\\]\\:items-start.min-\\[720px\\]\\:flex-row-reverse > div.relative.mx-auto.w-full.max-w-xs.flex-shrink-0.overflow-visible.min-\\[720px\\]\\:mx-0.min-\\[720px\\]\\:w-72.min-\\[720px\\]\\:max-w-none.xl\\:w-80 > div.relative.overflow-hidden.rounded-2xl.border.border-border\\/60.bg-muted\\/20.shadow-inner.shadow-black\\/5 > div > img"
            )?.attr("src")
            description = document.select("div.description").text()
        }

    // ----------------------
    // Chapters List
    // ----------------------
    override fun chapterListSelector() =
        "#radix-\\:r45\\:-content-chapters > div:nth-child(2) > div > div:nth-child(2) > div.space-y-2.ltr\\:ml-4.rtl\\:mr-4.ltr\\:border-l.rtl\\:border-r.ltr\\:pl-4.rtl\\:pr-4 > div > div > a"

    override fun chapterFromElement(element: Element): SChapter =
        SChapter.create().apply {
            name = element.text()
            url = element.attr("href")
        }

    // ----------------------
    // Page List (Images)
    // ----------------------
    override fun pageListParse(document: Document): List<Page> =
        document.select(
            "body > div.flex.flex-col > div > div.flex-1.flex.flex-col.w-full.max-w-full.overflow-x-hidden > main > div > div.-mx-4.md\\:mx-0 > div > div.rounded-lg.border.px-2.py-4.sm\\:px-4.sm\\:py-6.transition-colors.mx-auto.w-full.max-w-4xl.bg-background.text-foreground > div > div img"
        ).mapIndexed { i, img ->
            Page(i, "", img.attr("src"))
        }

    override fun imageUrlParse(document: Document) = ""
}
