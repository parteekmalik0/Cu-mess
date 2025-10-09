package com.malikparteek.cumess

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.malikparteek.cumess.navigation.BottomNavBar
import com.malikparteek.cumess.navigation.NavGraph
import com.malikparteek.cumess.ui.theme.CuMessTheme
import com.example.app.ReviewManagerHelper
import com.malikparteek.cumess.advertisement.InterstitialHelper
class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_UPDATE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        MobileAds.initialize(this) {}

        // Firestore - offline persistence
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        firestore.firestoreSettings = settings

        // Notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // In-App Update check
        checkForAppUpdate()

        // ✅ Count launches for Review
        ReviewManagerHelper.incrementLaunchCount(this)

        enableEdgeToEdge()
        setContent {
            CuMessTheme {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { BottomNavBar(navController = navController) }
                ) { innerPadding ->
                    NavGraph(navController = navController)
                }
            }
        }

        // ✅ Show Review after required launches (example: 10 launches)
        if (ReviewManagerHelper.shouldShowReview(this, requiredLaunches = 10)) {
            ReviewManagerHelper.showInAppReview(this)
        }
    }

    private fun checkForAppUpdate() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        this,
                        REQUEST_CODE_UPDATE
                    )
                } catch (e: Exception) {
                    Log.e("InAppUpdate", "Failed to start update flow", e)
                }
            }
        }
    }
}