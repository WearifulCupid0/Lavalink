package lavalink.server.player.filters.configs;

import lavalink.server.player.filters.FilterConfig;
import com.github.natanbc.lavadsp.volume.VolumePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import org.json.JSONObject;

public class VolumeConfig implements FilterConfig {
    private float volume = 1f;
    
    public float getVolume() {
        return volume;
    }
    
    public void setVolume(float volume) {
        if(volume <= 0) {
            throw new IllegalArgumentException("Volume <= 0.0");
        }
        if(volume > 5) {
            throw new IllegalArgumentException("Volume > 5.0");
        }
        this.volume = volume;
    }
    
    @Override
    public String getFilterName() {
        return "volume";
    }
    
    @Override
    public boolean isEnabled() {
        return FilterConfig.isSet(volume, 1f);
    }
    
    @Override
    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        return new VolumePcmAudioFilter(output)
            .setVolume(volume);
    }
    
    @Override
    public JSONObject encode() {
        return new JSONObject()
            .put("volume", volume);
    }
}
