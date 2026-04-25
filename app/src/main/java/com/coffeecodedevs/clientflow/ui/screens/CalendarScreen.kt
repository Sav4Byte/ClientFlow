package com.coffeecodedevs.clientflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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

import androidx.compose.ui.res.stringResource
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import kotlinx.coroutines.launch
import java.util.Locale

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
    initialTab: String = "REMINDER",
    contacts: List<com.coffeecodedevs.clientflow.data.Contact> = emptyList(),
    onTabChange: (String) -> Unit = {},
    onOrderClick: (Order) -> Unit = {},
    onEditReminder: (com.coffeecodedevs.clientflow.data.Contact) -> Unit = {},
    onDeleteReminder: (com.coffeecodedevs.clientflow.data.Contact) -> Unit = {},
    onNavigate: (Int) -> Unit
) {
    var selectedTab by remember { mutableStateOf<String>(initialTab) }
    val coroutineScope = rememberCoroutineScope()
    
    val allDayText = stringResource(R.string.all_day_label)
    val allTab = stringResource(R.string.all_tab)
    val reminderTab = stringResource(R.string.reminder_tab)
    val ordersTab = stringResource(R.string.orders_tab)
    val callLabel = stringResource(R.string.call_label)
    val editDesc = stringResource(R.string.edit_desc)
    val deleteDesc = stringResource(R.string.delete_desc)
    
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }
    
    val totalDays = 2000
    val days = remember {
        (0 until totalDays).map { today.minusDays((totalDays / 2).toLong()).plusDays(it.toLong()) }
    }
    
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val itemWidthPx = screenWidthPx / 7f
    val cutoutCenterX = screenWidthPx / 2f

    val initialIndex = totalDays / 2
    val initialScrollOffset = -(cutoutCenterX - itemWidthPx / 2f).toInt()
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex,
        initialFirstVisibleItemScrollOffset = initialScrollOffset
    )

    // Sync selectedDate with visual center
    LaunchedEffect(listState) {
        snapshotFlow { 
            val layoutInfo = listState.layoutInfo
            val currentVisibleItems = layoutInfo.visibleItemsInfo
            if (currentVisibleItems.isNotEmpty()) {
                val closestItem = currentVisibleItems.minByOrNull { item ->
                    val itemCenter = item.offset + item.size / 2f
                    kotlin.math.abs(itemCenter - cutoutCenterX)
                }
                closestItem?.index
            } else {
                null
            }
        }
        .collect { closestIndex ->
            closestIndex?.let { index ->
                if (index in days.indices) {
                    val newDate = days[index]
                    if (selectedDate != newDate) {
                        selectedDate = newDate
                    }
                }
            }
        }
    }

    val dateString = selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

    val reminders = remember(contacts, dateString, allDayText) {
        contacts.filter { it.reminderText.isNotBlank() && it.reminderDate == dateString }
            .map { Reminder(it.id, it.reminderText, if (it.reminderTime.isNotBlank()) it.reminderTime else allDayText) }
    }

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
        val callDatePrefix = selectedDate.format(DateTimeFormatter.ofPattern("MMM. d,", Locale.getDefault()))
        
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
                    colors = listOf(Color(0xFFAEE0FF), Color(0xFFDDC6A3))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))

            val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
            val headerText = selectedDate.format(monthFormatter).uppercase()

            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(OctoberHeaderWithCutoutShape(cutoutCenterX))
                        .background(Color.White.copy(alpha = 0.75f))
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = headerText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334D6F),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                val dayFormatter = DateTimeFormatter.ofPattern("dd")
                val dayOfWeekFormatter = DateTimeFormatter.ofPattern("E", Locale.getDefault())
                val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
                val horizontalPadding = (cutoutCenterX / density.density).dp - (itemWidthPx / 2f / density.density).dp

                LazyRow(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 16.dp),
                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    flingBehavior = snapBehavior
                ) {
                    itemsIndexed(days) { index, date ->
                        val isSelected = date == selectedDate
                        
                        val scale by remember(index) {
                            derivedStateOf {
                                val layoutInfo = listState.layoutInfo
                                val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == index }
                                
                                if (itemInfo != null) {
                                    val itemCenter = itemInfo.offset + itemInfo.size / 2f
                                    val distanceFromCenter = kotlin.math.abs(itemCenter - cutoutCenterX)
                                    val maxDistance = itemInfo.size.toFloat() * 1.5f
                                    val rawScale = 1f - (distanceFromCenter / maxDistance)
                                    rawScale.coerceIn(0f, 1f)
                                } else {
                                    0f
                                }
                            }
                        }

                        Box(
                            modifier = Modifier.width((itemWidthPx / density.density).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CalendarDay(
                                day = date.format(dayFormatter),
                                dayName = date.format(dayOfWeekFormatter),
                                isSelected = isSelected,
                                scale = scale,
                                onClick = {
                                    selectedDate = date
                                    coroutineScope.launch {
                                        listState.animateScrollToItem(index, initialScrollOffset)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 175.dp)
                .padding(horizontal = 16.dp)
                .clip(ContentWithTabShape(selectedTab))
                .background(Color.White.copy(alpha = 0.75f))
        ) {
            Box(modifier = Modifier.padding(top = 40.dp).padding(horizontal = 16.dp, vertical = 20.dp)) {
                when (selectedTab) {
                    "REMINDER" -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 120.dp)) {
                            items(reminders) { reminder -> 
                                val contact = contacts.find { it.id == reminder.id }
                                CalendarReminderItem(
                                    reminder, 
                                    editDesc, 
                                    deleteDesc,
                                    onEdit = { contact?.let { onEditReminder(it) } },
                                    onDelete = { contact?.let { onDeleteReminder(it) } }
                                ) 
                            }
                        }
                    }
                    "ORDERS" -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 120.dp)) {
                            items(orders) { order -> 
                                CalendarOrderItem(order = order, onClick = { onOrderClick(order) }) 
                            }
                        }
                    }
                    "ALL" -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 120.dp)) {
                            items(allTabItems) { item ->
                                when (item) {
                                    is TimelineItem.ReminderItem -> {
                                        val contact = contacts.find { it.id == item.reminder.id }
                                        CalendarReminderItem(
                                            item.reminder, 
                                            editDesc, 
                                            deleteDesc,
                                            onEdit = { contact?.let { onEditReminder(it) } },
                                            onDelete = { contact?.let { onDeleteReminder(it) } }
                                        )
                                    }
                                    is TimelineItem.CallItem -> CalendarCallItem(item)
                                    is TimelineItem.OrderItem -> CalendarOrderItem(item.order)
                                }
                            }
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 175.dp)
                .height(40.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                TabItem(allTab, selectedTab == "ALL") { 
                    selectedTab = "ALL"
                    onTabChange("ALL")
                }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                TabItem(ordersTab, selectedTab == "ORDERS") { 
                    selectedTab = "ORDERS"
                    onTabChange("ORDERS")
                }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                TabItem(reminderTab, selectedTab == "REMINDER") { 
                    selectedTab = "REMINDER"
                    onTabChange("REMINDER")
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: String,
    dayName: String,
    isSelected: Boolean,
    scale: Float = 1f,
    onClick: () -> Unit
) {
    val numberSize = (20 + (44 - 20) * scale).sp
    val dayNameSize = (14 + (32 - 14) * scale).sp
    val fontWeight = if (scale > 0.5f) FontWeight.Bold else FontWeight.Normal
    val verticalOffset = (-25 - (13 * scale)).dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .offset(y = verticalOffset)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Text(day, fontSize = numberSize, fontWeight = fontWeight, color = Color(0xFF334D6F))
        Text(dayName, fontSize = dayNameSize, fontWeight = FontWeight.Normal, color = Color(0xFF334D6F))
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
                val tabWidth = size.width / 3f
                val tabStartX = when (selectedTab) {
                    "ALL" -> 0f
                    "ORDERS" -> tabWidth
                    "REMINDER" -> tabWidth * 2f
                    else -> 0f
                }
                val tabEndX = tabStartX + tabWidth
                moveTo(0f, size.height)
                lineTo(size.width, size.height)
                if (selectedTab == "REMINDER") {
                    lineTo(size.width, cornerRadius)
                    quadraticTo(size.width, 0f, size.width - cornerRadius, 0f)
                    lineTo(tabStartX + smoothFactor, 0f)
                    cubicTo(tabStartX, 0f, tabStartX, bodyTop, tabStartX - smoothFactor, bodyTop)
                    lineTo(bodyCornerRadius, bodyTop)
                    quadraticTo(0f, bodyTop, 0f, bodyTop + bodyCornerRadius)
                } else if (selectedTab == "ALL") {
                    lineTo(size.width, bodyTop + bodyCornerRadius)
                    quadraticTo(size.width, bodyTop, size.width - bodyCornerRadius, bodyTop)
                    lineTo(tabEndX + smoothFactor, bodyTop)
                    cubicTo(tabEndX, bodyTop, tabEndX, 0f, tabEndX - smoothFactor, 0f)
                    lineTo(cornerRadius, 0f)
                    quadraticTo(0f, 0f, 0f, cornerRadius)
                } else {
                    lineTo(size.width, bodyTop + bodyCornerRadius)
                    quadraticTo(size.width, bodyTop, size.width - bodyCornerRadius, bodyTop)
                    lineTo(tabEndX + smoothFactor, bodyTop)
                    cubicTo(tabEndX, bodyTop, tabEndX, 0f, tabEndX - smoothFactor * 0.8f, 0f)
                    lineTo(tabStartX + smoothFactor * 0.8f, 0f)
                    cubicTo(tabStartX, 0f, tabStartX, bodyTop, tabStartX - smoothFactor, bodyTop)
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
                val cutoutWidth = 60f * density.density
                val cutoutHeight = 40f * density.density
                val smoothing = 25f * density.density
                val left = cutoutCenterX - cutoutWidth / 2f
                val right = cutoutCenterX + cutoutWidth / 2f
                val top = size.height - cutoutHeight
                lineTo(size.width, 0f)
                lineTo(size.width, size.height - edgeCornerRadius)
                quadraticTo(size.width, size.height, size.width - edgeCornerRadius, size.height)
                lineTo(right + smoothing, size.height)
                cubicTo(right, size.height, right, top, right - smoothing, top)
                lineTo(left + smoothing, top)
                cubicTo(left, top, left, size.height, left - smoothing, size.height)
                lineTo(edgeCornerRadius, size.height)
                quadraticTo(0f, size.height, 0f, size.height - edgeCornerRadius)
                lineTo(0f, 0f)
                close()
            }
        )
    }
}

