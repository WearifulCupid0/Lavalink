package lavalink.server.player.services;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import org.json.JSONArray;
import org.json.JSONObject;

import lavalink.server.player.Player;
import lavalink.server.player.services.sponsorblock.SponsorblockHandler;

import java.util.ArrayList;
import java.util.List;

public class PlayerServicesHandler {
    private final Player player;

    //Sponsorblock
    private boolean sponsorblock = false;
    private List<String> sponsorblockCategories = new ArrayList<>();

    public PlayerServicesHandler(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isSponsorblockEnabled() {
        return this.sponsorblock;
    }

    public void setSponsorblock(boolean sponsorblock) {
        this.sponsorblock = sponsorblock;
    }

    public List<String> getSponsorblockCategories() {
        return this.sponsorblockCategories;
    }

    public void setSponsorblockCategories(List<String> categories) {
        this.sponsorblockCategories = categories;
    }

    public JSONObject encode() {
        return new JSONObject()
        .put("sponsorblock", new JSONObject()
            .put("enabled", this.sponsorblock)
            .put("categories", this.sponsorblockCategories)
        );
    }

    public void handleTrackStart(AudioTrack track) {
        AudioSourceManager sourceManager = track.getSourceManager();
        if (this.sponsorblock && !this.sponsorblockCategories.isEmpty() && (sourceManager != null && sourceManager.getSourceName().equals("youtube"))) {
            SponsorblockHandler.handleTrack(track, this);
        }
    }

    public void handleTrackEnd(AudioTrack track, AudioTrackEndReason reason) {
        //Nothing for now...
    }

    public void parseJSON(JSONObject json) {
        if (json.has("sponsorblock")) {
            JSONObject sponsorblock = json.getJSONObject("sponsorblock");
            this.sponsorblock = sponsorblock.optBoolean("enabled", false);
            JSONArray jsonArr = sponsorblock.optJSONArray("categories");
            List<Object> categories = jsonArr != null ? jsonArr.toList() : null;
            if (categories != null && !categories.isEmpty()) {
                this.sponsorblockCategories.clear();
                for (Object o : categories) {
                    this.sponsorblockCategories.add(o.toString());
                }
            }
        }
    }
}
