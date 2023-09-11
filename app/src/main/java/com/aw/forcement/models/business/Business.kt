import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2023 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Business (

	@SerializedName("id") val id : Int,
	@SerializedName("businessID") val businessID : String,
	@SerializedName("businessName") val businessName : String,
	@SerializedName("subCountyID") val subCountyID : String,
	@SerializedName("wardID") val wardID : String,
	@SerializedName("plotNumber") val plotNumber : String,
	@SerializedName("physicalAddress") val physicalAddress : String,
	@SerializedName("buildingName") val buildingName : String,
	@SerializedName("buildingOccupancy") val buildingOccupancy : String,
	@SerializedName("floorNo") val floorNo : String,
	@SerializedName("roomNo") val roomNo : String,
	@SerializedName("premiseSize") val premiseSize : String,
	@SerializedName("numberOfEmployees") val numberOfEmployees : String,
	@SerializedName("tonnage") val tonnage : String,
	@SerializedName("businessDes") val businessDes : String,
	@SerializedName("businessCategory") val businessCategory : String,
	@SerializedName("businessSubCategory") val businessSubCategory : String,
	@SerializedName("businessEmail") val businessEmail : String,
	@SerializedName("postalAddress") val postalAddress : String,
	@SerializedName("postalCode") val postalCode : String,
	@SerializedName("businessPhone") val businessPhone : String,
	@SerializedName("contactPersonNames") val contactPersonNames : String,
	@SerializedName("contactPersonIDNo") val contactPersonIDNo : String,
	@SerializedName("businessRole") val businessRole : String,
	@SerializedName("contactPersonPhone") val contactPersonPhone : String,
	@SerializedName("contactPersonEmail") val contactPersonEmail : String,
	@SerializedName("fullNames") val fullNames : String,
	@SerializedName("ownerID") val ownerID : String,
	@SerializedName("ownerPhone") val ownerPhone : String,
	@SerializedName("ownerEmail") val ownerEmail : String,
	@SerializedName("kraPin") val kraPin : String,
	@SerializedName("createdBy") val createdBy : String,
	@SerializedName("createdByIDNo") val createdByIDNo : String,
	@SerializedName("dateCreated") val dateCreated : String,
	@SerializedName("lat") val lat : String,
	@SerializedName("lng") val lng : String
)