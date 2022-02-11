package lavalink.server.player.filters;

import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import org.json.JSONObject;

public interface FilterConfig {
    float MINIMUM_FP_DIFF = 0.01f;
    
    String getFilterName();
    boolean isEnabled();
    AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output);
    JSONObject encode();
    static boolean isSet(float value, float defaultValue) {
        return Math.abs(value - defaultValue) >= MINIMUM_FP_DIFF;
    }
}
