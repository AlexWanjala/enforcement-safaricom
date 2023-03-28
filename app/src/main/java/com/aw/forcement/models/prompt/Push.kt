import com.google.gson.annotations.SerializedName


data class Push (

	@SerializedName("ref") val ref : String,
	@SerializedName("callback_returned") val callback_returned : String,
	@SerializedName("message") val message : String,
	@SerializedName("transaction_code") val transaction_code : String,
	@SerializedName("amount") val amount : String
)