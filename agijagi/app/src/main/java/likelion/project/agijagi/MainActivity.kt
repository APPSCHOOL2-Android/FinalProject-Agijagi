package likelion.project.agijagi

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()

        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        onSetUpNavigation()
        setup()

        activityMainBinding.run {

        }
    }

    private fun onSetUpNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        activityMainBinding.bottomNavigation.apply {
            setupWithNavController(navController)

            setOnItemSelectedListener { item ->
                NavigationUI.onNavDestinationSelected(item, navController)
                navController.popBackStack(item.itemId, inclusive = false)
                true
            }
            navController.addOnDestinationChangedListener { _, destination, _ ->
                visibility = when (destination.id) {
                    R.id.homeFragment, R.id.categoryfragment, R.id.shippingfragment, R.id.wishListFragment, R.id.mypagefragment -> {
                        View.VISIBLE
                    }

                    else -> {
                        View.GONE
                    }
                }

                if (destination.id == R.id.mypagefragment) {
                    val currentUser = FirebaseAuth.getInstance().currentUser

                    // 로그인이 되어있지 않은 경우
                    if (currentUser == null) {
//                        findNavController().navigate(R.id.)

                    } else {
                        // 로그인이 되어있는 경우
                        // 여기서 사용자의 계정 타입을 확인하고 해당 프래그먼트로 이동 처리
                        // 판매자 계정인지, 구매자 계정인지에 따라 분기 처리
                        // 예: if문을 사용하여 sellerFragment 또는 buyerFragment로 이동
                        // 사용자의 계정 타입을 확인하는 코드 추가 필요
                    }
                }
            }
        }
    }

    fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }
}