package lavalink.server.player.filters.configs;

import lavalink.server.player.filters.FilterConfig;
import me.rohank05.echo.EchoPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import org.json.JSONObject;

public class EchoConfig implements FilterConfig {
    private float decay = -1;
    private double delay = -1;

    public float getDecay() {
        return this.decay;
    }

    public double getDelay() {
        return this.delay;
    }

    public void setDecay(float decay) {
        this.decay = decay;
    }

    public void setDelay(double delay) {
        this.delay = delay;
    }

    @Override
    public String getFilterName() {
        return "echo";
    }

    @Override
    public boolean isEnabled() {
        return FilterConfig.isSet(this.decay, -1) || FilterConfig.isSet((float) this.delay, -1);
    }

    @Override
    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        return new EchoPcmAudioFilter(output, format.channelCount, format.sampleRate)
                .setDecay(this.decay)
                .setDelay(this.delay);
    }
    
    @Override
    public JSONObject encode() {
        return new JSONObject().put("decay", this.decay).put("delay", this.delay);
    }
}
