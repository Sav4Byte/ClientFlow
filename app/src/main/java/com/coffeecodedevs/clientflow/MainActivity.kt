package com.coffeecodedevs.clientflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
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
    var currentScreen by remember { mutableStateOf(0) }
    var selectedBottomTab by remember { mutableStateOf(0) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedContactName by remember { mutableStateOf("Daniel Brooks") }
    var showContactActivity by remember { mutableStateOf(true) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Экраны
        when (currentScreen) {
            0 -> ContactsScreen(
                onContactClick = { name, full ->
                    selectedContactName = name
                    showContactActivity = full
                    currentScreen = 5
                },
                onCreateClick = { showCreateDialog = true }
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
                contactName = selectedContactName,
                showActivity = showContactActivity,
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
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.align(Alignment.TopCenter).offset(y = (-25).dp).size(56.dp),
                    containerColor = Color(0xFF313131),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }
        
        // Create Dialog
        if (showCreateDialog) {
            CreateContactDialog(
                onDismiss = { showCreateDialog = false },
                onSave = { showCreateDialog = false }
            )
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


@Composable
private fun CreateContactDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var createType by remember { mutableStateOf("CONTACT") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone1 by remember { mutableStateOf("") }
    var phone2 by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                   color = Color(0xFF334D6F)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Type tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("CONTACT", "ORDER", "REMINDER", "NOTE").forEach { type ->
                        Button(
                            onClick = { createType = type },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (createType == type) Color(0xFFAEDEF4) else Color(0xFFE8E8E8),
                                contentColor = Color(0xFF334D6F)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(type, fontSize = 12.sp)
                        }
                    }
                }
                
                // Input fields
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = { Text("First Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color.White
                    )
                )
                
                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = { Text("Last Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color.White
                    )
                )
                
                // Phone fields
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = phone1,
                        onValueChange = { phone1 = it },
                        placeholder = { Text("+380 67 895 50 89") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color.White
                        )
                    )
                    if (phone1.isNotEmpty()) {
                        Icon(
                            painter = painterResource(R.drawable.trash),
                            contentDescription = "Remove",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { phone1 = "" },
                            tint = Color(0xFF999999)
                        )
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = phone2,
                        onValueChange = { phone2 = it },
                        placeholder = { Text("+380 93 578 90 28") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color.White
                        )
                    )
                    if (phone2.isNotEmpty()) {
                        Icon(
                            painter = painterResource(R.drawable.trash),
                            contentDescription = "Remove",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { phone2 = "" },
                            tint = Color(0xFF999999)
                        )
                    }
                }
                
                // Add phone button
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF313131))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add phone",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Works on Sun, Mon, Wed and Fri.\nDon't call after 16:00.") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF313131))
            ) {
                Text("SAVE", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text("CANCEL", color = Color(0xFF334D6F))
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(16.dp))
    )
}
