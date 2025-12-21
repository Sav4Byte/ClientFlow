package com.coffeecodedevs.clientflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeecodedevs.clientflow.R

data class Reminder(val id: Int, val text: String, val time: String)

@Composable
fun FourthScreen() {
    var selectedTab by remember { mutableStateOf("REMINDER") }
    var selectedBottomTab by remember { mutableStateOf(3) }
    
    val reminders = listOf(
        Reminder(1, "Call Daniel Brooks", "9:00"),
        Reminder(2, "Call Ali Conors", "9:40"),
        Reminder(3, "Make an order for Sam Watson", "11:10"),
        Reminder(4, "Ask Fred about \"Gepur\"", "12:00"),
        Reminder(5, "Prepare documents", "16:00")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF87CEEB), Color(0xFFF5F5DC))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))

            // Calendar header - OCTOBER on white, dates on gradient
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // OCTOBER 2025 on white background - with cutout at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(OctoberHeaderWithCutoutShape())
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "OCTOBER 2025",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334D6F)
                    )
                }
                
                // Dates on gradient background
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 16.dp, start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CalendarDay("20", "Mon", false)
                    CalendarDay("21", "Tue", false)
                    CalendarDay("22", "Wed", false)
                    CalendarDay("23", "Thu", false)
                    CalendarDay("24", "Fri", true)
                    CalendarDay("25", "Sat", false)
                    CalendarDay("26", "Sun", false)
                }
        }
    }

        // 1. White content block (positioned at 175dp to allow tab protrusion)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 175.dp) 
                .padding(horizontal = 16.dp)
                .padding(bottom = 100.dp)
                .clip(ContentWithTabShape(selectedTab))
                .background(Color.White)
        ) {
            Box(modifier = Modifier.padding(top = 40.dp).padding(20.dp)) {
                when (selectedTab) {
                    "REMINDER" -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(reminders) { reminder -> ReminderItem(reminder) }
                        }
                    }
                    else -> {}
                }
            }
        }

        // 2. Tabs Row (pinned to sit exactly above the white block)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 175.dp) 
                .height(40.dp)
                .padding(horizontal = 16.dp), // Совпадает с отступом белого блока
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                TabItem("ALL", selectedTab == "ALL") { selectedTab = "ALL" }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                TabItem("ORDERS", selectedTab == "ORDERS") { selectedTab = "ORDERS" }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                TabItem("REMINDER", selectedTab == "REMINDER") { selectedTab = "REMINDER" }
            }
        }

        // Bottom Navigation
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 64.dp, end = 64.dp, bottom = 57.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = FourthScreenBottomBarShape(cutoutRadiusDp = 36.dp),
                color = Color(0xFF313131),
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavIcon(painterResource(R.drawable.contact), selectedBottomTab == 0) { selectedBottomTab = 0 }
                    BottomNavIcon(painterResource(R.drawable.notes), selectedBottomTab == 1) { selectedBottomTab = 1 }
                    Spacer(modifier = Modifier.width(70.dp))
                    BottomNavIcon(painterResource(R.drawable.dial), selectedBottomTab == 2) { selectedBottomTab = 2 }
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0x8087CEEB)).clickable { selectedBottomTab = 3 },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(painterResource(R.drawable.calendar), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
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


@Composable
private fun CalendarDay(day: String, dayName: String, isSelected: Boolean) {
    if (isSelected) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(120.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Убрали форму с фоном - только текст
            
            // Текст "24" - можно двигать независимо
            Text(
                day,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334D6F),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-28).dp)
            )
            
            // Текст "Fri" - можно двигать независимо
            Text(
                dayName,
                fontSize = 18.sp,
                color = Color(0xFF334D6F),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 28.dp)
            )
        }
    } else {
        // Невыбранный день остается как Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(40.dp)
                .padding(vertical = 8.dp)
        ) {
            Text(
                day,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF8B9CAE)
            )
            Text(
                dayName,
                fontSize = 12.sp,
                color = Color(0xFF8B9CAE)
            )
        }
    }
}

