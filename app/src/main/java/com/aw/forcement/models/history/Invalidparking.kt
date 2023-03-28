import com.google.gson.annotations.SerializedName


data class Invalidparking (
	@SerializedName("queryfor") override var queryfor : String,
	@SerializedName("whichitem") override val whichitem : String,
	@SerializedName("Latitude") override val latitude : String,
	@SerializedName("Longitude") override val longitude : String,
	@SerializedName("DateCreated") override val dateCreated : String,
	@SerializedName("NumberQueried")override val numberQueried : String,
	@SerializedName("CurrentState") override var currentState : String
): BaseObject()