import com.google.gson.annotations.SerializedName

data class Allparkingquery  (

	@SerializedName("queryfor")override var queryfor : String,
	@SerializedName("whichitem")override val whichitem : String,
	@SerializedName("latitude")override val latitude : String,
	@SerializedName("longitude") override val longitude : String,
	@SerializedName("dateCreated") override val dateCreated : String,
	@SerializedName("numberQueried")override val numberQueried : String,
	@SerializedName("currentState")override var currentState : String
) : BaseObject()