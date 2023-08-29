package com.example.mono

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.example.mono.core.designsystem.theme.MonoTheme
import com.example.mono.ui.MonoApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Disable the decor fitting for system windows to utilize the system elements area.
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MonoTheme {
                MonoApp()
            }
        }
    }
}