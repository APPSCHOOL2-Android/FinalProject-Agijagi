package likelion.project.agijagi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Essential {

    companion object {
        lateinit var mainActivity: MainActivity
        lateinit var db: Firebase
        lateinit var auth: FirebaseAuth
        fun getMilliSec(): String {
            val sdf = SimpleDateFormat("yyMMddhhmmssSSS", Locale.getDefault())

            return sdf.format(Date(System.currentTimeMillis()))
        }
    }

}