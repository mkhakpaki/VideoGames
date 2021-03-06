package ir.mkhakpaki.videogames.util

object Constants {
    const val NOT_FOUND = 404
    const val NETWORK_OK = 200
    const val NAME = "name"
    const val IMAGE_URL = "image_url"
    const val RATING = "rating"
    const val RELEASE_DATE = "release_date"
    const val IS_LIKED = "is_liked"
    const val CONNECT_TIMEOUT = 10
    const val READ_TIMEOUT = 15
    const val WRITE_TIMEOUT = 15
    const val KEEP_ALIVE_TIME = 14
    const val MAX_IDLE_CONNECTIONS = 5
}

object API_PATH {
    const val BASE_URL = "https://rawg-video-games-database.p.rapidapi.com"
    const val LIST_GAMES = "/games"
    const val GAMED_DETAILS = "/games/{id}"
}