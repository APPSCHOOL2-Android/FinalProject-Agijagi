package likelion.project.agijagi.sellermypage

class ProductUpdateModel(
    var name: String = "",
    var price: String = "",
    var category: String = "",
    var ordermade: String = "",
    var options: ArrayList<OptionClass> = arrayListOf(),
    var detail: String = "",
    var pictures: ArrayList<String> = arrayListOf(), // Uri
    var plans: ArrayList<String> = arrayListOf(), // Uri
    val date: String = "dd-MM-yyyy" // date
) {
    inner class OptionClass(val opName: String, val isCheck: Boolean, val opPrice: String)

    // 개발용
    fun debugData() {
        val str: StringBuffer = StringBuffer()

        str.appendLine("name: $name")
        str.appendLine("price: $price")
        str.appendLine("category: $category")
        str.appendLine("ordermade: $ordermade")
        str.appendLine("options.size: ${options.size}")
        str.appendLine("detail: $detail")
        str.appendLine("pictures.size: ${pictures.size}")
        str.appendLine("plans.size: ${plans.size}")

        println(str.toString())
    }
}