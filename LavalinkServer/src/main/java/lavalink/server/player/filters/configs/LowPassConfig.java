package lavalink.server.player.filters.configs;

import lavalink.server.player.filters.FilterConfig;
import com.github.natanbc.lavadsp.lowpass.LowPassPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import org.json.JSONObject;

public class LowPassConfig implements FilterConfig {
    private float smoothing = 20f;
    
    public float getSmoothing() {
        return smoothing;
    }
    
    public void setSmoothing(float smoothing) {
        this.smoothing = smoothing;
    }

    @Override
    public String getFilterName() {
        return "lowpass";
    }
    
    @Override
    public boolean isEnabled() {
        return FilterConfig.isSet(smoothing, 20f);
    }
    
    @Override
    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        return new LowPassPcmAudioFilter(output, format.channelCount, 0)
                .setSmoothing(smoothing);
    }
    
    @Override
    public JSONObject encode() {
        return new JSONObject().put("smoothing", smoothing);
    }
}
