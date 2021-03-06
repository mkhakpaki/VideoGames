package ir.mkhakpaki.videogames.network.pojo

import com.google.gson.annotations.SerializedName

class GamePojo {
    @SerializedName("id")
    var id:Long? = null

    @SerializedName("name")
    var name:String? = null

    @SerializedName("name_original")
    var originalName: String? = null

    @SerializedName("description")
    var description: String? = null

    @SerializedName("metacritic")
    var metacritic: Int? = null

    @SerializedName("background_image")
    var backgroundImage: String? = null

    @SerializedName("rating")
    var rating: Float? = null

    @SerializedName("released")
    var releaseDate: String? = null


}