import com.google.gson.annotations.SerializedName


data class Durations (

	@SerializedName("id") val id : Int,
	@SerializedName("duration") val duration : String,
	@SerializedName("days") val days : Int
)