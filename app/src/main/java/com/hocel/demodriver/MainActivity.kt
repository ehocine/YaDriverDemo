package com.hocel.demodriver

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.hocel.demodriver.data.RepositoryImpl
import com.hocel.demodriver.data.RepositoryImpl.realm
import com.hocel.demodriver.model.Trip
import com.hocel.demodriver.navigation.SetupNavGraph
import com.hocel.demodriver.util.Constants.APP_ID
import com.stevdza.san.demodriver.navigation.Screen
import com.stevdza.san.demodriver.ui.theme.MongoDemoTheme
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.subscriptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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