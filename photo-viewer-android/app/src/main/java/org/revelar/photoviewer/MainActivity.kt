package org.revelar.photoviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import org.revelar.photoviewer.ui.RevelarNav
import org.revelar.photoviewer.ui.theme.RevelarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            RevelarTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RevelarNav()
                }
            }
        }
    }
}
