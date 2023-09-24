package likelion.project.agijagi.signup.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignupModel(
    val name: String,
    val nickname: String,
    val email: String,
    val password: String
) : Parcelable
