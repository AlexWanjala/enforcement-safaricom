import com.google.gson.annotations.SerializedName


data class Clampreasons (

	@SerializedName("id") val id : Int,
	@SerializedName("reasonCode") val reasonCode : String,
	@SerializedName("reason") val reason : String
)