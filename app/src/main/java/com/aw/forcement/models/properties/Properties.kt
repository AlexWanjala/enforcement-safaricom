import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2024 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Properties (

	@SerializedName("propertyID") val propertyID : String,
	@SerializedName("property") val property : String,
	@SerializedName("noOfOccupiedUnits") val noOfOccupiedUnits : String,
	@SerializedName("noOfEmptyUnits") val noOfEmptyUnits : String,
	@SerializedName("noOfUnits") val noOfUnits : String,
	@SerializedName("totalArrears") val totalArrears : String,
	@SerializedName("id") val id : Int,
	@SerializedName("UPN") val uPN : String,
	@SerializedName("LRNumber") val lRNumber : String,
	@SerializedName("subCountyName") val subCountyName : String,
	@SerializedName("subCountyID") val subCountyID : String,
	@SerializedName("wardName") val wardName : String,
	@SerializedName("wardID") val wardID : String,
	@SerializedName("percelSize") val percelSize : String,
	@SerializedName("lat") val lat : String,
	@SerializedName("lng") val lng : String,
	@SerializedName("percelAddress") val percelAddress : String,
	@SerializedName("ownerNames") val ownerNames : String,
	@SerializedName("ownerEmail") val ownerEmail : String,
	@SerializedName("ownerPhoneNo") val ownerPhoneNo : String,
	@SerializedName("outstandingArrears") val outstandingArrears : String,
	@SerializedName("annualCharges") val annualCharges : String,
	@SerializedName("createdBy") val createdBy : String,
	@SerializedName("createdByIDNo") val createdByIDNo : String,
	@SerializedName("dateCreated") val dateCreated : String,
	@SerializedName("zone") val zone : String,
	@SerializedName("block") val block : String,
	@SerializedName("useName") val useName : String,
	@SerializedName("updatedBy") val updatedBy : String
)