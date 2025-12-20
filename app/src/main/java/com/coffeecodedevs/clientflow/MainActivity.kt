package com.coffeecodedevs.clientflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.coffeecodedevs.clientflow.ui.screens.ContactsScreen
import com.coffeecodedevs.clientflow.ui.screens.FourthScreen
import com.coffeecodedevs.clientflow.ui.screens.GoalsScreen
import com.coffeecodedevs.clientflow.ui.screens.ThirdScreen
import com.coffeecodedevs.clientflow.ui.theme.ClientFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClientFlowTheme {
               FourthScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GoalsScreenPreview() {
    ClientFlowTheme {
        ContactsScreen()
    }
}