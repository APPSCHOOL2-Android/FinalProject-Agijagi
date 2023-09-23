package likelion.project.agijagi

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import likelion.project.agijagi.databinding.ActivityMainBinding
import likelion.project.agijagi.model.UserModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding

    private var auth: FirebaseAuth? = null
    private lateinit var db: FirebaseFirestore

    companion object {

        fun getMilliSec(): String {
            val sdf = SimpleDateFormat("yyMMddHHmmssSSS", Locale.getDefault())

            return sdf.format(Date(System.currentTimeMillis()))
        }

        fun displayDialogUserNotLogin(
            context: Context,
            navController: NavController,
            destinationId: Int
        ) {
            MaterialAlertDialogBuilder(context)
                .setTitle("로그인으로 이동")
                .setMessage("해당 서비스를 이용하시려면 로그인 해주세요.")
                .setPositiveButton("확인") { _: DialogInterface, _: Int ->
                    navController.navigate(destinationId)
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        auth = FirebaseAuth.getInstance()
        auth?.signOut()

        setup()
        onSetUpNavigation()
        handleOnBackPressed()

    }

    private fun onSetUpNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        activityMainBinding.bottomNavigation.apply {
            setupWithNavController(navController)

            setOnItemSelectedListener { item ->
                if (UserModel.uid == "" && (item.itemId == R.id.orderFragment ||
                            item.itemId == R.id.wishListFragment ||
                            item.itemId == R.id.buyerMypageFragment)
                ) {
                    displayDialogUserNotLogin(
                        this@MainActivity,
                        navController,
                        R.id.loginFragment
                    )
                    return@setOnItemSelectedListener false
                }

                NavigationUI.onNavDestinationSelected(item, navController)
                navController.popBackStack(item.itemId, inclusive = false)
                true
            }
            navController.addOnDestinationChangedListener { _, destination, _ ->
                visibility = when (destination.id) {
                    R.id.homeFragment,
                    R.id.categoryFragment,
                    R.id.orderFragment,
                    R.id.wishListFragment,
                    R.id.buyerMypageFragment -> {
                        View.VISIBLE
                    }

                    else -> {
                        View.GONE
                    }
                }
            }
        }
    }

    private fun handleOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                val navController = navHostFragment.navController

                when (navController.currentDestination?.id) {
                    R.id.homeFragment,
                    R.id.categoryFragment,
                    R.id.orderFragment,
                    R.id.wishListFragment,
                    R.id.buyerMypageFragment,
                    R.id.sellerMypageFragment -> finish()

                    else -> {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    fun setup() {
        db = Firebase.firestore

        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }
}