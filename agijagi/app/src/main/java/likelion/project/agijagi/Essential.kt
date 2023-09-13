package likelion.project.agijagi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Essential {

    // ** 의논 **
    // Android 컨텍스트 클래스를 정적(static) 필드에 저장하면 메모리 누수(memory leak)의 위험
    // 메모리 누수는 앱이 메모리를 지속적으로 소비하여 앱 성능을 저하시키고 최종적으로 앱 충돌을 유발할 수 있는 심각한 문제...?

    companion object {
        lateinit var mainActivity: MainActivity
        lateinit var db: FirebaseFirestore   // ** 의논 **
        lateinit var auth: FirebaseAuth
        fun getMilliSec(): String {
            val sdf = SimpleDateFormat("yyMMddhhmmssSSS", Locale.getDefault())

            return sdf.format(Date(System.currentTimeMillis()))
        }
    }

}