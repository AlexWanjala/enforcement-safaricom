import com.google.gson.annotations.SerializedName

data class History (

	@SerializedName("allparkingquery") val allparkingquery : List<Allparkingquery>,
	@SerializedName("validparking") val validparking : List<Validparking>,
	@SerializedName("invalidparking") val invalidparking : List<Invalidparking>,
	@SerializedName("clampedparking") val clampedparking : List<Clampedparking>,
	@SerializedName("allbusinesses") val allbusinesses : List<Allbusinesses>,
	@SerializedName("validbusinesses") val validbusinesses : List<Validbusinesses>,
	@SerializedName("invalidbusinesses") val invalidbusinesses : List<Invalidbusinesses>,
	@SerializedName("allreceipts") val allreceipts : List<Allreceipts>,
	@SerializedName("validreceipts") val validreceipts : List<Validreceipts>,
	@SerializedName("invalidreceipts") val invalidreceipts : List<Invalidreceipts>
)