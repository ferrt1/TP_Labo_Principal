package com.example.cypher_vault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.cypher_vault.view.navigation.NavigationHost
import com.example.cypher_vault.viewmodel.authentication.AuthenticationViewModel

class MainActivity : ComponentActivity() {
    private val authenticationViewModel: AuthenticationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            NavigationHost(authenticationViewModel)
        }
    }
}
