package lavalink.server.player.filters.configs;

import lavalink.server.player.filters.FilterConfig;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.Equalizer;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import org.json.JSONArray;
import org.json.JSONObject;

public class EqualizerConfig implements FilterConfig {
    private final float[] equalizerBands = new float[Equalizer.BAND_COUNT];
    
    public float getBand(int band) {
        return equalizerBands[band];
    }
    
    public void setBand(int band, float gain) {
        equalizerBands[band] = gain;
    }
    
    @Override
    public String getFilterName() {
        return "equalizer";
    }
    
    @Override
    public boolean isEnabled() {
        for(float band : equalizerBands) {
            if(FilterConfig.isSet(band, 0f)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        return Equalizer.isCompatible(format) ? new Equalizer(format.channelCount, output, equalizerBands) : null;
    }

    @Override
    public JSONObject encode() {
        JSONArray array = new JSONArray();
        for(int i = 0; i < Equalizer.BAND_COUNT; i++) {
            array.put(new JSONObject()
                    .put("band", i)
                    .put("gain", equalizerBands[i])
            );
        }
        return new JSONObject().put("bands", array);
    }
}
