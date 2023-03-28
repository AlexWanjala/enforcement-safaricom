import com.google.gson.annotations.SerializedName

data class Json4Kotlin_Base (

	@SerializedName("status") val status : Int,
	@SerializedName("success") val success : Boolean,
	@SerializedName("message") val message : String,
	@SerializedName("data") val data : Data
)