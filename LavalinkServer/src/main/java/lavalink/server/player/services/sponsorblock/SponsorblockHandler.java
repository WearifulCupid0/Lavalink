package lavalink.server.player.services.sponsorblock;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import lavalink.server.player.Player;
import lavalink.server.player.services.PlayerServicesHandler;
import lavalink.server.util.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.SUSPICIOUS;

public class SponsorblockHandler {
    private static final Logger log = LoggerFactory.getLogger(SponsorblockHandler.class);
    private static final String SPONSORBLOCK_URL = "https://sponsor.ajay.app/api/skipSegments?videoID=%s&categories=%s";

    private static JSONArray fetchSegments(String videoId, List<String> categories) {
        try {
            String cat = categories.stream().map(s -> String.format("\"%s\"", s)).collect(Collectors.joining(","));
            URL url = new URL(String.format(SPONSORBLOCK_URL, videoId,
                    URLEncoder.encode(String.format("[%s]", cat), StandardCharsets.UTF_8)));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            return new JSONArray(content.toString());
        } catch (Exception e) {
            log.error(String.format("Failed to fetch video segments from track: %s", videoId), e);
            throw new FriendlyException("Failed to fetch video segments", SUSPICIOUS, e);
        }
    }

    public static void handleTrack(AudioTrack track, PlayerServicesHandler servicesHandler) {
        try {
            String identifier = track.getIdentifier();
            JSONArray array = SponsorblockHandler.fetchSegments(identifier, servicesHandler.getSponsorblockCategories());
            List<Segment> segments = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                segments.add(new Segment(array.getJSONObject(i)));
            }

            if (!segments.isEmpty()) {
                JSONObject json = new JSONObject()
                        .put("op", "event")
                        .put("event", "TrackSegmentLoaded")
                        .put("guildId", servicesHandler.getPlayer().getGuildId())
                        .put("segments",
                                new JSONArray(segments.stream().map(Segment::encode).collect(Collectors.toList())));

                try {
                    json.put("track", Util.toMessage(servicesHandler.getPlayer().getAudioPlayerManager(), track));
                } catch (Exception e) {
                    json.put("track", JSONObject.NULL);
                }

                servicesHandler.getPlayer().getSocket().sendMessage(json);
                track.setMarker(new TrackMarker(segments.get(0).getStartTime(),
                        new SegmentHandler(servicesHandler.getPlayer(), track, segments)));
            }
        } catch (FriendlyException err) {
            JSONObject out = new JSONObject()
                    .put("op", "event")
                    .put("event", "TrackSegmentFailed")
                    .put("guildId", servicesHandler.getPlayer().getGuildId());

            try {
                out.put("track", Util.toMessage(servicesHandler.getPlayer().getAudioPlayerManager(), track));
            } catch (Exception e) {
                out.put("track", JSONObject.NULL);
            }

            out.put("exception", new JSONObject()
                .put("message", err.getMessage())
                .put("severity", err.severity.toString())
                .put("cause", Util.getRootCause(err))
            );

            servicesHandler.getPlayer().getSocket().sendMessage(out);
        }
    }
}
