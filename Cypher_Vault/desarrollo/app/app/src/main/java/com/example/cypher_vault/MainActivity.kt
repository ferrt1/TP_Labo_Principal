package com.example.cypher_vault


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import com.example.cypher_vault.view.navigation.NavigationHost


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseManager.initialize(this)
        setContent {
            NavigationHost()
        }
    }
}