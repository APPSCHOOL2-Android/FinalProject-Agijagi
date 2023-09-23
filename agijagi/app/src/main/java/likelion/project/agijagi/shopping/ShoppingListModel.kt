package likelion.project.agijagi.shopping

data class ShoppingListModel(
    val brand: String,
    val name: String,
    val price: String,
    val count: String,
    var thumbnail: String,
    val documentId:String,
    var isCheck: Boolean = false
)
