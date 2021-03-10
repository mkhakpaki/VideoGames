package ir.mkhakpaki.videogames.util

object Constants {
    const val VIEW_TYPE_LOADING = 1
    const val VIEW_TYPE_ERROR = 2
    const val TYPE_GAME_ITEM_LIST = 4
    const val NAME = "name"
    const val GAME_ID = "game_id"
    const val IMAGE_URL = "image_url"
    const val RATING = "rating"
    const val RELEASE_DATE = "release_date"
    const val IS_LIKED = "is_liked"
    const val CONNECT_TIMEOUT = 5
    const val READ_TIMEOUT = 10
    const val WRITE_TIMEOUT = 10
    const val KEEP_ALIVE_TIME = 10
    const val MAX_IDLE_CONNECTIONS = 5
    const val LIST_THRESHOLD = 6
    const val NEXT_PAGE_TO_REQUEST = "NEXT_PAGE_TO_REQUEST"
}

object API_PATH {
    const val BASE_URL = "https://rawg-video-games-database.p.rapidapi.com"
    const val LIST_GAMES = "/games"
    const val GAMED_DETAILS = "/games/{id}"
}