import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2023 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Transactions (

	@SerializedName("id") val id : Int,
	@SerializedName("account_to") val account_to : String,
	@SerializedName("account_from") val account_from : String,
	@SerializedName("amount") val amount : String,
	@SerializedName("ref") val ref : String,
	@SerializedName("transaction_code") val transaction_code : String,
	@SerializedName("payment_mode") val payment_mode : String,
	@SerializedName("company") val company : String,
	@SerializedName("status") val status : String,
	@SerializedName("merchant_request_id") val merchant_request_id : String,
	@SerializedName("date") val date : String,
	@SerializedName("balance") val balance : String,
	@SerializedName("names") val names : String,
	@SerializedName("email") val email : String,
	@SerializedName("posted") val posted : String,
	@SerializedName("postedToEric") val postedToEric : String,
	@SerializedName("TransactionDesc") val transactionDesc : String,
	@SerializedName("zone") val zone : String,
	@SerializedName("street") val street : String,
	@SerializedName("user_id") val user_id : String,
	@SerializedName("username") val username : String,
	@SerializedName("user_full_name") val user_full_name : String,
	@SerializedName("msisdn") val msisdn : String,
	@SerializedName("postedToBiller") val postedToBiller : String,
	@SerializedName("queried") val queried : String,
	@SerializedName("postedToHealth") val postedToHealth : String,
	@SerializedName("currency") val currency : String,
	@SerializedName("message") val message : String,
	@SerializedName("response") val response : String,
	@SerializedName("remarks") val remarks : String
)