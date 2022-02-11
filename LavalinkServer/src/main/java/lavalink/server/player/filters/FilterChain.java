package lavalink.server.player.filters;

import lavalink.server.player.filters.configs.*;
import com.sedmelluq.discord.lavaplayer.filter.AudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.FloatPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.filter.PcmFilterFactory;
import com.sedmelluq.discord.lavaplayer.filter.UniversalPcmAudioFilter;
import com.sedmelluq.discord.lavaplayer.format.AudioDataFormat;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class FilterChain {
    private final ChannelMixConfig channelMixConfig = new ChannelMixConfig();
    private final DistortionConfig distortionConfig = new DistortionConfig();
    private final EqualizerConfig equalizerConfig = new EqualizerConfig();
    private final KaraokeConfig karaokeConfig = new KaraokeConfig();
    private final LowPassConfig lowPassConfig = new LowPassConfig();
    private final RotationConfig rotationConfig = new RotationConfig();
    private final TimescaleConfig timescaleConfig = new TimescaleConfig();
    private final TremoloConfig tremoloConfig = new TremoloConfig();
    private final VibratoConfig vibratoConfig = new VibratoConfig();
    private final VolumeConfig volumeConfig = new VolumeConfig();
    private final Map<Class<? extends FilterConfig>, FilterConfig> filters = new HashMap<>();
    private final AudioPlayer player;
    
    public FilterChain(AudioPlayer player) {
        filters.put(channelMixConfig.getClass(), channelMixConfig);
        filters.put(distortionConfig.getClass(), distortionConfig);
        filters.put(equalizerConfig.getClass(), equalizerConfig);
        filters.put(karaokeConfig.getClass(), karaokeConfig);
        filters.put(lowPassConfig.getClass(), lowPassConfig);
        filters.put(rotationConfig.getClass(), rotationConfig);
        filters.put(timescaleConfig.getClass(), timescaleConfig);
        filters.put(tremoloConfig.getClass(), tremoloConfig);
        filters.put(vibratoConfig.getClass(), vibratoConfig);
        filters.put(volumeConfig.getClass(), volumeConfig);
        this.player = player;
    }
    
    public boolean hasConfig(Class<? extends FilterChain> clazz) {
        return filters.containsKey(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends FilterChain> T getConfig(Class<T> clazz) {
        return (T) filters.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends FilterConfig> T config(Class<T> clazz, Supplier<T> supplier) {
        return (T) filters.computeIfAbsent(clazz, __ -> {
            T config = Objects.requireNonNull(supplier.get(), "Provided configuration may not be null");
            if(!clazz.isInstance(config)) {
                throw new IllegalArgumentException("Config not instance of provided class");
            }
            for(FilterConfig c : filters.values()) {
                if(c.getFilterName().equals(config.getFilterName())) {
                    throw new IllegalArgumentException("Duplicate configuration name " + c.getFilterName());
                }
            }
            return config;
        });
    }
    
    public boolean isEnabled() {
        for(FilterConfig config : filters.values()) {
            if(config.isEnabled()) return true;
        }
        return false;
    }

    public PcmFilterFactory factory() {
        return isEnabled() ? new Factory(this) : null;
    }

    public JSONObject encode() {
        JSONObject obj = new JSONObject();
        for(FilterConfig config : filters.values()) {
            obj.put(config.getFilterName(), config.encode().put("enabled", config.isEnabled()));
        }
        return obj;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public ChannelMixConfig getChannelMixConfig() {
        return channelMixConfig;
    }

    public DistortionConfig getDistortionConfig() {
        return distortionConfig;
    }

    public EqualizerConfig getEqualizerConfig() {
        return equalizerConfig;
    }

    public KaraokeConfig getKaraokeConfig() {
        return karaokeConfig;
    }

    public LowPassConfig getLowPassConfig() {
        return lowPassConfig;
    }

    public RotationConfig getRotationConfig() {
        return rotationConfig;
    }

    public TimescaleConfig getTimescaleConfig() {
        return timescaleConfig;
    }

    public TremoloConfig getTremoloConfig() {
        return tremoloConfig;
    }

    public VibratoConfig getVibratoConfig() {
        return vibratoConfig;
    }

    public VolumeConfig getVolumeConfig() {
        return volumeConfig;
    }
    
    private static class Factory implements PcmFilterFactory {
        private final FilterChain chain;
        
        private Factory(FilterChain chain) {
            this.chain = chain;
        }
        
        @Override
        public List<AudioFilter> buildChain(AudioTrack track, AudioDataFormat format, UniversalPcmAudioFilter output) {
            List<AudioFilter> list = new ArrayList<>();
            list.add(output);
            for(FilterConfig config : chain.filters.values()) {
                AudioFilter filter = config.isEnabled() ?
                    config.create(format, (FloatPcmAudioFilter) list.get(0)) //may return null
                    : null;
                if(filter != null) {
                    list.add(0, filter);
                }
            }
            return list.subList(0, list.size() - 1);
        }
    }

    public static void setFiltersFromJSON(JSONObject json, FilterChain filterChain) {
        if (json.has("channelmix")) {
            JSONObject channelMix = json.getJSONObject("channelmix");
            ChannelMixConfig channelMixConfig = filterChain.getChannelMixConfig();
            channelMixConfig.setLeftToLeft(channelMix.getFloat("leftToLeft"));
            channelMixConfig.setLeftToRight(channelMix.getFloat("leftToRight"));
            channelMixConfig.setRightToLeft(channelMix.getFloat("rightToLeft"));
            channelMixConfig.setRightToRight(channelMix.getFloat("rightToRight"));
        }
        if (json.has("distortion")) {
            JSONObject distortion = json.getJSONObject("distortion");
            DistortionConfig distortionConfig = filterChain.getDistortionConfig();
            distortionConfig.setSinOffset(distortion.getFloat("singOffset"));
            distortionConfig.setSinScale(distortion.getFloat("sinScale"));
            distortionConfig.setCosOffset(distortion.getFloat("cosOffset"));
            distortionConfig.setCosScale(distortion.getFloat("cosScale"));
            distortionConfig.setTanOffset(distortion.getFloat("tanOffset"));
            distortionConfig.setTanScale(distortion.getFloat("tanScale"));
            distortionConfig.setOffset(distortion.getFloat("offset"));
            distortionConfig.setScale(distortion.getFloat("scale"));
        }
        if (json.has("equalizer")) {
            JSONArray array = json.getJSONObject("equalizer").getJSONArray("bands");
            EqualizerConfig equalizerConfig = filterChain.getEqualizerConfig();
            for(int i = 0; i < array.length(); i++) {
                JSONObject band = array.getJSONObject(i);
                equalizerConfig.setBand(band.getInt("band"), band.getFloat("gain"));
            }
        }
        if (json.has("karaoke")) {
            JSONObject karaoke = json.getJSONObject("karaoke");
            KaraokeConfig karaokeConfig = filterChain.getKaraokeConfig();
            karaokeConfig.setLevel(karaoke.getFloat("level"));
            karaokeConfig.setMonoLevel(karaoke.getFloat("monoLevel"));
            karaokeConfig.setFilterBand(karaoke.getFloat("filterBand"));
            karaokeConfig.setFilterWidth(karaoke.getFloat("filterWidth"));
        }
        if (json.has("lowpass")) {
            JSONObject lowPass = json.getJSONObject("lowpass");
            LowPassConfig lowPassConfig = filterChain.getLowPassConfig();
            lowPassConfig.setSmoothing(lowPass.getFloat("smoothing"));
        }
        if (json.has("rotation")) {
            JSONObject rotation = json.getJSONObject("rotation");
            RotationConfig rotationConfig = filterChain.getRotationConfig();
            rotationConfig.setRotationHz(rotation.getFloat("rotationHz"));
        }
        if (json.has("timescale")) {
            JSONObject timescale = json.getJSONObject("timescale");
            TimescaleConfig timescaleConfig = filterChain.getTimescaleConfig();
            timescaleConfig.setSpeed(timescale.getFloat("speed"));
            timescaleConfig.setPitch(timescale.getFloat("pitch"));
            timescaleConfig.setRate(timescale.getFloat("rate"));
        }
        if (json.has("tremolo")) {
            JSONObject tremolo = json.getJSONObject("tremolo");
            TremoloConfig tremoloConfig = filterChain.getTremoloConfig();
            tremoloConfig.setFrequency(tremolo.getFloat("frequency"));
            tremoloConfig.setDepth(tremolo.getFloat("depth"));
        }
        if (json.has("vibrato")) {
            JSONObject vibrato = json.getJSONObject("vibrato");
            VibratoConfig vibratoConfig = filterChain.getVibratoConfig();
            vibratoConfig.setFrequency(vibrato.getFloat("frequency"));
            vibratoConfig.setDepth(vibrato.getFloat("depth"));
        }
        if (json.has("volume")) {
            VolumeConfig volumeConfig = filterChain.getVolumeConfig();
            volumeConfig.setVolume(json.getFloat("volume"));
        }
        filterChain.getPlayer().setFilterFactory(filterChain.factory());
    }
}
