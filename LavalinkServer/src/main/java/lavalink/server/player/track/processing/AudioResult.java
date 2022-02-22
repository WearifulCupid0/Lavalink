package lavalink.server.player.track.processing;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioResult {
    public AudioResultStatus loadResultType;
    public AudioPlaylist playlist;
    public List<AudioTrack> tracks;
    public FriendlyException exception;

    public AudioResult(AudioPlaylist playlist) {
        this.loadResultType = playlist.isSearchResult() ? AudioResultStatus.SEARCH_RESULT : AudioResultStatus.PLAYLIST_LOADED;;
        this.playlist = playlist.isSearchResult() ? null : playlist;
        this.tracks = playlist.getTracks();
        this.exception = null;
    }

    public AudioResult(AudioTrack track) {
        this.loadResultType = AudioResultStatus.TRACK_LOADED;
        this.playlist = null;
        this.exception = null;
        List<AudioTrack> list = new ArrayList<>();
        list.add(track);
        this.tracks = list;
    }

    public AudioResult(FriendlyException exception) {
        this.loadResultType = AudioResultStatus.LOAD_FAILED;
        this.tracks = Collections.emptyList();
        this.playlist = null;
        this.exception = exception;
    }

    public AudioResult() {
        this.loadResultType = AudioResultStatus.NO_MATCHES;
        this.tracks = Collections.emptyList();
        this.playlist = null;
        this.exception = null;
    }
}
