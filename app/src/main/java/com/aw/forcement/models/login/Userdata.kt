import com.google.gson.annotations.SerializedName

	data class Userdata (
		@SerializedName("id") val id : String,
		@SerializedName("firstName") val firstName : String,
		@SerializedName("lastName") val lastName : String,
		@SerializedName("otherName") val otherName : String,
		@SerializedName("email") val email : String,
		@SerializedName("phone") val phone : String,
		@SerializedName("idNo") val idNo : String,
		@SerializedName("password") val password : String
	)