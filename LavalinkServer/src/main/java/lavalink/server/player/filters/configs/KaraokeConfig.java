package lavalink.server.player.filters.configs;

import lavalink.server.player.filters.FilterConfig;
import com.github.natanbc.lavadsp.karaoke.KaraokePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import org.json.JSONObject;

public class KaraokeConfig implements FilterConfig {
    private float level = 1f;
    private float monoLevel = 1f;
    private float filterBand = 220f;
    private float filterWidth = 100f;
    
    public float getLevel() {
        return level;
    }
    
    public void setLevel(float level) {
        this.level = level;
    }
    
    public float getMonoLevel() {
        return monoLevel;
    }
    
    public void setMonoLevel(float monoLevel) {
        this.monoLevel = monoLevel;
    }
    
    public float getFilterBand() {
        return filterBand;
    }
    
    public void setFilterBand(float filterBand) {
        this.filterBand = filterBand;
    }
    
    public float getFilterWidth() {
        return filterWidth;
    }
    
    public void setFilterWidth(float filterWidth) {
        this.filterWidth = filterWidth;
    }
    @Override
    public String getFilterName() {
        return "karaoke";
    }
    
    @Override
    public boolean isEnabled() {
        return FilterConfig.isSet(level, 1f) || FilterConfig.isSet(monoLevel, 1f) ||
                FilterConfig.isSet(filterBand, 220f) || FilterConfig.isSet(filterWidth, 100f);
    }
    @Override
    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        return new KaraokePcmAudioFilter(output, format.channelCount, format.sampleRate)
            .setLevel(level)
            .setMonoLevel(monoLevel)
            .setFilterBand(filterBand)
            .setFilterWidth(filterWidth);
    }

    @Override
    public JSONObject encode() {
        return new JSONObject()
            .put("level", level)
            .put("monoLevel", monoLevel)
            .put("filterBand", filterBand)
            .put("filterWidth", filterWidth);
    }
}
