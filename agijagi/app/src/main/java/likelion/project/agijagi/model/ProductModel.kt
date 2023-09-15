package likelion.project.agijagi.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    var productId: String, // 상품 Pk
    var brand: String, // 상호명
    var category: String, // 카테고리
    var customOptionInfo: HashMap<String, String>, // lettering_fee, lettering_is_use, image_fee, image_is_use
    var detail: String, // 상품 설명
    var floorPlan: ArrayList<String>, // 도면 Uri <=4
    var image: ArrayList<String>, // 사진 Uri <=6
    var isCustom: Boolean, // 커스텀 여부
    var name: String, // 상품 이름
    var state: String, // 재고 여부 - 판매, 품절, 숨김
    var price: String, // 가격
    var salesQuantity: Long, // 팔린 수량
    var sellerId: String, // 판매자 Pk
    var thumbnail_image: String, // 대표 사진 Uri
    var updateDate: String // yyMMddhhmmsssss
) : Parcelable

