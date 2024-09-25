package com.kimnlee.mobipay

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.kimnlee.auth.presentation.viewmodel.AuthenticationState
import com.kimnlee.auth.presentation.viewmodel.BiometricViewModel
import com.kimnlee.common.ui.theme.MobiPayTheme
import com.kimnlee.common.auth.AuthManager
import com.kimnlee.mobipay.navigation.AppNavGraph
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var authManager: AuthManager
    private lateinit var biometricViewModel: BiometricViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = AuthManager(this)
        biometricViewModel = ViewModelProvider(this).get(BiometricViewModel::class.java)
        setContent {
            MobiPayTheme {
                val navController = rememberNavController()
                AppNavGraph(navController, authManager, applicationContext, biometricViewModel)
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS) {
            when (resultCode) {
                RESULT_OK -> {
                    biometricViewModel.updateAuthenticationState(AuthenticationState.Success)
//                    Log.d("MainActivity", "Authentication successful")
                }
                RESULT_CANCELED -> {
                    biometricViewModel.updateAuthenticationState(AuthenticationState.Failure)
//                    Log.d("MainActivity", "Authentication canceled or failed")
                }
                else -> {
                    biometricViewModel.updateAuthenticationState(AuthenticationState.Error("Unknown error occurred"))
//                    Log.d("MainActivity", "Authentication error: Unknown result code")
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE_CONFIRM_DEVICE_CREDENTIALS = 1
    }
}