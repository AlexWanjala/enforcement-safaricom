import com.google.gson.annotations.SerializedName


data class Queries (

	@SerializedName("id") val id : Int,
	@SerializedName("queryfor") val queryfor : String,
	@SerializedName("whichitem") val whichitem : String,
	@SerializedName("latitude") val latitude : String,
	@SerializedName("longitude") val longitude : String,
	@SerializedName("numberQueried") val numberQueried : String,
	@SerializedName("currentState") val currentState : String,
	@SerializedName("dateCreated") val dateCreated : String,
	@SerializedName("idNo") val idNo : String,
	@SerializedName("username") val username : String,
	@SerializedName("addressString") val addressString : String
)