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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import com.coffeecodedevs.clientflow.ui.screens.*
import com.coffeecodedevs.clientflow.ui.theme.ClientFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClientFlowTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf(5) }
    var selectedBottomTab by remember { mutableStateOf(0) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Экраны
        when (currentScreen) {
            0 -> ContactsScreen(
                onContactClick = { currentScreen = 5 }
            )
            1 -> ThirdScreen(
                onBackClick = { currentScreen = 0 },
                onGoalsClick = {
                    currentScreen = 2
                    selectedBottomTab = 2
                }
            )
            2 -> GoalsScreen(
                onBackClick = { currentScreen = 0 }
            )
            3 -> FourthScreen(
                onNavigate = { screenIndex ->
                    currentScreen = screenIndex
                    selectedBottomTab = screenIndex
                }
            )
            5 -> ContactDetailScreen(
                onBackClick = { currentScreen = 0 }
            )
        }
        
        // Нижняя панель навигации (скрыта только для экрана 5)
        if (currentScreen != 5) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 64.dp, end = 64.dp, bottom = 65.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = BottomBarShape(cutoutRadiusDp = 36.dp),
                    color = Color(0xFF313131),
                    shadowElevation = 12.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavIcon(
                            painter = painterResource(R.drawable.contact),
                            selected = selectedBottomTab == 0,
                            onClick = {
                                selectedBottomTab = 0
                                currentScreen = 0
                            }
                        )
                        BottomNavIcon(
                            painter = painterResource(R.drawable.notes),
                            selected = selectedBottomTab == 1,
                            onClick = {
                                selectedBottomTab = 1
                                currentScreen = 1
                            }
                        )
                        Spacer(modifier = Modifier.width(70.dp))
                        BottomNavIcon(
                            painter = painterResource(R.drawable.dial),
                            selected = selectedBottomTab == 2,
                            onClick = {
                                selectedBottomTab = 2
                                currentScreen = 2
                            }
                        )
                        BottomNavIcon(
                            painter = painterResource(R.drawable.calendar),
                            selected = selectedBottomTab == 3,
                            onClick = {
                                selectedBottomTab = 3
                                currentScreen = 3
                            }
                        )
                    }
                }
                FloatingActionButton(
                    onClick = {},
                    modifier = Modifier.align(Alignment.TopCenter).offset(y = (-25).dp).size(56.dp),
                    containerColor = Color(0xFF313131),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
private fun BottomNavIcon(
    painter: androidx.compose.ui.graphics.painter.Painter,
    selected: Boolean,
    onClick: () -> Unit
) {
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

private class BottomBarShape(private val cutoutRadiusDp: androidx.compose.ui.unit.Dp) : Shape {
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

@Preview(showBackground = true)
@Composable
fun GoalsScreenPreview() {
    ClientFlowTheme {
        ContactsScreen()
    }
}