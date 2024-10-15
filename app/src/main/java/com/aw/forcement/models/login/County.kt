import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2023 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class County (

	@SerializedName("id") val id : Int,
	@SerializedName("countyID") val countyID : Int,
	@SerializedName("countyName") val countyName : String,
	@SerializedName("tagline") val tagline : String,
	@SerializedName("smsusername") val smsusername : String,
	@SerializedName("smspassword") val smspassword : String,
	@SerializedName("contact") val contact : String,
	@SerializedName("center") val center : String,
	@SerializedName("address") val address : String,
	@SerializedName("email") val email : String,
	@SerializedName("headline") val headline : String,
	@SerializedName("tagline2") val tagline2 : String,
	@SerializedName("ussd") val ussd : String,
	@SerializedName("logo") val logo : String,
	@SerializedName("bank") val bank : String,
	@SerializedName("powered") val powered : String,
	@SerializedName("stateLogo") val stateLogo : String,
	@SerializedName("seal") val seal : String,
	@SerializedName("signature") val signature : String,
	@SerializedName("lat") val lat : String,
	@SerializedName("lng") val lng : String,
	@SerializedName("link") val link : String,
	@SerializedName("paysol") val paysol : String,
	@SerializedName("poweredByLogo") val poweredByLogo : String,
	@SerializedName("smsservice") val smsservice : String,
	@SerializedName("rabbitmq") val rabbitmq : String,
	@SerializedName("clampingDuration") val clampingDuration : String,
	@SerializedName("rabbitQue") val rabbitQue : Boolean,
	@SerializedName("loginOTP") val loginOTP : String
)