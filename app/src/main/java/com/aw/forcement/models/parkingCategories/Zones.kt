import com.google.gson.annotations.SerializedName


data class Zones (
	@SerializedName("zoneCode") val zoneCode : String,
	@SerializedName("id") val id : String,
@SerializedName("subCountyID") val subCountyID : String,
@SerializedName("subCountyName") val subCountyName : String,
@SerializedName("zone") val zone : String,
@SerializedName("wardID") val wardID : String,
@SerializedName("wardName") val wardName : String,
@SerializedName("lat") val lat : String,
@SerializedName("lng") val lng : String,
@SerializedName("zoneCategory") val zoneCategory : String,
@SerializedName("zoneCategoryID") val zoneCategoryID : String
)