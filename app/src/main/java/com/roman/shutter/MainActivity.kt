package com.roman.shutter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.roman.shutter.feature.canvas.ui.CanvasScreen
import com.roman.shutter.feature.canvas.ui.CanvasScreenStateless
import com.roman.shutter.feature.canvas.ui.sampleCanvasUiState
import com.roman.shutter.ui.theme.ShutterFlyTheme
import dagger.hilt.android.AndroidEntryPoint

/** Main entry activity. Annotated for Hilt; renders a single Compose screen. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShutterFlyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        CanvasScreen()
                    }
                }
            }
        }
    }
}

/** Preview uses a stateless screen variant to avoid Hilt in the preview tool. */
@Preview(showBackground = true)
@Composable
fun CanvasPreview() {
    ShutterFlyTheme {
        CanvasScreenStateless(uiState = sampleCanvasUiState())
    }
}