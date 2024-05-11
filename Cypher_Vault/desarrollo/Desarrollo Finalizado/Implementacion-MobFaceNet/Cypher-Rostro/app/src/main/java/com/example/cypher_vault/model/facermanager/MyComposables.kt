package com.example.cypher_vault.model.facermanager

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

object MyComposables {

        @Composable
        fun get(): Context? {
            var currentContext: Context = LocalContext.current
            while (currentContext is ContextWrapper) {
                if (currentContext is androidx.activity.ComponentActivity) {
                    return currentContext
                }
                currentContext = currentContext.baseContext
            }
            return null
        }
}