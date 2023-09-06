package likelion.project.agijagi.sellermypage.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderManagementModel(
    val brand: String,
    val name: String,
    val price: String
) : Parcelable