// A singleton class that holds a business object as a property
class Const public constructor() {

    // A late-initialized immutable property for the business object
    private lateinit var business: Business
    private lateinit var originalBusiness: Business
    private lateinit var entries: Entries

    private lateinit var bill: Bill
    private lateinit var receipt: Receipt

    private lateinit var statuses: List<Statuses>

    private lateinit var feesAndCharges: List<FeesAndCharges>

    // A mutable list to store the selected fees and charges
    private var selectedFeesAndCharges = mutableListOf<FeesAndCharges>()

    // A method to add a fee and charge to the list
    fun addFeeAndCharge(feeAndCharge: FeesAndCharges) {
        selectedFeesAndCharges.add(feeAndCharge)
    }

    // A method to remove a fee and charge from the list
    fun removeFeeAndCharge(feeAndCharge: FeesAndCharges) {
        selectedFeesAndCharges.remove(feeAndCharge)
    }

    // A method to get the list of selected fees and charges
    fun getSelectedFeesAndCharges(): List<FeesAndCharges> {
        return selectedFeesAndCharges
    }

    fun setFeesAndCharges(feesAndCharges: List<FeesAndCharges>) {
        this.feesAndCharges = feesAndCharges
    }
    fun getFeesAndCharges(): List<FeesAndCharges> {
        return feesAndCharges
    }

    fun setStatuses(statuses: List<Statuses>) {
        this.statuses = statuses
    }

    fun getStatuses(): List<Statuses> {
        return statuses
    }

    fun setOriginalBusiness(originalBusiness: Business) {
        this.originalBusiness = originalBusiness
    }

    // A function to get the business object
    fun getOriginalBusiness(): Business {
        return originalBusiness
    }

    // A function to set the business object
    fun setBusiness(business: Business) {
        this.business = business
    }

    // A function to get the business object
    fun getBusiness(): Business {
        return business
    }


    fun setEntries(entries: Entries) {
        this.entries = entries
    }

    // A function to get the business object
    fun getEntries(): Entries {
        return entries
    }


    fun setBill(bill: Bill) {
        this.bill = bill
    }

    // A function to get the business object
    fun getBill(): Bill {
        return bill
    }


    fun setReceipt(receipt: Receipt) {
        this.receipt = receipt
    }

    // A function to get the business object
    fun getReceipt(): Receipt {
        return receipt
    }


    // A companion object that holds the singleton instance
    companion object {
        // A lazy-initialized property for the singleton instance
        val instance: Const by lazy { Const() }
    }
}