package lavalink.server.io

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import lavalink.server.player.track.processing.*
import lavalink.server.util.Util
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.json.JSONArray
import org.json.JSONObject
import org.slf4j.LoggerFactory

import java.util.concurrent.CompletionStage
import java.io.IOException

@RestController
class TrackRestHandler(private val audioPlayerManager: AudioPlayerManager) {
    companion object {
        private val log = LoggerFactory.getLogger(TrackRestHandler::class.java)
    }

    @GetMapping(value = ["/loadtracks"], produces = ["application/json"])
    @ResponseBody
    fun getLoadTracks(@RequestParam identifier: String): CompletionStage<ResponseEntity<String>> {
        log.info("Got request to load for identifier \"{}\"", identifier);

        return AudioLoader(audioPlayerManager).load(identifier)
                .thenApply(this::encodeLoadResult)
                .thenApply {
                    ResponseEntity.ok(it.toString())
                };
    }

    @GetMapping(value = ["/decodetrack"], produces = ["application/json"])
    @ResponseBody
    fun getDecodeTrack(@RequestParam track: String): ResponseEntity<String> {
        val audioTrack = Util.toAudioTrack(audioPlayerManager, track);
        return ResponseEntity.ok(trackToJSON(audioTrack).toString());
    }

    @PostMapping(value = ["/decodetracks"], consumes = ["application/json"], produces = ["application/json"])
    @ResponseBody
    fun postDecodeTracks(@RequestBody body: String): ResponseEntity<String> {
        val requestJSON = JSONArray(body)
        val responseJSON = JSONArray()

        for (i in 0 until requestJSON.length()) {
            val track = requestJSON.getString(i);
            val audioTrack = Util.toAudioTrack(audioPlayerManager, track);

            val infoJSON = trackToJSON(audioTrack);
            val trackJSON = JSONObject()
                    .put("track", track)
                    .put("info", infoJSON);

            responseJSON.put(trackJSON);
        }

        return ResponseEntity.ok(responseJSON.toString())
    }

    private fun encodeLoadResult(loader: AudioResult): JSONObject {
        val root = JSONObject()
        val playlist = JSONObject()
        val tracks = JSONArray()

        loader.tracks.forEach {
            val obj = JSONObject()
            obj.put("info", trackToJSON(it))

            try {
                val encoded = Util.toMessage(audioPlayerManager, it)
                obj.put("track", encoded)
                tracks.put(obj)
            } catch (e: IOException) {
                log.warn("Failed to encode a track ${it.identifier}, skipping", e)
            }
        }

        if (loader.loadResultType == AudioResultStatus.PLAYLIST_LOADED && loader.playlist != null) {
            playlist.put("name", loader.playlist.name)
            playlist.put("creator", loader.playlist.creator)
            playlist.put("image", loader.playlist.image)
            playlist.put("uri", loader.playlist.uri)
            playlist.put("type", loader.playlist.type)
            playlist.put("selectedTrack", loader.tracks.indexOf(loader.playlist.selectedTrack))
        }

        root.put("playlistInfo", playlist)
        root.put("loadType", loader.loadResultType)
        root.put("tracks", tracks)

        if (loader.loadResultType == AudioResultStatus.LOAD_FAILED && loader.exception != null) {
            val exception = JSONObject();
            exception.put("message", loader.exception.getLocalizedMessage())
            exception.put("severity", loader.exception.severity.toString())

            root.put("exception", exception)
            log.error("Track loading failed", loader.exception)
        }

        return root
    }

    private fun trackToJSON(audioTrack: AudioTrack): JSONObject {
        val trackInfo = audioTrack.info

        return JSONObject()
                .put("class", audioTrack::class.simpleName)
                .put("title", trackInfo.title)
                .put("author", trackInfo.author)
                .put("length", trackInfo.length)
                .put("identifier", trackInfo.identifier)
                .put("uri", trackInfo.uri)
                .put("isStream", trackInfo.isStream)
                .put("artwork", trackInfo.artworkUrl)
                .put("isSeekable", audioTrack.isSeekable)
                .put("position", audioTrack.position)
                .put("source", audioTrack.sourceManager?.sourceName ?: "unknown")
                .put("userData", audioTrack.userData ?: null)
    }
}