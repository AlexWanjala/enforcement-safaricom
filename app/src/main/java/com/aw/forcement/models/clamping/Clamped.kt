import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2023 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Clamped (

	@SerializedName("id") val id : Int,
	@SerializedName("billNo") val billNo : String,
	@SerializedName("detailAmount") val detailAmount : String,
	@SerializedName("receiptAmount") val receiptAmount : String,
	@SerializedName("billBalance") val billBalance : String,
	@SerializedName("numberPlate") val numberPlate : String,
	@SerializedName("lat") val lat : String,
	@SerializedName("lng") val lng : String,
	@SerializedName("address") val address : String,
	@SerializedName("idNo") val idNo : String,
	@SerializedName("names") val names : String,
	@SerializedName("phoneNumber") val phoneNumber : String,
	@SerializedName("dateCreated") val dateCreated : String,
	@SerializedName("status") val status : String,
	@SerializedName("zone") val zone : String,
	@SerializedName("subCountyID") val subCountyID : String,
	@SerializedName("subCountyName") val subCountyName : String,
	@SerializedName("wardID") val wardID : String,
	@SerializedName("wardName") val wardName : String,
	@SerializedName("accountDesc") val accountDesc : String,
	@SerializedName("feeDescription") val feeDescription : String,
	@SerializedName("transactionCode") val transactionCode : String,
	@SerializedName("paidBy") val paidBy : String,
	@SerializedName("paymentMode") val paymentMode : String,
	@SerializedName("clampedStatus") val clampedStatus : String
)