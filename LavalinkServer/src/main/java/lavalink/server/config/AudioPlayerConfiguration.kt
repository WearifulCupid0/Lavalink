package lavalink.server.config

import com.sedmelluq.lavaplayer.extensions.thirdpartysources.applemusic.AppleMusicAudioSourceManager
import com.sedmelluq.lavaplayer.extensions.thirdpartysources.deezer.DeezerAudioSourceManager
import com.sedmelluq.lavaplayer.extensions.thirdpartysources.napster.NapsterAudioSourceManager
import com.sedmelluq.lavaplayer.extensions.thirdpartysources.spotify.SpotifyAudioSourceManager
import com.sedmelluq.lavaplayer.extensions.thirdpartysources.tidal.TidalAudioSourceManager
import com.sedmelluq.lavaplayer.extensions.thirdpartysources.yamusic.YandexMusicAudioSourceManager
import com.sedmelluq.lavaplayer.extensions.thirdpartysources.yamusic.YandexHttpContextFilter
import com.sedmelluq.discord.lavaplayer.container.MediaContainerRegistry
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.bandlab.BandlabAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.bilibili.BilibiliAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.clyp.ClypAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.getyarn.GetyarnAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.iheart.iHeartAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.instagram.InstagramAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.mixcloud.MixcloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.jamendo.JamendoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.newgrounds.NewgroundsAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.ocremix.OcremixAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.odysee.OdyseeAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.reddit.RedditAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.*
import com.sedmelluq.discord.lavaplayer.source.streamable.StreamableAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.tiktok.TiktokAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.tunein.TuneinAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitter.TwitterAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import com.sedmelluq.lavaplayer.extensions.format.xm.XmContainerProbe
import com.sedmelluq.lava.extensions.youtuberotator.YoutubeIpRotatorSetup
import com.sedmelluq.lava.extensions.youtuberotator.planner.*
import com.sedmelluq.lava.extensions.youtuberotator.tools.ip.Ipv4Block
import com.sedmelluq.lava.extensions.youtuberotator.tools.ip.Ipv6Block
import dev.arbjerg.lavalink.api.AudioPlayerManagerConfiguration
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.BasicCredentialsProvider
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetAddress
import java.util.function.Predicate

/**
 * Created by napster on 05.03.18.
 */
@Configuration
class AudioPlayerConfiguration {

    private val log = LoggerFactory.getLogger(AudioPlayerConfiguration::class.java)

