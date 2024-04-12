import com.google.gson.annotations.SerializedName



data class FireSafety (
	@SerializedName("id") val id : Int,
	@SerializedName("category") val category : String
)