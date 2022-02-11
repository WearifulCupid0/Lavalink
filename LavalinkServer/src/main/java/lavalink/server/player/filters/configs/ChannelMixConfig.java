package lavalink.server.player.filters.configs;

import lavalink.server.player.filters.FilterConfig;
import com.github.natanbc.lavadsp.channelmix.ChannelMixPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import org.json.JSONObject;

public class ChannelMixConfig implements FilterConfig {
    private float leftToLeft = 1f;
    private float leftToRight = 0f;
    private float rightToLeft = 0f;
    private float rightToRight = 1f;
    
    public float getLeftToLeft() {
        return leftToLeft;
    }
    
    public void setLeftToLeft(float leftToLeft) {
        this.leftToLeft = leftToLeft;
    }
    
    public float getLeftToRight() {
        return leftToRight;
    }
    
    public void setLeftToRight(float leftToRight) {
        this.leftToRight = leftToRight;
    }
    
    public float getRightToLeft() {
        return rightToLeft;
    }
    
    public void setRightToLeft(float rightToLeft) {
        this.rightToLeft = rightToLeft;
    }
    
    public float getRightToRight() {
        return rightToRight;
    }
    
    public void setRightToRight(float rightToRight) {
        this.rightToRight = rightToRight;
    }

    @Override
    public String getFilterName() {
        return "channelmix";
    }

    @Override
    public boolean isEnabled() {
        return FilterConfig.isSet(leftToLeft,  1.0f) || FilterConfig.isSet(leftToRight,  0.0f) ||
               FilterConfig.isSet(rightToLeft, 0.0f) || FilterConfig.isSet(rightToRight, 1.0f);
    }

    @Override
    public AudioFilter create(AudioDataFormat format, FloatPcmAudioFilter output) {
        return new ChannelMixPcmAudioFilter(output)
                .setLeftToLeft(leftToLeft)
                .setLeftToRight(leftToRight)
                .setRightToLeft(rightToLeft)
                .setRightToRight(rightToRight);
    }
    
    @Override
    public JSONObject encode() {
        return new JSONObject()
                .put("leftToLeft",   leftToLeft)
                .put("leftToRight",  leftToRight)
                .put("rightToLeft",  rightToLeft)
                .put("rightToRight", rightToRight);
    }
}