    @Bean
    fun audioPlayerManagerSupplier(
        sources: AudioSourcesConfig,
        search: AudioSearchConfig,
        serverConfig: ServerConfig,
        routePlanner: AbstractRoutePlanner?,
        audioSourceManagers: Collection<AudioSourceManager>,
        audioPlayerManagerConfigurations: Collection<AudioPlayerManagerConfiguration>
    ): AudioPlayerManager {
        val audioPlayerManager = DefaultAudioPlayerManager()

        if (serverConfig.isGcWarnings) {
            audioPlayerManager.enableGcMonitoring()
        }

        val defaultFrameBufferDuration = audioPlayerManager.frameBufferDuration
        serverConfig.frameBufferDurationMs?.let {
            if (it < 200) { // At the time of writing, LP enforces a minimum of 200ms.
                log.warn("Buffer size of {}ms is illegal. Defaulting to {}", it, defaultFrameBufferDuration)
            }

            val bufferDuration = it.takeIf { it >= 200 } ?: defaultFrameBufferDuration
            log.debug("Setting frame buffer duration to {}", bufferDuration)
            audioPlayerManager.frameBufferDuration = bufferDuration
        }

        if (sources.isYoutube) {
            val youtube = YoutubeAudioSourceManager(search.isYoutube, serverConfig.youtubeConfig?.email, serverConfig.youtubeConfig?.password)

            if (routePlanner != null) {
                val retryLimit = serverConfig.ratelimit?.retryLimit ?: -1
                when {
                    retryLimit < 0 -> YoutubeIpRotatorSetup(routePlanner).forSource(youtube).setup()
                    retryLimit == 0 -> YoutubeIpRotatorSetup(routePlanner).forSource(youtube)
                        .withRetryLimit(Int.MAX_VALUE).setup()
                    else -> YoutubeIpRotatorSetup(routePlanner).forSource(youtube).withRetryLimit(retryLimit).setup()

                }
            }

            audioPlayerManager.registerSourceManager(youtube)
        }
        if (sources.isSoundcloud) {
            val dataReader = DefaultSoundCloudDataReader()
            val dataLoader = DefaultSoundCloudDataLoader()
            val formatHandler = DefaultSoundCloudFormatHandler()

            audioPlayerManager.registerSourceManager(
                SoundCloudAudioSourceManager(
                    search.isSoundcloud,
                    dataReader,
                    dataLoader,
                    formatHandler,
                    DefaultSoundCloudPlaylistLoader(dataLoader, dataReader, formatHandler)
                )
            )
        }
        if (sources.isBandcamp) audioPlayerManager.registerSourceManager(BandcampAudioSourceManager())
        if (sources.isBandlab) audioPlayerManager.registerSourceManager(BandlabAudioSourceManager())
        if (sources.isBilibili) audioPlayerManager.registerSourceManager(BilibiliAudioSourceManager(search.isBilibili))
        if (sources.isClyp) audioPlayerManager.registerSourceManager(ClypAudioSourceManager())
        if (sources.isGetyarn) audioPlayerManager.registerSourceManager(GetyarnAudioSourceManager())
        if (sources.isIheart) audioPlayerManager.registerSourceManager(iHeartAudioSourceManager(search.isIheart))
        if (sources.isInstagram) audioPlayerManager.registerSourceManager(InstagramAudioSourceManager())
        if (sources.isJamendo) audioPlayerManager.registerSourceManager(JamendoAudioSourceManager(search.isJamendo))
        if (sources.isMixcloud) audioPlayerManager.registerSourceManager(MixcloudAudioSourceManager(search.isMixcloud))
        if (sources.isNewgrounds) audioPlayerManager.registerSourceManager(NewgroundsAudioSourceManager())
        if (sources.isOcremix) audioPlayerManager.registerSourceManager(OcremixAudioSourceManager())
        if (sources.isOdysee) audioPlayerManager.registerSourceManager(OdyseeAudioSourceManager(search.isOdysee))
        if (sources.isReddit) audioPlayerManager.registerSourceManager(RedditAudioSourceManager())
        if (sources.isStreamable) audioPlayerManager.registerSourceManager(StreamableAudioSourceManager())
        if (sources.isTiktok) audioPlayerManager.registerSourceManager(TiktokAudioSourceManager())
        if (sources.isTunein) audioPlayerManager.registerSourceManager(TuneinAudioSourceManager())
        if (sources.isTwitch) audioPlayerManager.registerSourceManager(TwitchStreamAudioSourceManager())
        if (sources.isTwitter) audioPlayerManager.registerSourceManager(TwitterAudioSourceManager())
        if (sources.isVimeo) audioPlayerManager.registerSourceManager(VimeoAudioSourceManager())
        if (sources.isLocal) audioPlayerManager.registerSourceManager(LocalAudioSourceManager())

        if (sources.isApplemusic) audioPlayerManager.registerSourceManager(AppleMusicAudioSourceManager(search.isApplemusic, audioPlayerManager))
        if (sources.isDeezer) audioPlayerManager.registerSourceManager(DeezerAudioSourceManager(search.isDeezer, audioPlayerManager))
        if (sources.isNapster) audioPlayerManager.registerSourceManager(NapsterAudioSourceManager(search.isNapster, audioPlayerManager))
        if (sources.isSpotify) audioPlayerManager.registerSourceManager(SpotifyAudioSourceManager(search.isSpotify, audioPlayerManager))
        if (sources.isTidal) audioPlayerManager.registerSourceManager(TidalAudioSourceManager(search.isTidal, audioPlayerManager))
        if (sources.isYandex) {
            val yandexConfig = serverConfig.yandexConfig
            val yandex = YandexMusicAudioSourceManager(search.isYandex, audioPlayerManager)

            if (yandexConfig != null) {
                if (yandexConfig.proxyHost.isNotBlank() && yandexConfig.proxyTimeout != -1) {
                    val credentials = UsernamePasswordCredentials(yandexConfig.proxyLogin, yandexConfig.proxyPass)
                    val credsProvider = BasicCredentialsProvider()
                    val authScope = AuthScope(yandexConfig.proxyHost, yandexConfig.proxyPort)
                    credsProvider.setCredentials(authScope, credentials)
                    yandex.configureBuilder { builder ->
                        builder.setProxy(HttpHost(yandexConfig.proxyHost, yandexConfig.proxyPort))
                        .setDefaultCredentialsProvider(credsProvider) }
                    yandex.configureRequests {
                        RequestConfig.copy(it).apply {
                            setConnectTimeout(yandexConfig.proxyTimeout)
                            setSocketTimeout(yandexConfig.proxyTimeout)
                            setConnectionRequestTimeout(yandexConfig.proxyTimeout)
                        }.build()
                    }
                }
            
                if (yandexConfig.token.isNotBlank()) {
                    YandexHttpContextFilter.setOAuthToken(yandexConfig.token)
                }
            }

            audioPlayerManager.registerSourceManager(yandex)
        }

        audioSourceManagers.forEach {
            audioPlayerManager.registerSourceManager(it)
            log.info("Registered {} provided from a plugin", it)
        }

        audioPlayerManager.configuration.isFilterHotSwapEnabled = true

        var am: AudioPlayerManager = audioPlayerManager

        audioPlayerManagerConfigurations.forEach {
            am = it.configure(am)
        }

        // This must be loaded last
        if (sources.isHttp) {
            val httpAudioConfig = serverConfig.httpAudioConfig
            val http = HttpAudioSourceManager(MediaContainerRegistry.extended(XmContainerProbe()))

            if (httpAudioConfig != null) {
                if (httpAudioConfig.proxyHost.isNotBlank() && httpAudioConfig.proxyTimeout != -1) {
                    val credentials = UsernamePasswordCredentials(httpAudioConfig.proxyLogin, httpAudioConfig.proxyPass)
                    val credsProvider = BasicCredentialsProvider()
                    val authScope = AuthScope(httpAudioConfig.proxyHost, httpAudioConfig.proxyPort)
                    credsProvider.setCredentials(authScope, credentials)
                    http.configureBuilder { builder ->
                    builder.setProxy(HttpHost(httpAudioConfig.proxyHost, httpAudioConfig.proxyPort))
                            .setDefaultCredentialsProvider(credsProvider) }
                    http.configureRequests {
                        RequestConfig.copy(it).apply {
                            setConnectTimeout(httpAudioConfig.proxyTimeout)
                            setSocketTimeout(httpAudioConfig.proxyTimeout)
                            setConnectionRequestTimeout(httpAudioConfig.proxyTimeout)
                        }.build()
                    }
                }
            }
            
            audioPlayerManager.registerSourceManager(http)
        }

        return am
    }