@Composable
fun TabItem(
    text: String, 
    selected: Boolean, 
    modifier: Modifier = Modifier, 
    onClick: () -> Unit = {}
) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        color = Color(0xFF334D6F),
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    )
}

@Composable
private fun CalendarReminderItem(
    reminder: Reminder, 
    editDesc: String, 
    deleteDesc: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF313131)), contentAlignment = Alignment.Center) {
                Icon(painterResource(R.drawable.bell), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(reminder.text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334D6F))
                Text(reminder.time, fontSize = 14.sp, color = Color(0xFF334D6F).copy(alpha = 0.6f))
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(painterResource(R.drawable.pensil), contentDescription = editDesc, tint = Color(0xFF334D6F), modifier = Modifier.size(22.dp).clickable { onEdit() })
            Icon(painterResource(R.drawable.trash), contentDescription = deleteDesc, tint = Color(0xFF334D6F), modifier = Modifier.size(22.dp).clickable { onDelete() })
        }
    }
}

@Composable
private fun CalendarCallItem(item: TimelineItem.CallItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.phone), contentDescription = null, tint = Color(0xFF334D6F), modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(R.string.call_label), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334D6F))
        }
        Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334D6F), modifier = Modifier.weight(1f))
        Icon(painterResource(R.drawable.arrow_up_right), contentDescription = null, tint = Color(0xFF334D6F), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(item.time, fontSize = 14.sp, color = Color(0xFF334D6F).copy(alpha = 0.6f))
    }
}

