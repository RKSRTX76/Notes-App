package com.rksrtx76.notesapp

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.auth.api.identity.Identity
import com.rksrtx76.notesapp.data.model.ui_states.SignInState
import com.rksrtx76.notesapp.presentation.NotesViewModel
import com.rksrtx76.notesapp.presentation.SignInViewModel
import com.rksrtx76.notesapp.presentation.authentication.EmailAuthClient
import com.rksrtx76.notesapp.presentation.authentication.GoogleAuthClient
import com.rksrtx76.notesapp.presentation.screens.AddEditDetailView
import com.rksrtx76.notesapp.presentation.screens.AuthenticationScreen
import com.rksrtx76.notesapp.presentation.screens.HomeScreen
import com.rksrtx76.notesapp.presentation.screens.SignUpScreen
import kotlinx.coroutines.launch


@Composable
fun Navigation(
    lifecycleScope: LifecycleCoroutineScope
){
    val applicationContext = LocalContext.current.applicationContext

    val authClient by lazy{
        EmailAuthClient(
            context = applicationContext
        )
    }
    val googleAuthClient by lazy{
        GoogleAuthClient(
            context = applicationContext,
            authClient = Identity.getSignInClient(applicationContext)
        )
    }
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.LOGIN_SCREEN){
        composable(Screen.LOGIN_SCREEN){
            val signInViewModel = hiltViewModel<SignInViewModel>()
            val state by signInViewModel.state.collectAsStateWithLifecycle()

            // check if already logged in
            LaunchedEffect(key1 = Unit) {
                val user = googleAuthClient.getSignedInUser()
                user?.let {
                    navController.navigate("${Screen.HOME_SCREEN}/${user.userId}")
                }
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if(result.resultCode == RESULT_OK){
                        lifecycleScope.launch {
                            val signInResult = googleAuthClient.signedInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            signInViewModel.onSignIn(signInResult)
                        }
                    }
                }
            )

            LaunchedEffect(key1 = state.isSignInSuccess) {
                if(state.isSignInSuccess){
                    val user = googleAuthClient.getSignedInUser()
                    user?.let {
                        Toast.makeText(
                            applicationContext,
                            "Sign in successful",
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigate("${Screen.HOME_SCREEN}/${user.userId}")
                        signInViewModel.resetState()
                    }
                }
            }

            AuthenticationScreen(
                state = state,
                onGoogleClick = {
                    lifecycleScope.launch {
                        val signInIntentSender = googleAuthClient.signIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                },
                onEmailSignIn = { email, password ->
                    lifecycleScope.launch {
                        val signInResult = authClient.signInWithEmailAndPassword(email, password = password)
                        signInViewModel.onSignIn(signInResult)
                    }
                },
                onSignUpClick = {
                    navController.navigate(Screen.SIGN_UP_SCREEN)
                }
            )
        }
        composable(Screen.SIGN_UP_SCREEN){
            SignUpScreen(
                onSignUp = { name ,email, password ->
                    lifecycleScope.launch {
                        val signUpResult = authClient.signUpWithEmailAndPassword(name, email, password)
                    }
                },
                navController = navController,
                authClient = googleAuthClient
            )
        }

        composable("${Screen.HOME_SCREEN}/{userId}",
            arguments = listOf(navArgument("userId"){ type = NavType.StringType })
        ){ backStackEntry ->

            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val notesViewModel = hiltViewModel<NotesViewModel>()

            val onSignOut : () -> Unit = {
                lifecycleScope.launch {
                    googleAuthClient.signOut()
                    Toast.makeText(
                        applicationContext,
                        "Signed out",
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(Screen.LOGIN_SCREEN)
                }
            }
            HomeScreen(
                navController = navController,
                userId = userId,
                viewModel = notesViewModel,
                userData = googleAuthClient.getSignedInUser(),
                onSignOut = onSignOut
            )
        }

        composable("${Screen.ADD_SCREEN}/{documentId}",
            arguments = listOf(
                navArgument("documentId"){
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "null"
                },
            )
        ){ backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId")
            val notesViewModel = hiltViewModel<NotesViewModel>()
            val user = googleAuthClient.getSignedInUser()
            val userId = user?.userId
            AddEditDetailView(
                documentId =  if (documentId == "null") null else documentId,
                userId = userId ?: "",
                viewModel = notesViewModel,
                navController = navController
            )
        }
    }
}