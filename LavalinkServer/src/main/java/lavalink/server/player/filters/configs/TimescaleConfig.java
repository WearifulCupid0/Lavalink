package lavalink.server.player.filters.configs;

import lavalink.server.player.filters.FilterConfig;
import com.github.natanbc.lavadsp.natives.TimescaleNativeLibLoader;
import com.github.natanbc.lavadsp.timescale.TimescalePcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import org.json.JSONObject;

public class TimescaleConfig implements FilterConfig {
    private static final boolean isNativesAvaible = tryLoad(TimescaleNativeLibLoader::loadTimescaleLibrary);
    private float speed = 1f;
    private float pitch = 1f;
    private float rate = 1f;
    
    public float getSpeed() {
        return speed;
    }
    
    public void setSpeed(float speed) {
        if(speed <= 0) {
            throw new IllegalArgumentException("speed <= 0");
        }
        this.speed = speed;
    }
    
    public float getPitch() {
        return pitch;
    }
    
    public void setPitch(float pitch) {
        if(pitch <= 0) {
            throw new IllegalArgumentException("pitch <= 0");
        }
        this.pitch = pitch;
    }
    
    public float getRate() {
        return rate;
    }
    
    public void setRate(float rate) {
        if(rate <= 0) {
            throw new IllegalArgumentException("rate <= 0");
        }
        this.rate = rate;
    }
    
    @Override
    public String getFilterName() {
        return "timescale";
    }
    
    @Override
    public boolean isEnabled() {
        return isNativesAvaible &&
            (FilterConfig.isSet(speed, 1f) || FilterConfig.isSet(pitch, 1f) || FilterConfig.isSet(rate, 1f));
    }

    @Override
    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        return new TimescalePcmAudioFilter(output, format.channelCount, format.sampleRate)
            .setSpeed(speed)
            .setPitch(pitch)
            .setRate(rate);
    }

    @Override
    public JSONObject encode() {
        return new JSONObject()
            .put("speed", speed)
            .put("pitch", pitch)
            .put("rate", rate);
    }

    private static boolean tryLoad(Runnable load) {
        try {
            load.run();
            return true;
        } catch(Throwable error) {
            return false;
        }
    }
}
