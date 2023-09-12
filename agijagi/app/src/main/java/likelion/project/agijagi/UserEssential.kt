package likelion.project.agijagi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserEssential {

    companion object {
        lateinit var mainActivity: MainActivity
        lateinit var db: FirebaseFirestore
        lateinit var auth: FirebaseAuth
        lateinit var email: String
        var emailNotif = false
        var googleLoginCheck = false
        var isSeller = false
        lateinit var name: String
        lateinit var pw: String
        lateinit var roleId: String
        var smsNotif = false
        var wish = mutableListOf<String>()
        lateinit var additionalName: String
        fun getMillisec(): String {
            val sdf = SimpleDateFormat("yyMMddhhmmssSSS", Locale.getDefault())
            val writeDate = sdf.format(Date(System.currentTimeMillis()))

            return writeDate
        }
    }

}