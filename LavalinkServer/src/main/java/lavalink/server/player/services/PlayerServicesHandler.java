package lavalink.server.player.services;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import org.json.JSONObject;

import lavalink.server.player.Player;
import lavalink.server.player.services.sponsorblock.SponsorblockHandler;

import java.util.List;

public class PlayerServicesHandler {
    private final Player player;

    //Sponsorblock
    private boolean sponsorBlock = false;
    private List<String> sponsorBlockCategories;

    public PlayerServicesHandler(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isSponsorBlockEnabled() {
        return this.sponsorBlock;
    }

    public void setSponsorBlock(boolean sponsorBlock) {
        this.sponsorBlock = sponsorBlock;
    }

    public List<String> getSponsorBlockCategories() {
        return this.sponsorBlockCategories;
    }

    public void setSponsorBlockCategories(List<String> categories) {
        this.sponsorBlockCategories = categories;
    }

    public JSONObject encode() {
        return new JSONObject()
        .put("sponsorblock", new JSONObject()
            .put("enabled", this.sponsorBlock)
            .put("categories", this.sponsorBlockCategories)
        );
    }

    public void handleTrackStart(AudioTrack track) {
        AudioSourceManager sourceManager = track.getSourceManager();
        if (this.sponsorBlock && !this.sponsorBlockCategories.isEmpty() && (sourceManager != null && sourceManager.getSourceName().equals("youtube"))) {
            SponsorblockHandler.handleTrack(track, this);
        }
    }

    public void handleTrackEnd(AudioTrack track, AudioTrackEndReason reason) {
        //Nothing for now...
    }
}
