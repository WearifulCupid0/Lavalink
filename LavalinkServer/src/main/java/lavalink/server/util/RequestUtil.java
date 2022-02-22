package lavalink.server.util;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.lavaplayer.extensions.thirdpartysources.ThirdPartyAudioTrack;

import lavalink.server.player.track.processing.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RequestUtil {
    private static final Logger log = LoggerFactory.getLogger(RequestUtil.class);

    public static JSONObject trackToJSON(AudioTrack audioTrack) {
        AudioTrackInfo trackInfo = audioTrack.getInfo();

        JSONObject out = new JSONObject()
                .put("class", audioTrack.getClass().getName())
                .put("title", trackInfo.title)
                .put("author", trackInfo.author)
                .put("length", trackInfo.length)
                .put("identifier", trackInfo.identifier)
                .put("uri", trackInfo.uri)
                .put("isStream", trackInfo.isStream)
                .put("artwork", trackInfo.artworkUrl)
                .put("isSeekable", audioTrack.isSeekable())
                .put("position", audioTrack.getPosition())
                .put("source", audioTrack.getSourceManager() != null ? audioTrack.getSourceManager().getSourceName() : "unknown")
                .put("userData", audioTrack.getUserData());

        if (audioTrack instanceof ThirdPartyAudioTrack) {
            ThirdPartyAudioTrack track = ((ThirdPartyAudioTrack) audioTrack);
            String isrc = track.getISRC();
            if (isrc != null) out.put("isrc", isrc);
        }
    
        return out;
    }

    public static JSONObject encodeLoadResult(AudioResult loader, AudioPlayerManager audioPlayerManager) {
        JSONObject root = new JSONObject();
        JSONObject playlist = new JSONObject();
        JSONArray tracks = new JSONArray();

        loader.tracks.forEach(track -> {
            JSONObject obj = new JSONObject();
            obj.put("info", trackToJSON(track));

            try {
                String encoded = Util.toMessage(audioPlayerManager, track);
                obj.put("track", encoded);
                tracks.put(obj);
            } catch (IOException e) {
                log.warn("Failed to encode a track {}, skipping", e);
            }
        });

        if (loader.loadResultType == AudioResultStatus.PLAYLIST_LOADED && loader.playlist != null) {
            playlist
            .put("name", loader.playlist.getName())
            .put("creator", loader.playlist.getCreator())
            .put("image", loader.playlist.getImage())
            .put("uri", loader.playlist.getURI())
            .put("type", loader.playlist.getType())
            .put("selectedTrack", loader.tracks.indexOf(loader.playlist.getSelectedTrack()));
        }

        root.put("playlistInfo", playlist);
        root.put("loadType", loader.loadResultType);
        root.put("tracks", tracks);

        if (loader.loadResultType == AudioResultStatus.LOAD_FAILED && loader.exception != null) {
            root.put("exception", new JSONObject()
                .put("message", loader.exception.getLocalizedMessage())
                .put("severity", loader.exception.severity.toString())
                .put("cause", Util.getRootCause(loader.exception).toString())
            );
            log.error("Track loading failed", loader.exception);
        }

        return root;
    }
}
