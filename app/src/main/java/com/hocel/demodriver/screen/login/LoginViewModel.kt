package com.hocel.demodriver.screen.login

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hocel.demodriver.data.RepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
) : ViewModel() {
    var authenticated = mutableStateOf(false)
        private set
    var loadingState = mutableStateOf(false)
        private set

    fun setLoading(loading: Boolean) {
        loadingState.value = loading
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

    fun signInEmailPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    RepositoryImpl.user = RepositoryImpl.app.login(
                        Credentials.emailPassword(email, password)
                    )
                }
                withContext(Dispatchers.Main) {
                    if (RepositoryImpl.user!!.loggedIn) {
                        RepositoryImpl.user = RepositoryImpl.app.currentUser
                        RepositoryImpl.initialize()
                        onSuccess()
                        RepositoryImpl.createDriver(mEmail = email)
                        delay(600)
                        authenticated.value = true
                    } else {
                        Log.d("MyUser", "User not loggedin")
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