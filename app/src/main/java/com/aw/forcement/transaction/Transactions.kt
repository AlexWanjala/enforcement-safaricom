import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2023 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Transactions (

	@SerializedName("id") val id : Int,
	@SerializedName("payment_type") val payment_type : String,
	@SerializedName("account_to") val account_to : String,
	@SerializedName("account_from") val account_from : String,
	@SerializedName("transaction_code") val transaction_code : String,
	@SerializedName("amount") val amount : String,
	@SerializedName("account_ref") val account_ref : String,
	@SerializedName("transaction_desc") val transaction_desc : String,
	@SerializedName("date") val date : String,
	@SerializedName("names") val names : String,
	@SerializedName("verified") val verified : Boolean,
	@SerializedName("verifiedBy") val verifiedBy : String,
	@SerializedName("code") val code : String,
)