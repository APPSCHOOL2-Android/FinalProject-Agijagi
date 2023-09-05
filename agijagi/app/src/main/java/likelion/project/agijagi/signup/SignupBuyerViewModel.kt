package likelion.project.agijagi.signup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.regex.Pattern

class SignupBuyerViewModel : ViewModel() {

    // 이메일 검사 정규식
    val emailValidation =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

    var name = MutableLiveData<String>("")
    var nickname = MutableLiveData<String>("")
    var email = MutableLiveData<String>("")
    var password = MutableLiveData<String>("")
    var passwordCheck = MutableLiveData<String>("")

    val nameState = MutableLiveData<Boolean>(false)
    val nickNameState = MutableLiveData<Boolean>(false)
    val emailState = MutableLiveData<Boolean>(false)
    val passwordState = MutableLiveData<Boolean>(false)
    val passwordCheckState = MutableLiveData<Boolean>(false)

    val buttonState = MutableLiveData<Boolean>(false)

    fun setName(name: String){

//        nameState.value = !(name.length < 2 || name.length > 5)
        nameState.value = name.length in 2..4
        if(nameState.value == true) this.name.value = name
    }

    fun setNickName(nickname: String){

        nickNameState.value = !(nickname.length < 2 || nickname.length > 9)
        this.nickname.value = nickname
    }

    fun setEmail(email: String){
        emailState.value = Pattern.matches(emailValidation,email)
        this.email.value = email
    }

    fun setPassword(password: String){
        passwordState.value = !(password.length < 4 || password.length > 15 || password.isBlank())
        this.password.value = password
    }

    fun setPasswordCheck(passwordCheck: String){
        passwordCheckState.value = this.password.value == this.passwordCheck.value
        this.passwordCheck.value = passwordCheck

        this.buttonState.value =
            this.run {
                nameState.value == true && nickNameState.value == true &&
                        emailState.value == true && passwordCheckState.value == true
            }
    }
}