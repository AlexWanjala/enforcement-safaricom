import com.google.gson.annotations.SerializedName


data class Town (

	@SerializedName("TownId") val townId : String,
	@SerializedName("Town") val town : String
)