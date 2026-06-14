package com.raven.segundocerebro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.raven.segundocerebro.ui.navigation.AppNavGraph
import com.raven.segundocerebro.ui.theme.SegundoCerebroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }
}

@Composable
private fun App() {
    SegundoCerebroTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavGraph()
        }
    }
}
