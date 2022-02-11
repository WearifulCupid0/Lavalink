package lavalink.server.player.filters.configs;

import lavalink.server.player.filters.FilterConfig;
import com.github.natanbc.lavadsp.distortion.DistortionPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;

import org.json.JSONObject;

public class DistortionConfig implements FilterConfig {
    private volatile float sinOffset = 0.0f;
    private volatile float sinScale = 1.0f;
    private volatile float cosOffset = 0.0f;
    private volatile float cosScale = 1.0f;
    private volatile float tanOffset = 0.0f;
    private volatile float tanScale = 1.0f;
    private volatile float offset = 0.0f;
    private volatile float scale = 1.0f;

    public float getSinOffset() {
        return sinOffset;
    }

    public void setSinOffset(float sinOffset) {
        this.sinOffset = sinOffset;
    }

    public float getSinScale() {
        return sinScale;
    }

    public void setSinScale(float sinScale) {
        this.sinScale = sinScale;
    }

    public float getCosOffset() {
        return cosOffset;
    }

    public void setCosOffset(float cosOffset) {
        this.cosOffset = cosOffset;
    }

    public float getCosScale() {
        return cosScale;
    }

    public void setCosScale(float cosScale) {
        this.cosScale = cosScale;
    }

    public float getTanOffset() {
        return tanOffset;
    }

    public void setTanOffset(float tanOffset) {
        this.tanOffset = tanOffset;
    }

    public float getTanScale() {
        return tanScale;
    }

    public void setTanScale(float tanScale) {
        this.tanScale = tanScale;
    }

    public float getOffset() {
        return this.offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public String getFilterName() {
        return "distortion";
    }

    @Override
    public boolean isEnabled() {
        return FilterConfig.isSet(sinOffset, 0.0f) || FilterConfig.isSet(sinScale, 1.0f) ||
        FilterConfig.isSet(cosOffset, 0.0f) || FilterConfig.isSet(cosScale, 1.0f) ||
        FilterConfig.isSet(tanOffset, 0.0f) || FilterConfig.isSet(tanScale, 1.0f) ||
        FilterConfig.isSet(offset, 0.0f) || FilterConfig.isSet(scale, 1.0f);
    }

    @Override
    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        return new DistortionPcmAudioFilter(output, format.channelCount)
            .setSinOffset(sinOffset)
            .setSinScale(sinScale)
            .setCosOffset(cosOffset)
            .setCosScale(cosScale)
            .setTanOffset(tanOffset)
            .setTanScale(tanScale)
            .setOffset(offset)
            .setScale(scale);
    }
    
    @Override
    public JSONObject encode() {
        return new JSONObject()
            .put("sinOffset", sinOffset)
            .put("sinScale", sinScale)
            .put("cosOffset", cosOffset)
            .put("cosScale", cosScale)
            .put("tanOffset", tanOffset)
            .put("tanScale", tanScale)
            .put("offset", offset)
            .put("scale", scale);
    }
}