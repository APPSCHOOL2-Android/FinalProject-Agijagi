package likelion.project.agijagi.signup

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class JoinDataVo(
    val name: String,
    val nickname: String,
    val email: String,
    val password: String
) : Parcelable
