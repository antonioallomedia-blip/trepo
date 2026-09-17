package com.yourname

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.utils.AppUtils.tryParseJson
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * HanimeTV provider for CloudStream.
 *
 * Uses the official Hanime.tv API (v8) for reliable data retrieval:
 *  - Search:        https://hanime.tv/api/v8/search?q={query}
 *  - Video detail:  https://hanime.tv/api/v8/video?id={slug}
 *
 * All network requests are made via the `app` helper (OkHttp wrapper)
 * that CloudStream injects into every MainAPI subclass.
 */
class HanimeTV : MainAPI() {

    override var mainUrl = "https://hanime.tv"
    override var name = "HanimeTV"
    override val hasMainPage = true
    override val hasQuickSearch = false
    override val supportedTypes = setOf(TvType.Movie)

    override val lang = "en"

    // ------------------------------------------------------------------
    //  HOME PAGE
    // ------------------------------------------------------------------

    /**
     * Returns the home‑page sections displayed when the user opens the
     * provider. We use the `/api/v8/video` endpoint with `trending` as a
     * pseudo‑query to fetch currently trending videos.
     */
    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val trending = app.get("$mainUrl/api/v8/video?trending=day&page=$page")
            .parsedSafe<HanimeTrendingResponse>()

        val items = trending?.results?.map { video ->
            newMovieSearchResponse(video.name, video.slug, TvType.Movie) {
                this.posterUrl = video.coverUrl
            }
        } ?: emptyList()

        return newHomePageResponse(
            listOf(
                HomePageList(
                    name = "Trending Now",
                    list = items,
                    isHorizontalImages = false
                )
            ),
            hasNext = (trending?.nextPage != null)
        )
    }

    // ------------------------------------------------------------------
    //  SEARCH
    // ------------------------------------------------------------------

    /**
     * Search Hanime.tv via their public search API.
     *
     * @param query The raw search string entered by the user.
     * @return A list of SearchResponse objects or null on failure.
     */
    override suspend fun search(query: String): List<SearchResponse>? {
        val url = "$mainUrl/api/v8/search?q=${query.trim()}"
        val response = app.get(url).parsedSafe<HanimeSearchResponse>()

        return response?.hits?.map { hit ->
            newMovieSearchResponse(
                name = hit.name,
                url = hit.slug,
                type = TvType.Movie
            ) {
                this.posterUrl = hit.coverUrl
            }
        }
    }

    // ------------------------------------------------------------------
    //  LOAD (DETAIL PAGE)
    // ------------------------------------------------------------------

    /**
     * Load detailed information for a single video.
     *
     * @param url The slug of the video (e.g. "overflow").
     */
    override suspend fun load(url: String): LoadResponse? {
        val apiUrl = "$mainUrl/api/v8/video?id=$url"
        val video = app.get(apiUrl).parsedSafe<HanimeVideoResponse>()?.hentaiVideo
            ?: return null

        return newMovieLoadResponse(
            name = video.name,
            url = url,
            type = TvType.Movie,
            dataUrl = url
        ) {
            this.posterUrl = video.coverUrl
            this.plot = video.description?.replace(Regex("<[^>]*>"), "")?.trim()
            this.year = video.releaseDate?.take(4)?.toIntOrNull()
            this.tags = video.tags?.map { it.text }
        }
    }

    // ------------------------------------------------------------------
    //  LOAD LINKS (VIDEO SOURCES)
    // ------------------------------------------------------------------

    /**
     * Extract playable video URLs from the Hanime API response.
     *
     * The API returns a `videos_manifest` object containing servers and
     * streams. We iterate over all available qualities and pass them to
     * the CloudStream callback.
     */
    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val apiUrl = "$mainUrl/api/v8/video?id=$data"
        val response = app.get(apiUrl).parsedSafe<HanimeVideoResponse>()
            ?: return false

        val streams = response.videosManifest?.servers
            ?.flatMap { it.streams }
            ?.filter { it.url.isNotBlank() }
            ?: return false

        streams.forEach { stream ->
            val qualityLabel = when (stream.height) {
                1080 -> "1080p"
                720  -> "720p"
                480  -> "480p"
                360  -> "360p"
                else -> "${stream.height}p"
            }

            callback.invoke(
                newExtractorLink(
                    source = this.name,
                    name = "$name - $qualityLabel",
                    url = stream.url,
                    type = ExtractorLinkType.M3U8   // Hanime streams are HLS (.m3u8)
                ) {
                    this.quality = when (stream.height) {
                        1080 -> Qualities.P1080.value
                        720  -> Qualities.P720.value
                        480  -> Qualities.P480.value
                        360  -> Qualities.P360.value
                        else -> Qualities.Unknown.value
                    }
                    this.referer = mainUrl
                }
            )
        }

        return streams.isNotEmpty()
    }
}

// ======================================================================
//  DATA MODELS (Jackson / kotlinx.serialization compatible)
// ======================================================================

/**
 * Response wrapper for `/api/v8/video?trending=...`.
 */
data class HanimeTrendingResponse(
    @JsonProperty("hentai_videos") val results: List<HanimeTrendingItem>? = null,
    @JsonProperty("next_page")     val nextPage: String? = null
)

data class HanimeTrendingItem(
    @JsonProperty("name")       val name: String,
    @JsonProperty("slug")       val slug: String,
    @JsonProperty("cover_url")  val coverUrl: String?
)

/**
 * Response wrapper for `/api/v8/search?q=...`.
 */
data class HanimeSearchResponse(
    @JsonProperty("hits") val hits: List<HanimeSearchHit>? = null
)

data class HanimeSearchHit(
    @JsonProperty("name")       val name: String,
    @JsonProperty("slug")       val slug: String,
    @JsonProperty("cover_url")  val coverUrl: String?
)

/**
 * Full response wrapper for `/api/v8/video?id=...`.
 */
data class HanimeVideoResponse(
    @JsonProperty("hentai_video")   val hentaiVideo: HanimeVideo? = null,
    @JsonProperty("videos_manifest") val videosManifest: HanimeManifest? = null
)

data class HanimeVideo(
    @JsonProperty("name")         val name: String,
    @JsonProperty("description")  val description: String? = null,
    @JsonProperty("cover_url")    val coverUrl: String? = null,
    @JsonProperty("release_date") val releaseDate: String? = null,
    @JsonProperty("tags")         val tags: List<HanimeTag>? = null
)

data class HanimeTag(
    @JsonProperty("text") val text: String
)

data class HanimeManifest(
    @JsonProperty("servers") val servers: List<HanimeServer>? = null
)

data class HanimeServer(
    @JsonProperty("streams") val streams: List<HanimeStream>? = null
)

data class HanimeStream(
    @JsonProperty("url")    val url: String,
    @JsonProperty("height") val height: Int,
    @JsonProperty("size_mbs") val sizeMbs: Double? = null
)
