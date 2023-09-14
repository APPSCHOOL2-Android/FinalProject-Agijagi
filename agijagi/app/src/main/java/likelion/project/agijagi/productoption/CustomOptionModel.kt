package likelion.project.agijagi.productoption

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomOptionModel(
    val option: String,
    val word: String?,
    val location: String?,
    val frontImage: Bitmap?,
    val backImage: Bitmap?,
    val leftImage: Bitmap?,
    val rightImage: Bitmap?,
    val count: Int
) : Parcelable

// 옵션의 종류
// 래터링일때 문구
// 래터링일때 위치
// 이미지일때 정면
// 이미지일때 후면
// 이미지일때 좌측
// 이미지일때 우측
// 수량