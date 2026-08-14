package com.kush.cachedeal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kush.cachedeal.core.designsystem.theme.CacheDealTheme
import com.kush.cachedeal.ui.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CacheDealTheme {
                AppNavHost()
            }
        }
    }
}