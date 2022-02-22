package lavalink.server.player.services.sponsorblock;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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

public class SponsorblockHandler {
    private static final Logger log = LoggerFactory.getLogger(SponsorblockHandler.class);
	private static final String SPONSORBLOCK_URL = "https://sponsor.ajay.app/api/skipSegments?videoID=%s&categories=%s";

    private static JSONArray fetchSegments(String videoId, List<String> categories) throws Exception {
        String responseText = "";
		try {
            String cat = categories.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(", "));
			URL url = new URL(String.format(SPONSORBLOCK_URL, videoId, URLEncoder.encode(String.format("[%s]", cat), StandardCharsets.UTF_8)));
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
			responseText = content.toString();
		} finally {}

        return new JSONArray(responseText);
    }

    public static void handleTrack(AudioTrack track, PlayerServicesHandler servicesHandler) {
        String identifier = track.getIdentifier();
        try {
            JSONArray array = SponsorblockHandler.fetchSegments(identifier, servicesHandler.getSponsorBlockCategories());
            List<Segment> segments = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                segments.add(new Segment(array.getJSONObject(i)));
            }

            if (segments != null && !segments.isEmpty()) {
                JSONObject json = new JSONObject()
                    .put("op", "event")
                    .put("event", "TrackSegmentLoaded")
                    .put("guildId", servicesHandler.getPlayer().getGuildId())
                    .put("segments", new JSONArray(segments.stream().map(Segment::encode).collect(Collectors.toList())));

                    try {
                        json.put("track", Util.toMessage(servicesHandler.getPlayer().getAudioPlayerManager(), track));
                    } catch (Exception e) {
                        json.put("track", JSONObject.NULL);
                    }
                
                servicesHandler.getPlayer().getSocket().sendMessage(json);
            }
        } catch (Exception e) {
            log.error(String.format("Failed to fetch video segments from video id {}", identifier), e);
        }
    }
}
