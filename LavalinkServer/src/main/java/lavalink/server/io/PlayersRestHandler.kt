package lavalink.server.io

import lavalink.server.player.filters.FilterChain
import lavalink.server.player.Player;
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import org.json.JSONObject

@RestController
class PlayersRestHandler(private val server: SocketServer) {

    @GetMapping("/players/{guildId}")
    fun getPlayer(request: HttpServletRequest, @PathVariable("guildId") guildId: String): ResponseEntity<String> {
        val context = getExistingContext(request.getHeader("Session-Id"))
        if (context == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val player = getExistingPlayer(guildId, context)
        if (player == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity.ok(player.getState().toString())
    }

    @DeleteMapping("/players/{guildId}")
    fun playerDelete(request: HttpServletRequest, @PathVariable("guildId") guildId: String): ResponseEntity<Void> {
        val context = getExistingContext(request.getHeader("Session-Id"))
        if (context == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val player = getExistingPlayer(guildId, context)
        if (player == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        context.destroyPlayer(guildId.toLong())
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/players/{guildId}")
    fun createPlayer(request: HttpServletRequest, @PathVariable("guildId") guildId: String, @RequestBody body: String): ResponseEntity<String> {
        val context = server.getExistingContext(request.getHeader("Session-Id"))
        if (context == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val player = getOrCreatePlayer(guildId, context)
        if (player == null) return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        if (body.isNotBlank()) context.playerHandler.update(JSONObject(body), player)
        return ResponseEntity.ok(player.getState().toString())
    }

    @PatchMapping("/players/{guildId}")
    fun playerEdit(request: HttpServletRequest, @PathVariable("guildId") guildId: String, @RequestBody body: String): ResponseEntity<String> {
        val context = getExistingContext(request.getHeader("Session-Id"))
        if (context == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val player = getExistingPlayer(guildId, context)
        if (player == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        if (body.isBlank()) return ResponseEntity(HttpStatus.BAD_REQUEST)
        context.playerHandler.update(JSONObject(body), player)
        return ResponseEntity.ok(player.getState().toString())
    }

    @PostMapping("/players/{guildId}/voice")
    fun playerVoice(request: HttpServletRequest, @PathVariable("guildId") guildId: String, @RequestBody body: String): ResponseEntity<Void> {
        val context = getExistingContext(request.getHeader("Session-Id"))
        if (context == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val player = getExistingPlayer(guildId, context)
        if (player == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        if (body.isBlank()) return ResponseEntity(HttpStatus.BAD_REQUEST)
        context.playerHandler.voiceUpdate(JSONObject(body), player)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/players/{guildId}/play")
    fun playerPlay(request: HttpServletRequest, @PathVariable("guildId") guildId: String, @RequestBody body: String): ResponseEntity<String> {
        val context = getExistingContext(request.getHeader("Session-Id"))
        if (context == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val player = getExistingPlayer(guildId, context)
        if (player == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        if (body.isBlank()) return ResponseEntity(HttpStatus.BAD_REQUEST)
        context.playerHandler.play(JSONObject(body), player)
        return ResponseEntity.ok(player.getState().toString())
    }

    @PostMapping("/players/{guildId}/stop")
    fun playerStop(request: HttpServletRequest, @PathVariable("guildId") guildId: String): ResponseEntity<String> {
        val context = getExistingContext(request.getHeader("Session-Id"))
        if (context == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val player = getExistingPlayer(guildId, context)
        if (player == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        player.stop()
        return ResponseEntity.ok(player.getState().toString())
    }

    @PatchMapping("/players/{guildId}/filters")
    fun playerFilters(request: HttpServletRequest, @PathVariable("guildId") guildId: String, @RequestBody body: String): ResponseEntity<String> {
        val context = getExistingContext(request.getHeader("Session-Id"))
        if (context == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val player = getExistingPlayer(guildId, context)
        if (player == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        if (body.isBlank()) return ResponseEntity(HttpStatus.BAD_REQUEST)
        FilterChain.setFiltersFromJSON(JSONObject(body), player.getFilterChain())
        return ResponseEntity.ok(player.getState().toString())
    }

    @PatchMapping("/players/{guildId}/pause")
    fun playerPause(request: HttpServletRequest, @PathVariable("guildId") guildId: String, @RequestBody body: String): ResponseEntity<String> {
        val context = getExistingContext(request.getHeader("Session-Id"))
        if (context == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val player = getExistingPlayer(guildId, context)
        if (player == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        if (body.isBlank()) return ResponseEntity(HttpStatus.BAD_REQUEST)
        player.setPause(JSONObject(body).getBoolean("pause"))
        return ResponseEntity.ok(player.getState().toString())
    }

    @PatchMapping("/players/{guildId}/seek")
    fun playerSeek(request: HttpServletRequest, @PathVariable("guildId") guildId: String, @RequestBody body: String): ResponseEntity<String> {
        val context = getExistingContext(request.getHeader("Session-Id"))
        if (context == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val player = getExistingPlayer(guildId, context)
        if (player == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        if (body.isBlank()) return ResponseEntity(HttpStatus.BAD_REQUEST)
        player.seekTo(JSONObject(body).getLong("position"))
        return ResponseEntity.ok(player.getState().toString())
    }

    @PatchMapping("/players/{guildId}/volume")
    fun playerVolume(request: HttpServletRequest, @PathVariable("guildId") guildId: String, @RequestBody body: String): ResponseEntity<String> {
        val context = getExistingContext(request.getHeader("Session-Id"))
        if (context == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val player = getExistingPlayer(guildId, context)
        if (player == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        if (body.isBlank()) return ResponseEntity(HttpStatus.BAD_REQUEST)
        player.setVolume(JSONObject(body).getInt("volume"))
        return ResponseEntity.ok(player.getState().toString())
    }

    private fun getOrCreatePlayer(guildId: String, context: SocketContext?): Player? {
        if (context == null) return null;
        return context.getPlayer(guildId)
    }

    private fun getExistingPlayer(guildId: String, context: SocketContext?): Player? {
        if (context == null) return null;
        return context.getExistingPlayer(guildId)
    }

    private fun getExistingContext(sessionId: String): SocketContext? {
        return server.getExistingContext(sessionId)
    }
}