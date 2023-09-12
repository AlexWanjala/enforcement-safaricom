package com.aw.forcement.sbp.applications

import Const
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.aw.forcement.R
import kotlinx.android.synthetic.main.activity_application_verification_billing_information.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ApplicationVerificationBillingInformation : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_verification_billing_information)


        val originalDate = LocalDateTime.parse(Const.instance.getBill().billDetails.dateCreated.split(".")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val formattedDate = originalDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a"))


        tv_bill_no.text = Const.instance.getReceipt().receiptDetails.billNo
        tv_date_created.text = formattedDate
        tv_payment_status.text = Const.instance.getReceipt().receiptDetails.status
        tv_receipt_no.text = Const.instance.getReceipt().receiptDetails.transactionCode
        tv_payment_mode.text = Const.instance.getReceipt().receiptDetails.source
        tv_ref.text = Const.instance.getReceipt().receiptDetails.billNo
        tv_ref.text = Const.instance.getReceipt().receiptDetails.billNo
        tv_bill_created_by.text = Const.instance.getReceipt().receiptDetails.names
        tv_paid_by.text = Const.instance.getReceipt().receiptDetails.paidBy

        val original = LocalDateTime.parse(Const.instance.getReceipt().receiptDetails.dateCreated.split(".")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val formatted = original.format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a"))

        tv_date_paid.text =formatted
        tv_billed_amount.text = "KES "+ Const.instance.getBill().billDetails.detailAmount
        tv_receipt_amount.text = "KES " + Const.instance.getBill().billDetails.receiptAmount
        tv_validated_amount.text = "KES " + Const.instance.getEntries().billTotal

        tv_remaining_amount.text = "KES "+(Const.instance.getEntries().billTotal.toDouble() - Const.instance.getBill().billDetails.receiptAmount.toDouble()).toString()

        val changes = getTotalNumberOfChanges()
        var changesMessage =""
        val businessSubCategoryMessage = getBusinessSubCategoryComparison()
        val actualChangesMade = getActualChangesMade()

        if(changes == 0){
             changesMessage ="There were 0 changes done and $businessSubCategoryMessage"
            val color = Color.parseColor("#8DC6D2DD")
            layout.backgroundTintList = ColorStateList.valueOf(color)
            tv_message.setTextColor(resources.getColor(R.color.black))

        }else{
             changesMessage ="You made changes to $actualChangesMade"
            // Create a color object from the hex string
            val color = Color.parseColor("#F35611")
            layout.backgroundTintList = ColorStateList.valueOf(color)
            tv_message.setTextColor(Color.parseColor("#CB2020"))
        }

        tv_message.text = changesMessage


    }


    private fun getTotalNumberOfChanges(): Int {
        // Get the original and updated business objects from the Const singleton class
        val originalBusiness = Const.instance.getOriginalBusiness()
        val updatedBusiness = Const.instance.getBusiness()

       // Declare a variable to store the total number of changes made
        var changes = 0

       // Use the equals function to compare the properties of the business objects
        if (originalBusiness.id != updatedBusiness.id) changes++
        if (originalBusiness.businessID != updatedBusiness.businessID) changes++
        if (originalBusiness.businessName != updatedBusiness.businessName) changes++
        // ... other properties
        // Print the result
        println("The total number of changes made is $changes")

        return changes
    }

    fun getActualChangesMade(): String {
        // Get the original and updated business objects from the Const singleton class
        val originalBusiness = Const.instance.getOriginalBusiness()
        val updatedBusiness = Const.instance.getBusiness()

        val changedProperties = mutableListOf<Pair<String, String>>()

        if (originalBusiness.id != updatedBusiness.id) changedProperties.add(Pair("id", "${originalBusiness.id} -> ${updatedBusiness.id}"))
        if (originalBusiness.businessID != updatedBusiness.businessID) changedProperties.add(Pair("businessID", "${originalBusiness.businessID} -> ${updatedBusiness.businessID}"))
        if (originalBusiness.businessName != updatedBusiness.businessName) changedProperties.add(Pair("businessName", "${originalBusiness.businessName} -> ${updatedBusiness.businessName}"))
        if (originalBusiness.subCountyID != updatedBusiness.subCountyID) changedProperties.add(Pair("subCountyID", "${originalBusiness.subCountyID} -> ${updatedBusiness.subCountyID}"))
        if (originalBusiness.subCountyName != updatedBusiness.subCountyName) changedProperties.add(Pair("businessName", "${originalBusiness.subCountyName} -> ${updatedBusiness.subCountyName}"))
        if (originalBusiness.wardName != updatedBusiness.wardName) changedProperties.add(Pair("wardName", "${originalBusiness.wardName} -> ${updatedBusiness.wardName}"))
        if (originalBusiness.plotNumber != updatedBusiness.plotNumber) changedProperties.add(Pair("plotNumber", "${originalBusiness.plotNumber} -> ${updatedBusiness.plotNumber}"))
        if (originalBusiness.physicalAddress != updatedBusiness.physicalAddress) changedProperties.add(Pair("physicalAddress", "${originalBusiness.physicalAddress} -> ${updatedBusiness.physicalAddress}"))
        if (originalBusiness.buildingName != updatedBusiness.buildingName) changedProperties.add(Pair("buildingName", "${originalBusiness.buildingName} -> ${updatedBusiness.buildingName}"))
        if (originalBusiness.buildingOccupancy != updatedBusiness.buildingOccupancy) changedProperties.add(Pair("buildingOccupancy", "${originalBusiness.buildingOccupancy} -> ${updatedBusiness.buildingOccupancy}"))
        if (originalBusiness.floorNo != updatedBusiness.floorNo) changedProperties.add(Pair("floorNo", "${originalBusiness.floorNo} -> ${updatedBusiness.floorNo}"))
        if (originalBusiness.roomNo != updatedBusiness.roomNo) changedProperties.add(Pair("roomNo", "${originalBusiness.roomNo} -> ${updatedBusiness.roomNo}"))
        if (originalBusiness.premiseSize != updatedBusiness.premiseSize) changedProperties.add(Pair("premiseSize", "${originalBusiness.premiseSize} -> ${updatedBusiness.premiseSize}"))
        if (originalBusiness.numberOfEmployees != updatedBusiness.numberOfEmployees) changedProperties.add(Pair("numberOfEmployees", "${originalBusiness.numberOfEmployees} -> ${updatedBusiness.numberOfEmployees}"))
        if (originalBusiness.tonnage != updatedBusiness.tonnage) changedProperties.add(Pair("tonnage", "${originalBusiness.tonnage} -> ${updatedBusiness.tonnage}"))
        if (originalBusiness.businessDes != updatedBusiness.businessDes) changedProperties.add(Pair("businessDes", "${originalBusiness.businessDes} -> ${updatedBusiness.businessDes}"))
        if (originalBusiness.businessCategory != updatedBusiness.businessCategory) changedProperties.add(Pair("businessCategory", "${originalBusiness.businessCategory} -> ${updatedBusiness.businessCategory}"))
        if (originalBusiness.businessSubCategory != updatedBusiness.businessSubCategory) changedProperties.add(Pair("businessSubCategory", "${originalBusiness.businessSubCategory} -> ${updatedBusiness.businessSubCategory}"))
        if (originalBusiness.businessEmail != updatedBusiness.businessEmail) changedProperties.add(Pair("businessEmail", "${originalBusiness.businessEmail} -> ${updatedBusiness.businessEmail}"))
        if (originalBusiness.postalAddress != updatedBusiness.postalAddress) changedProperties.add(Pair("postalAddress", "${originalBusiness.postalAddress} -> ${updatedBusiness.postalAddress}"))
        if (originalBusiness.postalCode != updatedBusiness.postalCode) changedProperties.add(Pair("postalCode", "${originalBusiness.postalCode} -> ${updatedBusiness.postalCode}"))
        if (originalBusiness.businessPhone != updatedBusiness.businessPhone) changedProperties.add(Pair("businessPhone", "${originalBusiness.businessPhone} -> ${updatedBusiness.businessPhone}"))
        if (originalBusiness.contactPersonNames != updatedBusiness.contactPersonNames) changedProperties.add(Pair("contactPersonNames", "${originalBusiness.contactPersonNames} -> ${updatedBusiness.contactPersonNames}"))
        if (originalBusiness.businessRole != updatedBusiness.businessRole) changedProperties.add(Pair("businessRole", "${originalBusiness.businessRole} -> ${updatedBusiness.businessRole}"))
        if (originalBusiness.contactPersonPhone != updatedBusiness.contactPersonPhone) changedProperties.add(Pair("contactPersonPhone", "${originalBusiness.contactPersonPhone} -> ${updatedBusiness.contactPersonPhone}"))
        if (originalBusiness.contactPersonEmail != updatedBusiness.contactPersonEmail) changedProperties.add(Pair("contactPersonEmail", "${originalBusiness.contactPersonEmail} -> ${updatedBusiness.contactPersonEmail}"))
        if (originalBusiness.fullNames != updatedBusiness.fullNames) changedProperties.add(Pair("fullNames", "${originalBusiness.fullNames} -> ${updatedBusiness.fullNames}"))
        if (originalBusiness.ownerID != updatedBusiness.ownerID) changedProperties.add(Pair("ownerID", "${originalBusiness.ownerID} -> ${updatedBusiness.ownerID}"))
        if (originalBusiness.ownerPhone != updatedBusiness.ownerPhone) changedProperties.add(Pair("ownerPhone", "${originalBusiness.ownerPhone} -> ${updatedBusiness.ownerPhone}"))
        if (originalBusiness.ownerEmail != updatedBusiness.ownerEmail) changedProperties.add(Pair("ownerEmail", "${originalBusiness.ownerEmail} -> ${updatedBusiness.ownerEmail}"))
        if (originalBusiness.kraPin != updatedBusiness.kraPin) changedProperties.add(Pair("kraPin", "${originalBusiness.kraPin} -> ${updatedBusiness.kraPin}"))
        if (originalBusiness.createdBy != updatedBusiness.createdBy) changedProperties.add(Pair("createdBy", "${originalBusiness.createdBy} -> ${updatedBusiness.createdBy}"))
        if (originalBusiness.dateCreated != updatedBusiness.dateCreated) changedProperties.add(Pair("dateCreated", "${originalBusiness.dateCreated} -> ${updatedBusiness.dateCreated}"))
        if (originalBusiness.lat != updatedBusiness.lat) changedProperties.add(Pair("lat", "${originalBusiness.lat} -> ${updatedBusiness.lat}"))
        if (originalBusiness.lng != updatedBusiness.lng) changedProperties.add(Pair("lng", "${originalBusiness.lng} -> ${updatedBusiness.lng}"))


        // Print the result
        var names = ""
        var index = 0


        for ((name, value) in changedProperties) {

            if (index == changedProperties.size - 1) {
                names += "and $name"
            } else {
                names += "$name, "
            }
            index++

        }

        println("The names of the changed properties are: $names")

       return  names
    }
    private fun getBusinessSubCategoryComparison(): String {
        // Get the original and updated business objects from the Const singleton class
        val originalBusiness = Const.instance.getOriginalBusiness()
        val updatedBusiness = Const.instance.getBusiness()

        // Use a conditional statement to check if there was a mismatch between the subCountyName and the wardName on the original business object
        if (originalBusiness.businessSubCategory != updatedBusiness.businessSubCategory) {

            return "There was a mismatch between the business Sub Category. The applicant will be required to make ad additional payment for this."
        } else {

            return "There was no mismatch between the business Sub Category"
        }

    }



}