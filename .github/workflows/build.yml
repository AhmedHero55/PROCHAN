package ar.prochan

import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import okhttp3.Response

class Prochan : ParsedHttpSource() {

    override val name = "Prochan"
    override val baseUrl = "https://prochan.example"
    override val lang = "en"
    override val supportsLatest = true

    // ✅ Popular Manga
    override fun popularMangaRequest(page: Int): Request {
        return GET("$baseUrl/popular?page=$page")
    }

    override fun popularMangaParse(response: Response): MangasPage {
        // TODO: parse popular manga list
        return MangasPage(emptyList(), false)
    }

    // ✅ Search Manga
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return GET("$baseUrl/search?query=$query&page=$page")
    }

    override fun searchMangaParse(response: Response): MangasPage {
        // TODO: parse search results
        return MangasPage(emptyList(), false)
    }

    // ✅ Manga Details
    override fun mangaDetailsParse(response: Response): SManga {
        // TODO: parse manga details
        return SManga.create()
    }

    // ✅ Chapter List
    override fun chapterListParse(response: Response): List<SChapter> {
        // TODO: parse chapters
        return emptyList()
    }

    // ✅ Page List
    override fun pageListParse(response: Response): List<Page> {
        // TODO: parse pages
        return emptyList()
    }

    // ✅ Latest Updates
    override fun latestUpdatesRequest(page: Int): Request {
        return GET("$baseUrl/latest?page=$page")
    }

    override fun latestUpdatesParse(response: Response): MangasPage {
        // TODO: parse latest updates
        return MangasPage(emptyList(), false)
    }
}