@Composable
private fun CalendarOrderItem(
    order: Order, 
    backgroundColor: Color = Color(0xFFE5CCFF), 
    iconRes: Int = R.drawable.notess,
    onClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxWidth().padding(end = 2.dp, bottom = 12.dp)) {
        Column(modifier = Modifier.fillMaxWidth().clip(FourthScreenOrderItemShape()).background(backgroundColor).padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(iconRes), contentDescription = null, tint = Color(0xFF334D6F), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = order.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334D6F))
                }
                Text(text = order.time, fontSize = 12.sp, color = Color(0xFF334D6F).copy(alpha = 0.6f))
            }
            if (order.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = order.description, fontSize = 13.sp, color = Color(0xFF334D6F), lineHeight = 16.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = order.clientName, fontSize = 12.sp, color = Color(0xFF334D6F).copy(alpha = 0.6f))
        }
        Box(modifier = Modifier.align(Alignment.BottomEnd).offset(x = 0.dp, y = 15.dp).size(42.dp).clip(CircleShape).background(Color(0xFF313131)).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
            Icon(painter = painterResource(R.drawable.arrow_up_right), contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

private class FourthScreenOrderItemShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cornerRadius = 8f * density.density
                val cutoutHeight = 34f * density.density 
                val flatWidth = 30f * density.density
                val slopeWidth = 40f * density.density
                val smoothing = 20f * density.density
                val topCornerRadius = 15f * density.density
                moveTo(cornerRadius, 0f)
                lineTo(size.width - cornerRadius, 0f)
                quadraticTo(size.width, 0f, size.width, cornerRadius)
                lineTo(size.width, size.height - cutoutHeight - topCornerRadius)
                quadraticTo(size.width, size.height - cutoutHeight, size.width - topCornerRadius, size.height - cutoutHeight)
                lineTo(size.width - flatWidth, size.height - cutoutHeight)
                cubicTo(size.width - flatWidth - smoothing, size.height - cutoutHeight, size.width - flatWidth - slopeWidth + smoothing, size.height, size.width - flatWidth - slopeWidth, size.height)
                lineTo(cornerRadius, size.height)
                quadraticTo(0f, size.height, 0f, size.height - cornerRadius)
                lineTo(0f, cornerRadius)
                quadraticTo(0f, 0f, cornerRadius, 0f)
                close()
            }
        )
    }
}
