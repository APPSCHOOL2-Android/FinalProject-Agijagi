package likelion.project.agijagi.model

data class ProductModel(
    var productId: String,
    var brand: String,
    var category: String,
    var customOptionInfo: Map<String, Any>,
    var detail: String,
    var floorPlan: ArrayList<String>,
    var image: ArrayList<String>,
    var isCustom: Boolean,
    var name: String,
    var outOfStock: Boolean,
    var price: String,
    var salesQuantity: Long,
    var sellerId: String,
    var updateDate: String
)
