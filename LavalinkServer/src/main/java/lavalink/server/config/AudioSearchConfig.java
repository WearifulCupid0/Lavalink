package lavalink.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "lavalink.server.search")
@Component
public class AudioSearchConfig {

    //Thirdparty
    private boolean applemusic = true;
    private boolean deezer = true;
    private boolean napster = true;
    private boolean spotify = true;
    private boolean tidal = true;
    private boolean yandex = true;

    //Remote
    private boolean bilibili = true;
    private boolean iheart = true;
    private boolean jamendo = true;
    private boolean mixcloud = true;
    private boolean odysee = true;
    private boolean soundcloud = true;
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



    public boolean isBilibili() {
        return bilibili;
    }

    public void setBilibili(boolean bilibili) {
        this.bilibili = bilibili;
    }

    public boolean isIheart() {
        return iheart;
    }

    public void setIheart(boolean iheart) {
        this.iheart = iheart;
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

    public boolean isOdysee() {
        return odysee;
    }

    public void setOdysee(boolean odysee) {
        this.odysee = odysee;
    }

    public boolean isSoundcloud() {
        return soundcloud;
    }

    public void setSoundcloud(boolean soundcloud) {
        this.soundcloud = soundcloud;
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
