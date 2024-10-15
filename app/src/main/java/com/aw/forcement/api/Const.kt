import com.toptoche.searchablespinnerlibrary.SearchableSpinner

// A singleton class that holds a business object as a property
class Const public constructor() {

    private lateinit var feesAndChargesList: MutableMap<SearchableSpinner, FeesAndCharges>

    // A late-initialized immutable property for the business object
    private var business: Business? = null
    private lateinit var originalBusiness: Business
    private lateinit var entries: Entries

    private lateinit var description: String

    private lateinit var bill: Bill
    private lateinit var receipt: Receipt
    private lateinit var receiptDetails: ReceiptDetails
    private lateinit var individual: Individual
    private lateinit var units: Units

    private lateinit var plot: Plot

    private lateinit var property: Properties

    private lateinit var category: Category


    private lateinit var statuses: List<Statuses>

    private lateinit var feesAndCharges: List<FeesAndCharges>

    private var selectedFireSafety = mutableListOf<String>()
    private var testsDone = mutableListOf<String>()
    // A method to add a fee and charge to the list
    fun addFireSafety(fireSafety: String) {
        if (!selectedFireSafety.contains(fireSafety)) {
            selectedFireSafety.add(fireSafety)
        }
    }

    // A method to remove a fee and charge from the list
    fun removeFireSafety(fireSafety: String) {
        selectedFireSafety.remove(fireSafety)
    }

    // A method to get the list of selected fees and charges
    fun getSelectedFireSafety(): List<String> {
        return selectedFireSafety
    }

    fun addTests(tests: String) {
        if (!testsDone.contains(tests)) {
            testsDone.add(tests)
        }
    }
    fun removeTests(tests: String) {
        testsDone.remove(tests)
    }
    fun getTests(): List<String> {
        return testsDone
    }


    // A mutable list to store the selected fees and charges
    private var selectedFeesAndCharges = mutableListOf<FeesAndCharges>()


    // A method to add a fee and charge to the list
    fun addFeeAndCharge(feeAndCharge: FeesAndCharges) {

        if (!selectedFeesAndCharges.contains(feeAndCharge)) {
            selectedFeesAndCharges.add(feeAndCharge)
        }

    }

    fun clearFeesAndCharges() {
        selectedFeesAndCharges.clear() // Replace feesAndChargesList with your actual list variable
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
    fun setSelectedFeesAndChargeList(feesAndCharges: MutableMap<SearchableSpinner, FeesAndCharges>) {
        this.feesAndChargesList = feesAndCharges
    }

    fun getSelectedFeesAndChargesList(): MutableMap<SearchableSpinner, FeesAndCharges> {
        return feesAndChargesList
    }

    fun getFeesAndCharges(): List<FeesAndCharges> {
        return feesAndCharges
    }

    fun setUnits(units: Units) {
        this.units = units
    }

    fun getUnits(): Units {
        return units
    }

    fun setPlot(plot: Plot) {
        this.plot = plot
    }

    fun getPlot(): Plot {
        return plot
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
    fun setBusiness(business: Business?) {
        this.business = business
    }

    // A function to get the business object
    fun getBusiness(): Business? {
        return business
    }


    fun setDescription(description: String){
        this.description = description
    }

    fun getDescription(): String {
        return description
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
    fun getReceipt(): Receipt {
        return receipt
    }

    fun setProperty(property: Properties) {
        this.property = property
    }
    fun getProperty(): Properties {
        return property
    }

    fun setReceiptDetails(receiptDetails: ReceiptDetails) {
        this.receiptDetails = receiptDetails
    }
    fun getReceiptDetails(): ReceiptDetails {
        return receiptDetails
    }

    fun setIndividual(individual: Individual) {
        this.individual = individual
    }
    fun getIndividual(): Individual {
        return individual
    }

     fun setCategory(category: Category) {
        this.category = category
    }
    fun getCategory(): Category {
        return category
    }



    // A companion object that holds the singleton instance
    companion object {
        // A lazy-initialized property for the singleton instance
        val instance: Const by lazy { Const() }
    }
}