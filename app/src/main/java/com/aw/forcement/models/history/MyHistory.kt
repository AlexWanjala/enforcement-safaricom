import com.google.gson.annotations.SerializedName

data class MyHistory (

	@SerializedName("id") val id : Int,
	@SerializedName("title") val title : String,
	@SerializedName("time") val time : String,
	@SerializedName("description") val description : String,
	@SerializedName("amount") val amount : String
)