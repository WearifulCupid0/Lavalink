package lavalink.server.io

import com.sedmelluq.discord.lavaplayer.track.TrackMarker
import dev.arbjerg.lavalink.api.AudioFilterExtension
import dev.arbjerg.lavalink.api.WebSocketExtension
import lavalink.server.player.Player
import lavalink.server.player.TrackEndMarkerHandler
import lavalink.server.player.filters.Band
import lavalink.server.player.filters.FilterChain
import lavalink.server.util.Util
import moe.kyokobot.koe.VoiceServerInfo
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction1

class PlayerFunHandlers(private val context: SocketContext) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(PlayerFunHandlers::class.java)
    }

    private var loggedVolumeDeprecationWarning = false
    private var loggedEqualizerDeprecationWarning = false

    public fun play(json: JSONObject, player: Player) {
        val noReplace = json.optBoolean("noReplace", false)

        if (noReplace && player.playingTrack != null) {
            log.info("Skipping play request because of noReplace")
            return
        }

        val track = Util.toAudioTrack(context.audioPlayerManager, json.getString("track"))

        if (json.has("startTime")) {
            track.position = json.getLong("startTime")
        }

        player.setPause(json.optBoolean("pause", false))
        if (json.has("volume")) {
            if(!loggedVolumeDeprecationWarning) log.warn("The volume property in the play operation has been deprecated" +
                    "and will be removed in v4. Please configure a filter instead. Note that the new filter takes a " +
                    "float value with 1.0 being 100%")
            loggedVolumeDeprecationWarning = true
            val filters = player.filters ?: FilterChain()
            filters.volume = json.getFloat("volume") / 100
            player.filters = filters
        }

        if (json.has("endTime")) {
            val stopTime = json.getLong("endTime")
            if (stopTime > 0) {
                val handler = TrackEndMarkerHandler(player)
                val marker = TrackMarker(stopTime, handler)
                track.setMarker(marker)
            }
        }

        player.play(track)

        val conn = context.getVoiceConnection(player)
        context.getPlayer(player.getGuildId()).provideTo(conn)
    }

    public fun update(json: JSONObject, player: Player) {
        if (json.has("volume")) player.setVolume(json.getInt("volume"))
        if (json.has("position")) player.seekTo(json.getLong("position"))
        if (json.has("filters")) player.filters = FilterChain.parse(json.getJSONObject("filters"), context.filterExtensions)
        if (json.has("pause")) player.setPause(json.getBoolean("pause"))
        if (json.has("play")) play(json.getJSONObject("play"), player)
        if (json.has("voice")) voiceUpdate(json.getJSONObject("voice"), player)
    }

    public fun voiceUpdate(json: JSONObject, player: Player) {
        val sessionId = json.getString("sessionId")
        
        val event = json.getJSONObject("event")
        val endpoint: String? = event.optString("endpoint")
        val token: String = event.getString("token")

        //discord sometimes send a partial server update missing the endpoint, which can be ignored.
        endpoint ?: return
        //clear old connection
        context.koe.destroyConnection(player.getGuildId())

        val conn = context.getVoiceConnection(player)
        conn.connect(VoiceServerInfo(sessionId, endpoint, token)).whenComplete { _, _ ->
            player.provideTo(conn)
        }
    }
}