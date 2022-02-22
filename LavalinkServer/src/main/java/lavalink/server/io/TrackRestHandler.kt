package lavalink.server.io

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import lavalink.server.player.track.processing.*
import lavalink.server.util.RequestUtil
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
                .thenApply {
                    RequestUtil.encodeLoadResult(it, audioPlayerManager)
                }
                .thenApply {
                    ResponseEntity.ok(it.toString())
                };
    }

    @GetMapping(value = ["/decodetrack"], produces = ["application/json"])
    @ResponseBody
    fun getDecodeTrack(@RequestParam track: String): ResponseEntity<String> {
        val audioTrack = Util.toAudioTrack(audioPlayerManager, track);
        return ResponseEntity.ok(RequestUtil.trackToJSON(audioTrack).toString());
    }

    @PostMapping(value = ["/decodetracks"], consumes = ["application/json"], produces = ["application/json"])
    @ResponseBody
    fun postDecodeTracks(@RequestBody body: String): ResponseEntity<String> {
        val requestJSON = JSONArray(body)
        val responseJSON = JSONArray()

        for (i in 0 until requestJSON.length()) {
            val track = requestJSON.getString(i);
            val audioTrack = Util.toAudioTrack(audioPlayerManager, track);

            val infoJSON = RequestUtil.trackToJSON(audioTrack);
            val trackJSON = JSONObject()
                    .put("track", track)
                    .put("info", infoJSON);

            responseJSON.put(trackJSON);
        }

        return ResponseEntity.ok(responseJSON.toString())
    }
}