private class ContentWithTabShape(val selectedTab: String) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cornerRadius = 24f * density.density
                val tabHeight = 40f * density.density
                val smoothFactor = 20f * density.density
                val bodyTop = tabHeight
                
                // Divide the total width into 3 equal sections to match the Row with weight(1f)
                val sectionWidth = size.width / 3f
                val tabWidth = sectionWidth // Ear width matches the section width for centering
                
                val tabStartX = when (selectedTab) {
                    "ALL" -> 0f
                    "ORDERS" -> sectionWidth
                    "REMINDER" -> sectionWidth * 2f
                    else -> 0f
                }
                val tabEndX = tabStartX + tabWidth

                // Start at bottom-left corner
                moveTo(0f, size.height - cornerRadius)
                quadraticBezierTo(0f, size.height, cornerRadius, size.height)
                
                // Bottom edge
                lineTo(size.width - cornerRadius, size.height)
                quadraticBezierTo(size.width, size.height, size.width, size.height - cornerRadius)
                
                // Right edge
                if (selectedTab == "REMINDER") {
                    // Right edge up into the tab
                    lineTo(size.width, cornerRadius)
                    quadraticBezierTo(size.width, 0f, size.width - cornerRadius, 0f)
                    
                    // Top of tab
                    lineTo(tabStartX + smoothFactor, 0f)
                    
                    // Curve down to main body
                    cubicTo(
                        tabStartX, 0f,
                        tabStartX, bodyTop,
                        tabStartX - smoothFactor, bodyTop
                    )
                    
                    // Across top left of main body
                    lineTo(cornerRadius, bodyTop)
                    quadraticBezierTo(0f, bodyTop, 0f, bodyTop + cornerRadius)
                } else if (selectedTab == "ALL") {
                    // Right edge up to main body top
                    lineTo(size.width, bodyTop + cornerRadius)
                    quadraticBezierTo(size.width, bodyTop, size.width - cornerRadius, bodyTop)
                    
                    // Across top edge to the end of the tab
                    lineTo(tabEndX + smoothFactor, bodyTop)
                    
                    // Curve UP into the tab
                    cubicTo(
                        tabEndX, bodyTop,
                        tabEndX, 0f,
                        tabEndX - smoothFactor, 0f
                    )
                    
                    // Top of tab to left edge
                    lineTo(cornerRadius, 0f)
                    quadraticBezierTo(0f, 0f, 0f, cornerRadius)
                } else { // ORDERS (Center)
                    // Right edge up to main body top
                    lineTo(size.width, bodyTop + cornerRadius)
                    quadraticBezierTo(size.width, bodyTop, size.width - cornerRadius, bodyTop)
                    
                    // Across to tab end
                    lineTo(tabEndX + smoothFactor, bodyTop)
                    
                    // Curve UP into tab
                    cubicTo(
                        tabEndX, bodyTop,
                        tabEndX, 0f,
                        tabEndX - smoothFactor * 0.8f, 0f
                    )
                    
                    // Top of tab
                    lineTo(tabStartX + smoothFactor * 0.8f, 0f)
                    
                    // Curve DOWN out of tab
                    cubicTo(
                        tabStartX, 0f,
                        tabStartX, bodyTop,
                        tabStartX - smoothFactor, bodyTop
                    )
                    
                    // Top edge to left
                    lineTo(cornerRadius, bodyTop)
                    quadraticBezierTo(0f, bodyTop, 0f, bodyTop + cornerRadius)
                }
                
                close()
            }
        )
    }
}

private class OctoberHeaderWithCutoutShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = Path().apply {
                // Cutout parameters - smooth and natural like in ContactsScreen
                val cutoutWidth = 130f * density.density
                val cutoutHeight = 45f * density.density
                val centerX = size.width * 0.63f // Позиция над числом 24 (сместил чуть левее)
                
                val cutoutLeft = centerX - cutoutWidth / 2f
                val cutoutRight = centerX + cutoutWidth / 2f
                val cutoutBottom = size.height
                val cutoutTop = cutoutBottom - cutoutHeight
                
                // Control points for smooth transitions - adjusted for balance
                val smoothFactor = 30f * density.density

                // Start from top-left
                moveTo(0f, 0f)

                // Top edge
                lineTo(size.width, 0f)

                // Right edge
                lineTo(size.width, size.height)
                
                // Bottom edge to cutout start (right side)
                lineTo(cutoutRight, size.height)

                // Curve UP and LEFT into the cutout
                cubicTo(
                    cutoutRight - smoothFactor, size.height,
                    cutoutRight - smoothFactor, cutoutTop,
                    cutoutRight - smoothFactor * 2, cutoutTop
                )

                // Line across top of cutout
                lineTo(cutoutLeft + smoothFactor * 2, cutoutTop)

                // Curve DOWN and LEFT out of the cutout
                cubicTo(
                    cutoutLeft + smoothFactor, cutoutTop,
                    cutoutLeft + smoothFactor, size.height,
                    cutoutLeft, size.height
                )

                // Continue bottom edge to left
                lineTo(0f, size.height)

                // Left edge back to start
                lineTo(0f, 0f)

                close()
            }
        )
    }
}


@Composable
private fun TabItem(text: String, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        color = if (selected) Color(0xFF334D6F) else Color(0xFF999999),
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun ReminderItem(reminder: Reminder) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFDDB5)).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(R.drawable.bell), contentDescription = null, tint = Color(0xFF333333), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(reminder.text, fontSize = 16.sp, color = Color(0xFF333333), modifier = Modifier.weight(1f))
        Text(reminder.time, fontSize = 14.sp, color = Color(0xFF666666))
    }
}

@Composable
private fun BottomNavIcon(painter: androidx.compose.ui.graphics.painter.Painter, selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(painter, contentDescription = null, tint = if (selected) Color.White else Color.White.copy(alpha = 0.4f), modifier = Modifier.size(22.dp))
    }
}

private class FourthScreenBottomBarShape(private val cutoutRadiusDp: androidx.compose.ui.unit.Dp) : Shape {
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
