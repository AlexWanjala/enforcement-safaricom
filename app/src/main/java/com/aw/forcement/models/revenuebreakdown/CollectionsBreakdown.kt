import com.google.gson.annotations.SerializedName


data class CollectionsBreakdown (
	@SerializedName("id") val id : Int,
	@SerializedName("title") val title : String,
	@SerializedName("time") val time : String,
	@SerializedName("amount") val amount : String
)