package com.hocel.demodriver.navigation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hocel.demodriver.screen.login.LoginScreen
import com.hocel.demodriver.screen.login.LoginViewModel
import com.hocel.demodriver.screen.home.HomeScreen
import com.hocel.demodriver.screen.home.HomeViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.hocel.demodriver.screen.signup.SignupScreen
import com.hocel.demodriver.screen.signup.SignupViewModel
import com.hocel.demodriver.util.toast


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        loginRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            },
            goToSignup = {
                navController.navigate(Screen.Signup.route)
            }
        )
        signupRoute(
            goToLogin = {
                navController.navigate(Screen.Login.route)
            }
        )
        homeRoute()
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.loginRoute(
    navigateToHome: () -> Unit,
    goToSignup: () -> Unit
) {
    composable(route = Screen.Login.route) {
        val viewModel: LoginViewModel = hiltViewModel()
        val authenticated by viewModel.authenticated
        val loadingState by viewModel.loadingState
        val context = LocalContext.current
        LoginScreen(
            authenticated = authenticated,
            loadingState = loadingState,
            navigateToHome = navigateToHome,
            onLoginClicked = { email, password ->
                viewModel.setLoading(true)
                viewModel.signInEmailPassword(
                    email = email,
                    password = password,
                    onSuccess = {
                        "Successfully Authenticated!".toast(context = context, Toast.LENGTH_SHORT)
                        viewModel.setLoading(false)
                    },
                    onError = {
                        it.message?.toast(context = context, Toast.LENGTH_SHORT)
                        viewModel.setLoading(false)
                    })
            },
            goToSignup = goToSignup
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun NavGraphBuilder.signupRoute(
    goToLogin: () -> Unit
) {
    composable(route = Screen.Signup.route) {
        val viewModel: SignupViewModel = hiltViewModel()
        val authenticated by viewModel.authenticated
        val loadingState by viewModel.loadingState
        val context = LocalContext.current

        SignupScreen(
            authenticated = authenticated,
            loadingState = loadingState,
            navigateToLogin = goToLogin,
            onSignupClicked = { email, password ->
                viewModel.setLoading(true)
                viewModel.registerNewUser(
                    email = email,
                    password = password,
                    onSuccess = {
                        "Successfully signed up!".toast(context = context, Toast.LENGTH_SHORT)
                        viewModel.setLoading(false)
                    }, onError = {
                        it.message?.toast(context = context, Toast.LENGTH_SHORT)
                        viewModel.setLoading(false)
                    })
            })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.homeRoute() {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = hiltViewModel()
        HomeScreen(
            viewModel = viewModel,
            onSwitchClicked = viewModel::switchStatus,
            tripFlowAction = viewModel::tripAction
        )
    }
}