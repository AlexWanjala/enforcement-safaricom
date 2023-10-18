import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2023 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class BillItem (

	@SerializedName("id") val id : Int,
	@SerializedName("feeId") val feeId : String,
	@SerializedName("feeDescription") val feeDescription : String,
	@SerializedName("unitOfMeasure") val unitOfMeasure : String,
	@SerializedName("unitFeeAmount") val unitFeeAmount : String,
	@SerializedName("accountNo") val accountNo : String,
	@SerializedName("incomeTypeId") val incomeTypeId : String,
	@SerializedName("feeType") val feeType : String,
	@SerializedName("costCenterNo") val costCenterNo : String,
	@SerializedName("accountDesc") val accountDesc : String,
	@SerializedName("typeDescription") val typeDescription : String,
	@SerializedName("zone") val zone : String,
	@SerializedName("customer") var customer : String,
	@SerializedName("revenueStreamItem") var revenueStreamItem : String,
	@SerializedName("amount") var amount : String,
	@SerializedName("subCountyID") var subCountyID : String,
	@SerializedName("subCountyName") var subCountyName : String,
	@SerializedName("wardID") var wardID : String,
	@SerializedName("wardName") var wardName : String,
	@SerializedName("idNo") var idNo : String,
	@SerializedName("phoneNumber") var phoneNumber : String,
	@SerializedName("names") var names : String,
	@SerializedName("customerPhoneNumber") var customerPhoneNumber : String,
	@SerializedName("description") var description : String
)



