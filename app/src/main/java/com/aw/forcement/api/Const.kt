// A singleton class that holds a business object as a property
class Const public constructor() {

    // A late-initialized immutable property for the business object
    private lateinit var business: Business

    // A function to set the business object
    fun setBusiness(business: Business) {
        this.business = business
    }

    // A function to get the business object
    fun getBusiness(): Business {
        return business
    }
    // A companion object that holds the singleton instance
    companion object {
        // A lazy-initialized property for the singleton instance
        val instance: Const by lazy { Const() }
    }
}