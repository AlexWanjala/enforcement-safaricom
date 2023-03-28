import com.google.gson.annotations.SerializedName


abstract class BaseObject {
	abstract var queryfor : String
	abstract val whichitem : String
	abstract val latitude : String
	abstract val longitude : String
	abstract val dateCreated : String
	abstract val numberQueried : String
	abstract var currentState : String
}