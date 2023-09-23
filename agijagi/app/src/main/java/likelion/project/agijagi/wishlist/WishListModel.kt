package likelion.project.agijagi.wishlist

data class WishListModel(
    val brand: String,
    val name: String,
    val price: String,
    val thumbnail: String?,
    val prodId:String,
    var isCheck: Boolean = true
)