    @Bean
    fun routePlanner(serverConfig: ServerConfig): AbstractRoutePlanner? {
        val rateLimitConfig = serverConfig.ratelimit
        if (rateLimitConfig == null) {
            log.debug("No rate limit config block found, skipping setup of route planner")
            return null
        }
        val ipBlockList = rateLimitConfig.ipBlocks
        if (ipBlockList.isEmpty()) {
            log.info("List of ip blocks is empty, skipping setup of route planner")
            return null
        }

        val blacklisted = rateLimitConfig.excludedIps.map { InetAddress.getByName(it) }
        val filter = Predicate<InetAddress> {
            !blacklisted.contains(it)
        }
        val ipBlocks = ipBlockList.map {
            when {
                Ipv4Block.isIpv4CidrBlock(it) -> Ipv4Block(it)
                Ipv6Block.isIpv6CidrBlock(it) -> Ipv6Block(it)
                else -> throw RuntimeException("Invalid IP Block '$it', make sure to provide a valid CIDR notation")
            }
        }

        return when (rateLimitConfig.strategy.toLowerCase().trim()) {
            "rotateonban" -> RotatingIpRoutePlanner(ipBlocks, filter, rateLimitConfig.searchTriggersFail)
            "loadbalance" -> BalancingIpRoutePlanner(ipBlocks, filter, rateLimitConfig.searchTriggersFail)
            "nanoswitch" -> NanoIpRoutePlanner(ipBlocks, rateLimitConfig.searchTriggersFail)
            "rotatingnanoswitch" -> RotatingNanoIpRoutePlanner(ipBlocks, filter, rateLimitConfig.searchTriggersFail)
            else -> throw RuntimeException("Unknown strategy!")
        }
    }

}
