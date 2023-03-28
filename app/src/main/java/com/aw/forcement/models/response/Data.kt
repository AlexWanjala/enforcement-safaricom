import com.google.gson.annotations.SerializedName

data class Data (

	@SerializedName("userdata") val userdata : Userdata,
	@SerializedName("parking") val parking : Parking,
	@SerializedName("clampreasons") val clampreasons : List<Clampreasons>,
	@SerializedName("receiptDetails") val receiptDetails : ReceiptDetails,
	@SerializedName("receiptInfos") val receiptInfos : List<ReceiptInfos>,
	@SerializedName("permit") val permit : Permit,
	@SerializedName("queries") val queries : List<Queries>,
	@SerializedName("Transactions") val transactions : List<Transactions>,
	@SerializedName("shortcodes") val shortcodes : List<Shortcodes>,
	@SerializedName("Push") val push : Push,
	@SerializedName("incomeTypes") val incomeTypes : List<IncomeTypes>,
	@SerializedName("feesAndCharges") val feesAndCharges : List<FeesAndCharges>,
	@SerializedName("billGenerated") val billGenerated : BillGenerated,
	@SerializedName("zones") val zones : List<Zones>,
	@SerializedName("categories") val categories : List<Categories>,
	@SerializedName("durations") val durations : List<Durations>,
	@SerializedName("billDetails") val billDetails : BillDetails,
	@SerializedName("billInfo") val billInfo : List<BillInfo>,
	@SerializedName("addresses") val addresses : List<Addresses>,
	@SerializedName("wards") val wards : List<Wards>,
	@SerializedName("subCounties") val subCounties : List<SubCounties>,
	@SerializedName("tradeCategories") val tradeCategories : List<TradeCategories>,
	@SerializedName("tradeSubCategories") val tradeSubCategories : List<TradeSubCategories>

)