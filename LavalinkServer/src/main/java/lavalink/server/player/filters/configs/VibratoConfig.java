package lavalink.server.player.filters.configs;

import lavalink.server.player.filters.FilterConfig;
import com.github.natanbc.lavadsp.vibrato.VibratoPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import org.json.JSONObject;

public class VibratoConfig implements FilterConfig {
    private static final float VIBRATO_FREQUENCY_MAX_HZ = 14;
    
    private float frequency = 2f;
    private float depth = 0.5f;
    
    public float getFrequency() {
        return frequency;
    }
    
    public void setFrequency(float frequency) {
        if(frequency <= 0) {
            throw new IllegalArgumentException("Frequency <= 0");
        }
        if(frequency > VIBRATO_FREQUENCY_MAX_HZ) {
            throw new IllegalArgumentException("Frequency > max (" + VIBRATO_FREQUENCY_MAX_HZ + ")");
        }
        this.frequency = frequency;
    }
    
    public float getDepth() {
        return depth;
    }
    
    public void setDepth(float depth) {
        if(depth <= 0) {
            throw new IllegalArgumentException("Depth <= 0");
        }
        if(depth > 1) {
            throw new IllegalArgumentException("Depth > 1");
        }
        this.depth = depth;
    }
    
    @Override
    public String getFilterName() {
        return "vibrato";
    }
    
    @Override
    public boolean isEnabled() {
        return FilterConfig.isSet(frequency, 2f) || FilterConfig.isSet(depth, 0.5f);
    }
    
    @Override
    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        return new VibratoPcmAudioFilter(output, format.channelCount, format.sampleRate)
            .setFrequency(frequency)
            .setDepth(depth);
    }
    
    @Override
    public JSONObject encode() {
        return new JSONObject()
            .put("frequency", frequency)
            .put("depth", depth);
    }
}
