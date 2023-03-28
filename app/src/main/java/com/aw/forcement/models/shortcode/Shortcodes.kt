import com.google.gson.annotations.SerializedName

data class Shortcodes (

	@SerializedName("id") val id : Int,
	@SerializedName("short_code") val short_code : String,
	@SerializedName("business_name") val business_name : String
)