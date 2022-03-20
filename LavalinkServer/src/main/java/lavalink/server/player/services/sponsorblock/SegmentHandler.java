package lavalink.server.player.services.sponsorblock;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler;
import lavalink.server.player.Player;
import lavalink.server.player.services.PlayerServicesHandler;
import lavalink.server.util.Util;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class SegmentHandler implements TrackMarkerHandler {
    private final Player player;
    private final PlayerServicesHandler servicesHandler;
    private final AudioTrack track;
    private final List<Segment> segments;
    private int segmentIndex;

    public SegmentHandler(Player player, AudioTrack track, List<Segment> segments) {
        this.player = player;
        this.servicesHandler = this.player.getServicesHandler();
        this.track = track;
        this.segments = segments;
    }

    private Segment getCurrentSegment() {
        return this.segments.get(this.segmentIndex);
    }

    @Override
    public void handle(MarkerState state) {
        if (!(state == MarkerState.REACHED || state == MarkerState.LATE)) return;
        Segment segment = this.getCurrentSegment();
        if (!this.servicesHandler.isSponsorblockEnabled()) return;

        track.setPosition(segment.getEndTime());
        JSONObject json = new JSONObject()
            .put("op", "event")
            .put("event", "SponsorblockSegmentSkipped")
            .put("guildId", this.player.getGuildId())
            .put("segment", segment.encode());

        try {
            json.put("track", Util.toMessage(this.player.getAudioPlayerManager(), track));
        } catch (IOException e) {
            json.put("track", JSONObject.NULL);
        }

        this.player.getSocket().sendMessage(json);
        this.segmentIndex++;
        if (this.segmentIndex < segments.size()) {
            track.setMarker(new TrackMarker(this.getCurrentSegment().getStartTime(), this));
        }
    }
}
