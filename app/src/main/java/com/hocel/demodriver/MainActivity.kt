package com.hocel.demodriver

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.hocel.demodriver.data.RepositoryImpl
import com.hocel.demodriver.data.RepositoryImpl.realm
import com.hocel.demodriver.navigation.SetupNavGraph
import com.hocel.demodriver.util.Constants.APP_ID
import com.hocel.demodriver.navigation.Screen
import com.stevdza.san.demodriver.ui.theme.MongoDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
    else Screen.Login.route
}