package lavalink.server.player.filters.configs;

import lavalink.server.player.filters.FilterConfig;
import com.github.natanbc.lavadsp.rotation.RotationPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import org.json.JSONObject;

public class RotationConfig implements FilterConfig {
    private float rotationHz = 5f;
    
    public float getRotationHz() {
        return rotationHz;
    }
    
    public void setRotationHz(float rotationHz) {
        this.rotationHz = rotationHz;
    }
    
    @Override
    public String getFilterName() {
        return "rotation";
    }
    
    @Override
    public boolean isEnabled() {
        return FilterConfig.isSet(rotationHz, 5f);
    }
    
    @Override
    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        return new RotationPcmAudioFilter(output, format.sampleRate)
                .setRotationSpeed(rotationHz);
    }
    
    @Override
    public JSONObject encode() {
        return new JSONObject().put("rotationHz", rotationHz);
    }
}
