import com.google.gson.annotations.SerializedName

data class Enforcebyplatenumber (

	@SerializedName("IdentifyNo") val identifyNo : String,
	@SerializedName("Customer") val customer : String,
	@SerializedName("BillNo") val billNo : String,
	@SerializedName("BillTotal") val billTotal : String,
	@SerializedName("ReducingBalance") val reducingBalance : String,
	@SerializedName("feeDescription") val feeDescription : String,
	@SerializedName("accountDesc") val accountDesc : String,
	@SerializedName("ReceiptNo") val receiptNo : String,
	@SerializedName("billingstatus") val billingstatus : String
)