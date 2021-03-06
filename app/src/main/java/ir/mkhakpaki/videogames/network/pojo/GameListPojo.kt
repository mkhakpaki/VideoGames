package ir.mkhakpaki.videogames.network.pojo

import com.google.gson.annotations.SerializedName

class GameListPojo {

    @SerializedName("count")
    var totalCount:Long? = null

    @SerializedName("next")
    var nextPage:String? = null

    @SerializedName("previous")
    var previousPage:String? = null

    @SerializedName("results")
    var gameList:List<GamePojo>? = null
}