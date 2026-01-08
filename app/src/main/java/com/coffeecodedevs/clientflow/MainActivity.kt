package com.coffeecodedevs.clientflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.coffeecodedevs.clientflow.ui.screens.ContactsScreen
import com.coffeecodedevs.clientflow.ui.screens.ContactDetailScreen
import com.coffeecodedevs.clientflow.ui.screens.FifthScreen
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
                var currentScreen by remember { mutableIntStateOf(5) } // Default to FifthScreen

                Box(modifier = Modifier.fillMaxSize()) {
                    // 1. Content Area
                    Box(modifier = Modifier.fillMaxSize().padding(bottom = 0.dp)) {
                        when (currentScreen) {
                            0 -> ContactsScreen(
                                selectedBottomTab = 0,
                                onTabSelected = { currentScreen = it },
                                onContactClick = { currentScreen = 4 }
                            )
                            1 -> ThirdScreen(
                                selectedBottomTab = 1,
                                onTabSelected = { currentScreen = it },
                                onBackClick = { currentScreen = 2 }
                            )
                            2 -> FourthScreen(
                                selectedBottomTab = 2,
                                onTabSelected = { currentScreen = it }
                            )
                            3 -> GoalsScreen(
                                selectedBottomTab = 3,
                                onTabSelected = { currentScreen = it },
                                onBackClick = { currentScreen = 2 }
                            )
                            4 -> ContactDetailScreen(
                                selectedBottomTab = 0,
                                onTabSelected = { currentScreen = it },
                                onBackClick = { currentScreen = 0 }
                            )
                            5 -> FifthScreen(
                                onBackClick = { currentScreen = 0 }
                            )
                            else -> FifthScreen(
                                onBackClick = { currentScreen = 0 }
                            )
                        }
                    }

                    // 2. Fixed Bottom Navigation Bar
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(start = 64.dp, end = 64.dp, bottom = 65.dp)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = MainBottomBarShape(cutoutRadiusDp = 36.dp),
                            color = Color(0xFF313131),
                            shadowElevation = 12.dp
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MainBottomNavIcon(painterResource(R.drawable.contact), currentScreen == 0) { currentScreen = 0 }
                                Spacer(modifier = Modifier.width(12.dp))
                                MainBottomNavIcon(painterResource(R.drawable.notes), currentScreen == 1) { currentScreen = 1 }
                                
                                Spacer(modifier = Modifier.width(70.dp))
                                
                                MainBottomNavIcon(painterResource(R.drawable.dial), currentScreen == 2) { currentScreen = 2 }
                                Spacer(modifier = Modifier.width(12.dp))
                                MainBottomNavIcon(painterResource(R.drawable.calendar), currentScreen == 3) { currentScreen = 3 }
                            }
                        }

                        // Fixed FAB (Plus Button)
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-28).dp)
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF313131))
                                .clickable { /* Global Add Action */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainBottomNavIcon(painter: androidx.compose.ui.graphics.painter.Painter, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (selected) Color(0x8087CEEB) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

private class MainBottomBarShape(private val cutoutRadiusDp: androidx.compose.ui.unit.Dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cornerRadius = size.height / 2f
                val R = with(density) { cutoutRadiusDp.toPx() }
                val r = 15f * density.density
                val cutoutCenterX = size.width / 2f
                val D = kotlin.math.sqrt(R * R + 2 * R * r)
                val theta = kotlin.math.atan2(-r, D)
                val thetaDeg = Math.toDegrees(theta.toDouble()).toFloat()
                val cupStartAngle = kotlin.math.atan2(r, -D)
                val cupStartDeg = Math.toDegrees(cupStartAngle.toDouble()).toFloat()
                val cupEndAngle = kotlin.math.atan2(r, D)
                val cupEndDeg = Math.toDegrees(cupEndAngle.toDouble()).toFloat()
                
                moveTo(0f, cornerRadius)
                arcTo(androidx.compose.ui.geometry.Rect(0f, 0f, cornerRadius * 2, cornerRadius * 2), 180f, 90f, false)
                lineTo(cutoutCenterX - D, 0f)
                arcTo(androidx.compose.ui.geometry.Rect(cutoutCenterX - D - r, 0f, cutoutCenterX - D + r, 2 * r), 270f, thetaDeg + 90f, false)
                arcTo(androidx.compose.ui.geometry.Rect(cutoutCenterX - R, -R, cutoutCenterX + R, R), cupStartDeg, cupEndDeg - cupStartDeg, false)
                arcTo(androidx.compose.ui.geometry.Rect(cutoutCenterX + D - r, 0f, cutoutCenterX + D + r, 2 * r), 180f - thetaDeg, 90f + thetaDeg, false)
                lineTo(size.width - cornerRadius, 0f)
                arcTo(androidx.compose.ui.geometry.Rect(size.width - cornerRadius * 2, 0f, size.width, cornerRadius * 2), 270f, 90f, false)
                lineTo(size.width, size.height - cornerRadius)
                arcTo(androidx.compose.ui.geometry.Rect(size.width - cornerRadius * 2, size.height - cornerRadius * 2, size.width, size.height), 0f, 90f, false)
                lineTo(cornerRadius, size.height)
                arcTo(androidx.compose.ui.geometry.Rect(0f, size.height - cornerRadius * 2, cornerRadius * 2, size.height), 90f, 90f, false)
                close()
            }
        )
    }
}