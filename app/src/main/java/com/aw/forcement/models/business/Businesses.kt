import com.google.gson.annotations.SerializedName


data class Businesses (
	@SerializedName("id") val id : String,
	@SerializedName("businessID") val businessID : String,
	@SerializedName("businessName") val businessName : String,
	@SerializedName("receiptAmount") val receiptAmount : String,
	@SerializedName("statusID") val statusID : String,
	@SerializedName("status") val status : String
)