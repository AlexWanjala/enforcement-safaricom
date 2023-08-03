import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2023 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class ReceiptInfos (

	@SerializedName("id") val id : Int,
	@SerializedName("receiptNo") val receiptNo : String,
	@SerializedName("billNo") val billNo : String,
	@SerializedName("billTotal") val billTotal : String,
	@SerializedName("receiptAmount") val receiptAmount : Double,
	@SerializedName("billBalance") val billBalance : String,
	@SerializedName("customer") val customer : String,
	@SerializedName("clientPhoneNo") val clientPhoneNo : String,
	@SerializedName("description") val description : String,
	@SerializedName("receiptDate") val receiptDate : String,
	@SerializedName("printCount") val printCount : String,
	@SerializedName("wardID") val wardID : String,
	@SerializedName("subCountyID") val subCountyID : String,
	@SerializedName("subCountyName") val subCountyName : String,
	@SerializedName("wardName") val wardName : String,
	@SerializedName("dateCreated") val dateCreated : String,
	@SerializedName("dateModified") val dateModified : String,
	@SerializedName("createdBy") val createdBy : String,
	@SerializedName("modifiedBy") val modifiedBy : String,
	@SerializedName("printedBy") val printedBy : String,
	@SerializedName("updated") val updated : String,
	@SerializedName("isActive") val isActive : Int,
	@SerializedName("status") val status : String,
	@SerializedName("zone") val zone : String,
	@SerializedName("departmentID") val departmentID : String,
	@SerializedName("idNo") val idNo : String,
	@SerializedName("phoneNumber") val phoneNumber : String,
	@SerializedName("names") val names : String,
	@SerializedName("customerPhoneNumber") val customerPhoneNumber : String
)