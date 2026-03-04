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
import androidx.compose.animation.core.animateFloatAsState
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

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.DayOfWeek
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch

data class Reminder(val id: Int, val text: String, val time: String)
data class Order(val id: Int, val title: String, val description: String, val clientName: String, val time: String)

sealed class TimelineItem {
    data class ReminderItem(val reminder: Reminder) : TimelineItem()
    data class CallItem(val id: Int, val name: String, val time: String) : TimelineItem()
    data class OrderItem(val order: Order, val backgroundColor: Color, val iconRes: Int) : TimelineItem()
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    contacts: List<com.coffeecodedevs.clientflow.data.Contact> = emptyList(),
    onTabChange: (String) -> Unit = {},
    onNavigate: (Int) -> Unit
) {
    var selectedTab by remember { mutableStateOf("REMINDER") }
    
    // Notify parent of initial tab
    LaunchedEffect(Unit) {
        onTabChange(selectedTab)
    }
    var selectedBottomTab by remember { mutableStateOf(3) }
    
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    
    // Create a large list of dates (e.g., +/- 1000 days from today)
    val totalDays = 2000
    val days = remember {
        (0 until totalDays).map { today.minusDays((totalDays / 2).toLong()).plusDays(it.toLong()) }
    }
    
    val initialIndex = totalDays / 2
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex - 3)
    val coroutineScope = rememberCoroutineScope()
    
    // Calculate which item is currently near the center/visible to animate the cutout smoothly
    val selectedGlobalIndex = days.indexOf(selectedDate)
    val density = androidx.compose.ui.platform.LocalDensity.current
    
    var lastKnownTarget by remember { mutableStateOf(0f) }
    val cutoutCenterX by remember(selectedGlobalIndex) {
        derivedStateOf<Float> {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            val selectedItem = visibleItems.find { it.index == selectedGlobalIndex }
            if (selectedItem != null) {
                // Increased offset to 20dp to push the bubble more to the right and keep it centered
                val center = selectedItem.offset.toFloat() + selectedItem.size / 2f + (20f * density.density)
                lastKnownTarget = center
                center
            } else {
                lastKnownTarget
            }
        }
    }

    val dateString = selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"))

    val reminders = remember(contacts, dateString) {
        contacts.filter { it.reminderText.isNotBlank() && it.reminderDate == dateString }
            .map { Reminder(it.id, it.reminderText, if (it.reminderTime.isNotBlank()) it.reminderTime else "All Day") }
    }

    // Orders show all regardless of date
    val orders = remember(contacts) {
        contacts.filter { it.orderName.isNotBlank() }
            .map {
                Order(
                    id = it.id,
                    title = it.orderName,
                    description = it.contact ?: "",
                    clientName = it.customerName.ifBlank { "${it.firstName} ${it.lastName}".trim() },
                    time = if (it.reminderTime.isNotBlank()) it.reminderTime else ""
                )
            }
    }

    val allTabItems = remember(reminders, contacts, dateString) {
        val items = mutableListOf<TimelineItem>()
        items.addAll(reminders.map { TimelineItem.ReminderItem(it) })
        // Orders are no longer shown in the "Daily Timeline" because they don't depend on the calendar date
        
        
        // For calls, it's a bit harder to filter by year, but let's try to match "MMM. d,"
        val callDatePrefix = selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM. d,", java.util.Locale.ENGLISH))
        
        contacts.forEach { contact ->
            contact.callLog.forEach { timeStr ->
                if (timeStr.startsWith(callDatePrefix)) {
                    items.add(
                        TimelineItem.CallItem(
                            id = contact.id,
                            name = "${contact.firstName} ${contact.lastName}".trim().uppercase(),
                            time = timeStr
                        )
                    )
                }
            }
        }
        items
    }

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

            val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", java.util.Locale.ENGLISH)
            val headerText = selectedDate.format(monthFormatter).uppercase()

            // Calendar header: MONTH on white background, dates on gradient
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // White background block with cutout from bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(OctoberHeaderWithCutoutShape(cutoutCenterX))
                        .background(Color.White)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = headerText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334D6F)
                    )
                }

                // Dates with smooth scrolling
                val dayFormatter = DateTimeFormatter.ofPattern("dd")
                val dayOfWeekFormatter = DateTimeFormatter.ofPattern("E", java.util.Locale.ENGLISH)

                LazyRow(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    itemsIndexed(days) { index, date ->
                        val isSelected = date == selectedDate
                        Box(
                            modifier = Modifier.fillParentMaxWidth(fraction = 1f / 7f),
                            contentAlignment = Alignment.Center
                        ) {
                            CalendarDay(
                                day = date.format(dayFormatter),
                                dayName = date.format(dayOfWeekFormatter),
                                isSelected = isSelected,
                                onClick = {
                                    selectedDate = date
                                }
                            )
                        }
                    }
                }
            }
        }

        // 1. White content block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 175.dp)
                .padding(horizontal = 16.dp)
                .clip(ContentWithTabShape(selectedTab))
                .background(Color.White)
        ) {
            Box(modifier = Modifier.padding(top = 40.dp).padding(horizontal = 16.dp, vertical = 20.dp)) {
                when (selectedTab) {
                    "REMINDER" -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 120.dp)) {
                            items(reminders) { reminder -> ReminderItem(reminder) }
                        }
                    }
                    "ORDERS" -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 120.dp)) {
                            items(orders) { order -> OrderItem(order) }
                        }
                    }
                    "ALL" -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 120.dp)) {
                            items(allTabItems) { item ->
                                when (item) {
                                    is TimelineItem.ReminderItem -> ReminderItem(item.reminder)
                                    is TimelineItem.CallItem -> SimpleCallItem(item)
                                    is TimelineItem.OrderItem -> OrderItem(
                                        order = item.order,
                                        backgroundColor = item.backgroundColor,
                                        iconRes = item.iconRes
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 175.dp)
                .height(40.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                TabItem("ALL", selectedTab == "ALL") { 
                    selectedTab = "ALL"
                    onTabChange("ALL")
                }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                TabItem("ORDERS", selectedTab == "ORDERS") { 
                    selectedTab = "ORDERS"
                    onTabChange("ORDERS")
                }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                TabItem("REMINDER", selectedTab == "REMINDER") { 
                    selectedTab = "REMINDER"
                    onTabChange("REMINDER")
                }
            }
        }
    }
}


