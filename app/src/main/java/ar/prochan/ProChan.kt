package ar.prochan

import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.Page
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class ProChan : Source {

    private val client = OkHttpClient()

    override val id: Long = 1234567890
    override val name: String = "ProChan"
    override val lang: String = "ar"
    override val supportsLatest: Boolean = true

    override fun popularMangaRequest(page: Int): Request {
        return Request.Builder()
            .url("https://prochan.example.com/popular?page=$page")
            .build()
    }

    override fun popularMangaParse(response: okhttp3.Response): List<SManga> {
        val document = Jsoup.parse(response.body?.string())
        val mangas = mutableListOf<SManga>()

        document.select("div.manga-item").forEach { element ->
            val manga = SManga.create()
            manga.title = element.select("h3.title").text()
            manga.description = element.select("p.description").text()
            manga.thumbnail_url = element.select("img.cover").attr("src")
            manga.url = element.select("a").attr("href")
            mangas.add(manga)
        }

        return mangas
    }

    override fun chapterListParse(response: okhttp3.Response): List<SChapter> {
        val document = Jsoup.parse(response.body?.string())
        val chapters = mutableListOf<SChapter>()

        document.select("div.chapter-item").forEach { element ->
            val chapter = SChapter.create()
            chapter.name = element.select("span.chapter-title").text()
            chapter.url = element.select("a").attr("href")
            chapters.add(chapter)
        }

        return chapters
    }

    override fun pageListParse(response: okhttp3.Response): List<Page> {
        val document = Jsoup.parse(response.body?.string())
        val pages = mutableListOf<Page>()

        document.select("img.page-image").forEachIndexed { index, element ->
            val imageUrl = element.attr("src")
            pages.add(Page(index, "", imageUrl))
        }

        return pages
    }
}
