package likelion.project.agijagi.sellermypage.model

data class ProductAddModel(
    var name: String = "",
    var price: String = "",
    var category: String = "",
    var ordermade: String = "",
    var options: ArrayList<OptionClass> = arrayListOf(),
    var detail: String = "",
    var pictures: ArrayList<String> = arrayListOf(), // Uri
    var plans: ArrayList<String> = arrayListOf(), // Uri
    val date: String = "" // date
) {
    inner class OptionClass(val opName: String, val isCheck: Boolean, val opPrice: String)
}