@Composable
private fun CalendarDay(day: String, dayName: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth() // Uses the 1/7 fraction from parent
            .height(115.dp) // Slightly taller to accommodate large text
            .offset(y = (-25.dp))
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        if (isSelected) {
            // Reverted to original large fonts
            Text(
                day,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334D6F)
            )
            Text(
                dayName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF334D6F)
            )
        } else {
            // Unselected state
            Text(
                day,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0x99334D6F)
            )
            Text(
                dayName,
                fontSize = 12.sp, // Reverted to 12
                color = Color(0x99334D6F)
            )
        }
    }
}

private class ContentWithTabShape(val selectedTab: String) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cornerRadius = 32f * density.density
                val bodyCornerRadius = 16f * density.density
                val tabHeight = 40f * density.density
                val smoothFactor = 28f * density.density
                val bodyTop = tabHeight

                val sectionWidth = size.width / 3f
                val tabWidth = sectionWidth

                val tabStartX = when (selectedTab) {
                    "ALL" -> 0f
                    "ORDERS" -> sectionWidth
                    "REMINDER" -> sectionWidth * 2f
                    else -> 0f
                }
                val tabEndX = tabStartX + tabWidth

                moveTo(0f, size.height)
                lineTo(size.width, size.height)

                if (selectedTab == "REMINDER") {
                    lineTo(size.width, cornerRadius)
                    quadraticBezierTo(size.width, 0f, size.width - cornerRadius, 0f)
                    lineTo(tabStartX + smoothFactor, 0f)
                    cubicTo(
                        tabStartX, 0f,
                        tabStartX, bodyTop,
                        tabStartX - smoothFactor, bodyTop
                    )
                    lineTo(bodyCornerRadius, bodyTop)
                    quadraticTo(0f, bodyTop, 0f, bodyTop + bodyCornerRadius)
                } else if (selectedTab == "ALL") {
                    lineTo(size.width, bodyTop + bodyCornerRadius)
                    quadraticTo(size.width, bodyTop, size.width - bodyCornerRadius, bodyTop)
                    lineTo(tabEndX + smoothFactor, bodyTop)
                    cubicTo(
                        tabEndX, bodyTop,
                        tabEndX, 0f,
                        tabEndX - smoothFactor, 0f
                    )
                    lineTo(cornerRadius, 0f)
                    quadraticTo(0f, 0f, 0f, cornerRadius)
                } else { // ORDERS
                    lineTo(size.width, bodyTop + bodyCornerRadius)
                    quadraticTo(size.width, bodyTop, size.width - bodyCornerRadius, bodyTop)
                    lineTo(tabEndX + smoothFactor, bodyTop)
                    cubicTo(
                        tabEndX, bodyTop,
                        tabEndX, 0f,
                        tabEndX - smoothFactor * 0.8f, 0f
                    )
                    lineTo(tabStartX + smoothFactor * 0.8f, 0f)
                    cubicTo(
                        tabStartX, 0f,
                        tabStartX, bodyTop,
                        tabStartX - smoothFactor, bodyTop
                    )
                    lineTo(bodyCornerRadius, bodyTop)
                    quadraticTo(0f, bodyTop, 0f, bodyTop + bodyCornerRadius)
                }

                close()
            }
        )
    }
}

