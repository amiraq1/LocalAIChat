package com.localaichat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.localaichat.ui.LocalAIChatApp
import com.localaichat.ui.theme.LocalAIChatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalAIChatTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LocalAIChatApp()
                }
            }
        }
    }
}
