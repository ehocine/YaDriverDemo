package com.hocel.demodriver.screen.auth

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hocel.demodriver.data.RepositoryImpl
import com.hocel.demodriver.permissionHelper.PermissionHelper
import com.hocel.demodriver.permissionHelper.askForNotificationPermission
import com.hocel.demodriver.util.Constants.APP_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val permissionHelper: PermissionHelper
) : ViewModel() {
    var authenticated = mutableStateOf(false)
        private set
    var loadingState = mutableStateOf(false)
        private set

    var hasLocationPermission = mutableStateOf(permissionHelper.hasLocationPermissions())
        private set

    var hasNotificationPermission = mutableStateOf(permissionHelper.hasNotificationPermission())
        private set

    fun setLoading(loading: Boolean) {
        loadingState.value = loading
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun askNotificationPermission(activity: ComponentActivity?) {
        activity?.let {
            if (!permissionHelper.hasNotificationPermission()) {
                it.askForNotificationPermission(permissionHelper)
            }
        }
    }

    fun signInWithMongoAtlas(
        tokenId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    RepositoryImpl.user = RepositoryImpl.app.login(
                        Credentials.google(token = tokenId, type = GoogleAuthType.ID_TOKEN)
                    )
                }
                withContext(Dispatchers.Main) {
                    if (RepositoryImpl.user!!.loggedIn) {
                        onSuccess()
                        delay(600)
                        authenticated.value = true
                    } else {
                        onError(Exception("User is not logged in."))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

}