private class OctoberHeaderWithCutoutShape(private val cutoutCenterX: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = Path().apply {
                val edgeCornerRadius = 24f * density.density

                moveTo(0f, 0f)
                lineTo(size.width, 0f)

                if (cutoutCenterX < 0) {
                    lineTo(size.width, size.height - edgeCornerRadius)
                    quadraticBezierTo(size.width, size.height, size.width - edgeCornerRadius, size.height)
                    lineTo(edgeCornerRadius, size.height)
                    quadraticBezierTo(0f, size.height, 0f, size.height - edgeCornerRadius)
                    lineTo(0f, 0f)
                    close()
                    return@apply
                }

                val cutoutWidth = 60f * density.density
                val cutoutHeight = 40f * density.density
                val smoothing = 25f * density.density

                val left = cutoutCenterX - cutoutWidth / 2f
                val right = cutoutCenterX + cutoutWidth / 2f
                val top = size.height - cutoutHeight

                // Draw clockwise
                lineTo(size.width, 0f)
                lineTo(size.width, size.height - edgeCornerRadius)
                quadraticBezierTo(size.width, size.height, size.width - edgeCornerRadius, size.height)
                
                // Bottom edge with the smooth cutout
                lineTo(right + smoothing, size.height)
                
                // Cubic curve into the cutout
                cubicTo(
                    right, size.height,
                    right, top,
                    right - smoothing, top
                )
                
                lineTo(left + smoothing, top)
                
                // Cubic curve out of the cutout
                cubicTo(
                    left, top,
                    left, size.height,
                    left - smoothing, size.height
                )
                
                lineTo(edgeCornerRadius, size.height)
                quadraticBezierTo(0f, size.height, 0f, size.height - edgeCornerRadius)
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
        color = Color(0xFF334D6F),
        modifier = modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun ReminderItem(reminder: Reminder) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFFFDDB5)).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(painterResource(R.drawable.reminder), contentDescription = null, tint = Color(0xFF333333), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(reminder.text, fontSize = 14.sp, color = Color(0xFF333333), modifier = Modifier.weight(1f))
        Text(reminder.time, fontSize = 14.sp, color = Color(0xFF666666))
    }
}

@Composable
private fun SimpleCallItem(item: TimelineItem.CallItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFAEDEF4))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(R.drawable.phone),
            contentDescription = null,
            tint = Color(0xFF334D6F),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = item.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF334D6F),
            modifier = Modifier.weight(1f)
        )
        Icon(
            painterResource(R.drawable.arrow_up_right),
            contentDescription = null,
            tint = Color(0xFF334D6F),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.time,
            fontSize = 14.sp,
            color = Color(0xFF334D6F).copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun OrderItem(order: Order, backgroundColor: Color = Color(0xFFE5CCFF), iconRes: Int = R.drawable.notess) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 2.dp, bottom = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(FourthScreenOrderItemShape())
                .background(backgroundColor)
                .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = Color(0xFF334D6F),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334D6F)
                    )
                }
                Text(
                    text = order.time,
                    fontSize = 12.sp,
                    color = Color(0xFF334D6F).copy(alpha = 0.6f)
                )
            }

            if (order.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = order.description,
                    fontSize = 13.sp,
                    color = Color(0xFF334D6F),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = order.clientName,
                fontSize = 12.sp,
                color = Color(0xFF334D6F).copy(alpha = 0.6f)
            )
        }

        // Кнопка со стрелкой (в центре тройной выемки)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 0.dp, y = 15.dp) // Возвращаем правильный офсет
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFF313131)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_up_right),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private class FourthScreenOrderItemShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cornerRadius = 8f * density.density

                // Возвращаем проверенные параметры формы для красоты
                val cutoutHeight = 34f * density.density 
                val flatWidth = 30f * density.density
                val slopeWidth = 40f * density.density
                val smoothing = 20f * density.density
                val topCornerRadius = 15f * density.density

                // Верх
                moveTo(cornerRadius, 0f)
                lineTo(size.width - cornerRadius, 0f)
                quadraticBezierTo(size.width, 0f, size.width, cornerRadius)

                // Правое ребро вниз до начала скругления
                lineTo(size.width, size.height - cutoutHeight - topCornerRadius)

                // Внутренний верхний угол (вход в вырез)
                quadraticBezierTo(
                    size.width, size.height - cutoutHeight,
                    size.width - topCornerRadius, size.height - cutoutHeight
                )

                // Плоский участок (потолок)
                lineTo(size.width - flatWidth, size.height - cutoutHeight)

                // Плавный S-образный спуск
                cubicTo(
                    size.width - flatWidth - smoothing, size.height - cutoutHeight,
                    size.width - flatWidth - slopeWidth + smoothing, size.height,
                    size.width - flatWidth - slopeWidth, size.height
                )

                // Низ и лево
                lineTo(cornerRadius, size.height)
                quadraticBezierTo(0f, size.height, 0f, size.height - cornerRadius)
                lineTo(0f, cornerRadius)
                quadraticBezierTo(0f, 0f, cornerRadius, 0f)

                close()
            }
        )
    }
}



