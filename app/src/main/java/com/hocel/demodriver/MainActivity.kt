package com.hocel.demodriver

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.hocel.demodriver.data.RepositoryImpl
import com.hocel.demodriver.data.RepositoryImpl.realm
import com.hocel.demodriver.navigation.SetupNavGraph
import com.hocel.demodriver.permissionHelper.PermissionHelper
import com.hocel.demodriver.permissionHelper.askForNotificationPermission
import com.hocel.demodriver.util.Constants.APP_ID
import com.stevdza.san.demodriver.navigation.Screen
import com.stevdza.san.demodriver.ui.theme.MongoDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionHelper: PermissionHelper

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val resultLauncher =
        this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (!checkOverlayPermission()) {
//                askForOverlayPermission(
//                    sharedPref.readOverlayPermissionDecision(),
//                    sharedPref = sharedPref,
//                    permissionHelper
//                )
//            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                askForNotificationPermission(permissionHelper)
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        super.onCreate(savedInstanceState)
        this.actionBar?.hide()
        RepositoryImpl.configureCollections()
        setContent {
            MongoDemoTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navController = navController
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}

private fun getStartDestination(): String {
    val user = App.create(APP_ID).currentUser
    return if (user != null && user.loggedIn) Screen.Home.route
    else Screen.Authentication.route
}