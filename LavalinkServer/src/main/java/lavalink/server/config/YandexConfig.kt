package lavalink.server.config

data class YandexConfig (
    var token: String = "",
    var proxyHost: String = "",
    var proxyPort: Int = 8080,
    var proxyLogin: String = "",
    var proxyPass: String = "",
    var proxyTimeout: Int = 10000
)