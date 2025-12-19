package ar.prochan

import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.MangasPage
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl

class Prochan : ParsedHttpSource() {

    override val name = "Prochan"
    override val baseUrl = "https://prochan.example"
    override val lang = "en"
    override val supportsLatest = true

    // ✅ Popular Manga
    override fun popularMangaRequest(page: Int): Request {
        return Request.Builder()
            .url("$baseUrl/popular?page=$page".toHttpUrl())
            .get()
            .build()
    }

    override fun popularMangaParse(response: Response): MangasPage {
        return MangasPage(emptyList(), false)
    }

    // ✅ Search Manga
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return Request.Builder()
            .url("$baseUrl/search?query=$query&page=$page".toHttpUrl())
            .get()
            .build()
    }

    override fun searchMangaParse(response: Response): MangasPage {
        return MangasPage(emptyList(), false)
    }

    // ✅ Manga Details (كان ناقص)
    override fun mangaDetailsParse(response: Response): SManga {
        val manga = SManga.create()
        manga.title = "Placeholder Title"
        manga.author = "Unknown"
        manga.artist = "Unknown"
        manga.status = SManga.UNKNOWN
        manga.description = "No description available"
        manga.thumbnail_url = "$baseUrl/placeholder.png"
        return manga
    }

    // ✅ Chapter List
    override fun chapterListParse(response: Response): List<SChapter> {
        return emptyList()
    }

    // ✅ Page List
    override fun pageListParse(response: Response): List<Page> {
        return emptyList()
    }

    // ✅ Latest Updates
    override fun latestUpdatesRequest(page: Int): Request {
        return Request.Builder()
            .url("$baseUrl/latest?page=$page".toHttpUrl())
            .get()
            .build()
    }

    override fun latestUpdatesParse(response: Response): MangasPage {
        return MangasPage(emptyList(), false)
    }
}
