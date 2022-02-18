package lavalink.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by napster on 05.03.18.
 */
@ConfigurationProperties(prefix = "lavalink.server.sources")
@Component
public class AudioSourcesConfig {
    private boolean local = false;
    private boolean http = true;

    //Thirdparty
    private boolean applemusic = true;
    private boolean deezer = true;
    private boolean napster = true;
    private boolean spotify = true;
    private boolean tidal = true;
    private boolean yandex = true;

    //Remote
    private boolean audioboom = true;
    private boolean bandcamp = true;
    private boolean bandlab = true;
    private boolean bilibili = true;
    private boolean clyp = true;
    private boolean getyarn = true;
    private boolean iheart = true;
    private boolean instagram = true;
    private boolean jamendo = true;
    private boolean mixcloud = true;
    private boolean newgrounds = true;
    private boolean ocremix = true;
    private boolean odysee = true;
    private boolean reddit = true;
    private boolean smule = true;
    private boolean soundcloud = true;
    private boolean streamable = true;
    private boolean tiktok = true;
    private boolean tunein = true;
    private boolean twitch = true;
    private boolean vimeo = true;
    private boolean youtube = true;

    public boolean isApplemusic() {
        return applemusic;
    }

    public void setApplemusic(boolean applemusic) {
        this.applemusic = applemusic;
    }

    public boolean isDeezer() {
        return deezer;
    }

    public void setDeezer(boolean deezer) {
        this.deezer = deezer;
    }

    public boolean isNapster() {
        return napster;
    }

    public void setNapster(boolean napster) {
        this.napster = napster;
    }

    public boolean isSpotify() {
        return spotify;
    }

    public void setSpotify(boolean spotify) {
        this.spotify = spotify;
    }

    public boolean isTidal() {
        return tidal;
    }

    public void setTidal(boolean tidal) {
        this.tidal = tidal;
    }

    public boolean isYandex() {
        return yandex;
    }

    public void setYandex(boolean yandex) {
        this.yandex = yandex;
    }

    

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isHttp() {
        return http;
    }

    public void setHttp(boolean http) {
        this.http = http;
    }


    public boolean isAudioboom() {
        return audioboom;
    }

    public void setAudioboom(boolean audioboom) {
        this.audioboom = audioboom;
    }

    public boolean isBandcamp() {
        return bandcamp;
    }

    public void setBandcamp(boolean bandcamp) {
        this.bandcamp = bandcamp;
    }

    public boolean isBandlab() {
        return bandlab;
    }

    public void setBandlab(boolean bandlab) {
        this.bandlab = bandlab;
    }

    public boolean isBilibili() {
        return bilibili;
    }

    public void setBilibili(boolean bilibili) {
        this.bilibili = bilibili;
    }

    public boolean isClyp() {
        return clyp;
    }

    public void setClyp(boolean clyp) {
        this.clyp = clyp;
    }

    public boolean isGetyarn() {
        return getyarn;
    }

    public void setGetyarn(boolean getyarn) {
        this.getyarn = getyarn;
    }

    public boolean isIheart() {
        return iheart;
    }

    public void setIheart(boolean iheart) {
        this.iheart = iheart;
    }

    public boolean isInstagram() {
        return instagram;
    }

    public void setInstagram(boolean instagram) {
        this.instagram = instagram;
    }

    public boolean isJamendo() {
        return jamendo;
    }

    public void setJamendo(boolean jamendo) {
        this.jamendo = jamendo;
    }

    public boolean isMixcloud() {
        return mixcloud;
    }

    public void setMixcloud(boolean mixcloud) {
        this.mixcloud = mixcloud;
    }

    public boolean isNewgrounds() {
        return newgrounds;
    }

    public void setNewgrounds(boolean newgrounds) {
        this.newgrounds = newgrounds;
    }

    public boolean isOcremix() {
        return ocremix;
    }

    public void setOcremix(boolean ocremix) {
        this.ocremix = ocremix;
    }

    public boolean isOdysee() {
        return odysee;
    }

    public void setOdysee(boolean odysee) {
        this.odysee = odysee;
    }

    public boolean isReddit() {
        return reddit;
    }

    public void setReddit(boolean reddit) {
        this.reddit = reddit;
    }

    public boolean isSmule() {
        return smule;
    }

    public void setSmule(boolean smule) {
        this.smule = smule;
    }

    public boolean isStreamable() {
        return streamable;
    }

    public void setStreamable(boolean streamable) {
        this.streamable = streamable;
    }

    public boolean isTiktok() {
        return tiktok;
    }

    public void setTiktok(boolean tiktok) {
        this.tiktok = tiktok;
    }

    public boolean isTunein() {
        return tunein;
    }

    public void setTunein(boolean tunein) {
        this.tunein = tunein;
    }

    public boolean isSoundcloud() {
        return soundcloud;
    }

    public void setSoundcloud(boolean soundcloud) {
        this.soundcloud = soundcloud;
    }

    public boolean isTwitch() {
        return twitch;
    }

    public void setTwitch(boolean twitch) {
        this.twitch = twitch;
    }

    public boolean isVimeo() {
        return vimeo;
    }

    public void setVimeo(boolean vimeo) {
        this.vimeo = vimeo;
    }

    public boolean isYoutube() {
        return youtube;
    }

    public void setYoutube(boolean youtube) {
        this.youtube = youtube;
    }
}
