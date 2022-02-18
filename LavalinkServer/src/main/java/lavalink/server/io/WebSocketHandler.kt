package lavalink.server.io

import com.sedmelluq.discord.lavaplayer.track.TrackMarker
import dev.arbjerg.lavalink.api.AudioFilterExtension
import dev.arbjerg.lavalink.api.WebSocketExtension
import lavalink.server.player.TrackEndMarkerHandler
import lavalink.server.player.filters.FilterChain
import lavalink.server.util.Util
import moe.kyokobot.koe.VoiceServerInfo
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KFunction1

class WebSocketHandler(
    private val context: SocketContext,
    private val wsExtensions: List<WebSocketExtension>,
    private val filterExtensions: List<AudioFilterExtension>
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(WebSocketHandler::class.java)
    }

    private val handlers: Map<String, (JSONObject) -> Unit> = mutableMapOf(
        "voiceUpdate" to ::voiceUpdate,
        "update" to ::update,
        "play" to ::play,
        "stop" to ::stop,
        "pause" to ::pause,
        "seek" to ::seek,
        "volume" to ::volume,
        "filters" to ::filters,
        "destroy" to ::destroy,
        "configureResuming" to ::configureResuming,
        "ping" to ::ping
    ).apply {
        wsExtensions.forEach {
            val func = fun(json: JSONObject) { it.onInvocation(context, json) }
            this[it.opName] = func as KFunction1<JSONObject, Unit>
        }
    }

    fun handle(json: JSONObject) {
        val op = json.getString("op")
        val handler = handlers[op] ?: return log.warn("Unknown op '$op'")
        handler(json)
    }

    private fun voiceUpdate(json: JSONObject) {
        val player = context.getPlayer(json.getString("guildId"))
        context.playerHandler.voiceUpdate(json, player)
    }
    
    private fun update(json: JSONObject) {
        val player = context.getPlayer(json.getString("guildId"))
        context.playerHandler.update(json, player)
    }

    private fun play(json: JSONObject) {
        val player = context.getPlayer(json.getString("guildId"))
        context.playerHandler.play(json, player)
    }

    private fun filters(json: JSONObject) {
        val player = context.getPlayer(json.getLong("guildId"))
        FilterChain.setFiltersFromJSON(json, player.getFilterChain())
    }

    private fun stop(json: JSONObject) {
        val player = context.getPlayer(json.getString("guildId"))
        player.stop()
    }

    private fun pause(json: JSONObject) {
        val player = context.getPlayer(json.getString("guildId"))
        player.setPause(json.getBoolean("pause"))
        SocketServer.sendPlayerUpdate(context, player)
    }

    private fun seek(json: JSONObject) {
        val player = context.getPlayer(json.getString("guildId"))
        player.seekTo(json.getLong("position"))
        SocketServer.sendPlayerUpdate(context, player)
    }

    private fun volume(json: JSONObject) {
        val player = context.getPlayer(json.getString("guildId"))
        player.setVolume(json.getInt("volume"))
    }

    private fun destroy(json: JSONObject) {
        context.destroyPlayer(json.getLong("guildId"))
    }

    private fun configureResuming(json: JSONObject) {
        context.resumeKey = json.optString("key", null)
        if (json.has("timeout")) context.resumeTimeout = json.getLong("timeout")
    }

    private fun ping(json: JSONObject) {
        context.send(JSONObject().put("op", "pong"))
    }
}
