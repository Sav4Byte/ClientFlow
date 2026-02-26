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
data class Order(val id: Int, val title: String, val description: String, val clientName: String, val time: String)

sealed class TimelineItem {
    data class ReminderItem(val reminder: Reminder) : TimelineItem()
    data class CallItem(val id: Int, val name: String, val time: String) : TimelineItem()
    data class OrderItem(val order: Order, val backgroundColor: Color, val iconRes: Int) : TimelineItem()
}

@Composable
fun CalendarScreen(onNavigate: (Int) -> Unit) {
    var selectedTab by remember { mutableStateOf("REMINDER") }
    var selectedBottomTab by remember { mutableStateOf(3) }

    val reminders = listOf(
        Reminder(1, "Call Daniel Brooks", "9:00"),
        Reminder(2, "Call Ali Conors", "9:40"),
        Reminder(3, "Make an order for Sam Watson", "11:10"),
        Reminder(4, "Ask Fred about \"Gepur\"", "12:00"),
        Reminder(5, "Prepare documents", "16:00")
    )

    val orders = listOf(
        Order(1, "SUMMER COLLECTION", "The client requested a lookbook for the July capsule drop and asked to be notified once the new swimwear line becomes available.", "Daniel Brooks", "9:10"),
        Order(2, "FLORAL PRINT DRESSES", "Color: Sand / Coral. Size Range: XS–XXL\nQuantity: 200 pcs. Unit Price: $12.50", "Ali Konnors", "9:45"),
        Order(3, "SHORT-SLEEVE SHIRTS", "Men’s Linen Short-Sleeve Shirt:\nClient requested fabric swatches for upcoming fall color palette.\nAdd 10 extra units of the Sand Oversized T-Shirt if available in stock.", "Sam Watson", "9:30"),
        Order(4, "T-SHIRTS", "The client requested a lookbook for the July capsule drop and asked to be notified once the new swimwear line becomes available.", "Fred", "9:25")
    )

    val allTabItems = remember {
        listOf(
            TimelineItem.ReminderItem(Reminder(1, "Call Daniel Brooks", "9:00")),
            TimelineItem.CallItem(1, "DANIEL BROOKS", "9:02"),
            TimelineItem.OrderItem(
                Order(1, "SUMMER COLLECTION", "The client requested a lookbook for the July capsule drop and asked to be notified once the new swimwear line becomes available.", "Daniel Brooks", "9:10"),
                backgroundColor = Color(0xFFE5CCFF),
                iconRes = R.drawable.notess
            ),
            TimelineItem.OrderItem(
                Order(4, "DAN DILAN", "The client expressed interest in upcoming pre-orders for the Resort Wear Capsule, asking to receive the digital lookbook.", "", "9:30"),
                backgroundColor = Color(0xFFAEDEF4),
                iconRes = R.drawable.phone
            ),
            TimelineItem.ReminderItem(Reminder(2, "Call Ali Conors", "9:40")),
            TimelineItem.OrderItem(
                Order(2, "FLORAL PRINT DRESSES", "Color: Sand / Coral. Size Range: XS–XXL\nQuantity: 200 pcs. Unit Price: $12.50", "Ali Konnors", "9:45"),
                backgroundColor = Color(0xFFE5CCFF),
                iconRes = R.drawable.notess
            )
        )
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

            // Заголовок календаря: ОКТЯБРЬ на белом фоне, даты на градиенте
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // OCTOBER 2025 на белом фоне - с вырезом снизу
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

                // Даты на градиентном фоне
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

        // 1. Белый блок контента (размещен на 175dp для выступа вкладки)
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

        // 2. Ряд вкладок (закреплен ровно над белым блоком)
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
    }
}


@Composable
private fun CalendarDay(day: String, dayName: String, isSelected: Boolean) {
    if (isSelected) {
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(100.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Текст числа (например, "24")
            Text(
                day,
                fontSize = 44.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF334D6F),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-28).dp) // Сместил выше
            )

            // Текст названия дня (например, "Fri")
            Text(
                dayName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF334D6F),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 20.dp) // Сместил выше вслед за числом
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
                color = Color(0x99334D6F) // Установил запрошенный цвет с прозрачностью
            )
            Text(
                dayName,
                fontSize = 12.sp,
                color = Color(0x99334D6F) // Установил запрошенный цвет с прозрачностью
            )
        }
    }
}

private class ContentWithTabShape(val selectedTab: String) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cornerRadius = 32f * density.density // Для "ушек"
                val bodyCornerRadius = 16f * density.density // Для основных углов блока (сделаем острее)
                val tabHeight = 40f * density.density
                val smoothFactor = 28f * density.density
                val bodyTop = tabHeight

                // Разделяем общую ширину на 3 равные секции для соответствия Row с weight(1f)
                val sectionWidth = size.width / 3f
                val tabWidth = sectionWidth // Ширина вкладки соответствует ширине секции для центрирования

                val tabStartX = when (selectedTab) {
                    "ALL" -> 0f
                    "ORDERS" -> sectionWidth
                    "REMINDER" -> sectionWidth * 2f
                    else -> 0f
                }
                val tabEndX = tabStartX + tabWidth

                // Начинаем с левого нижнего угла (прямые углы)
                moveTo(0f, size.height)
                lineTo(size.width, size.height)

                // Правый край
                if (selectedTab == "REMINDER") {
                    // Правый край вверх во вкладку
                    lineTo(size.width, cornerRadius)
                    quadraticBezierTo(size.width, 0f, size.width - cornerRadius, 0f)

                    // Верх вкладки
                    lineTo(tabStartX + smoothFactor, 0f)

                    // Изгиб вниз к основному телу
                    cubicTo(
                        tabStartX, 0f,
                        tabStartX, bodyTop,
                        tabStartX - smoothFactor, bodyTop
                    )

                    // По левой верхней части основного тела
                    lineTo(bodyCornerRadius, bodyTop)
                    quadraticBezierTo(0f, bodyTop, 0f, bodyTop + bodyCornerRadius)
                } else if (selectedTab == "ALL") {
                    // Правый край вверх к верху основного тела
                    lineTo(size.width, bodyTop + bodyCornerRadius)
                    quadraticBezierTo(size.width, bodyTop, size.width - bodyCornerRadius, bodyTop)

                    // По верхнему краю к концу вкладки
                    lineTo(tabEndX + smoothFactor, bodyTop)

                    // Изгиб ВВЕРХ во вкладку
                    cubicTo(
                        tabEndX, bodyTop,
                        tabEndX, 0f,
                        tabEndX - smoothFactor, 0f
                    )

                    // От верха вкладки к левому краю
                    lineTo(cornerRadius, 0f)
                    quadraticBezierTo(0f, 0f, 0f, cornerRadius)
                } else { // ORDERS (Центр)
                    // Правый край вверх к верху основного тела
                    lineTo(size.width, bodyTop + bodyCornerRadius)
                    quadraticBezierTo(size.width, bodyTop, size.width - bodyCornerRadius, bodyTop)

                    // Переход к концу вкладки
                    lineTo(tabEndX + smoothFactor, bodyTop)

                    // Изгиб ВВЕРХ во вкладку
                    cubicTo(
                        tabEndX, bodyTop,
                        tabEndX, 0f,
                        tabEndX - smoothFactor * 0.8f, 0f
                    )

                    // Верх вкладки
                    lineTo(tabStartX + smoothFactor * 0.8f, 0f)

                    // Изгиб ВНИЗ из вкладки
                    cubicTo(
                        tabStartX, 0f,
                        tabStartX, bodyTop,
                        tabStartX - smoothFactor, bodyTop
                    )

                    // Верхний край к левой стороне
                    lineTo(bodyCornerRadius, bodyTop)
                    quadraticBezierTo(0f, bodyTop, 0f, bodyTop + bodyCornerRadius)
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
                // Параметры выреза
                val cutoutWidth = 130f * density.density
                val cutoutHeight = 45f * density.density
                val centerX = size.width * 0.63f

                val cutoutLeft = centerX - cutoutWidth / 2f
                val cutoutRight = centerX + cutoutWidth / 2f
                val cutoutBottom = size.height
                val cutoutTop = cutoutBottom - cutoutHeight

                val smoothFactor = 30f * density.density
                val edgeCornerRadius = 24f * density.density // Радиус для краев

                // Начинаем с левого верхнего угла
                moveTo(0f, 0f)

                // Верхний край
                lineTo(size.width, 0f)

                // Правый край вниз к началу закругления угла
                lineTo(size.width, size.height - edgeCornerRadius)

                // Изгиб в правом нижнем углу (идет "вверх")
                quadraticBezierTo(size.width, size.height, size.width - edgeCornerRadius, size.height)

                // Нижний край к началу центрального выреза (правая сторона)
                lineTo(cutoutRight, size.height)

                // Изгиб ВВЕРХ и ВЛЕВО в центральный вырез
                cubicTo(
                    cutoutRight - smoothFactor, size.height,
                    cutoutRight - smoothFactor, cutoutTop,
                    cutoutRight - smoothFactor * 2, cutoutTop
                )

                // Линия по верху центрального выреза
                lineTo(cutoutLeft + smoothFactor * 2, cutoutTop)

                // Изгиб ВНИЗ и ВЛЕВО из центрального выреза
                cubicTo(
                    cutoutLeft + smoothFactor, cutoutTop,
                    cutoutLeft + smoothFactor, size.height,
                    cutoutLeft, size.height
                )

                // Линия к началу закругления левого нижнего угла
                lineTo(edgeCornerRadius, size.height)

                // Изгиб в левом нижнем углу
                quadraticBezierTo(0f, size.height, 0f, size.height - edgeCornerRadius)

                // Левый край обратно к началу
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
            .padding(end = 2.dp, bottom = 25.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(FourthScreenOrderItemShape())
                .background(backgroundColor)
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 10.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.description,
                fontSize = 13.sp,
                color = Color(0xFF334D6F),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                .offset(x = 0.dp, y = 15.dp) // Выравнивание правого края (x=0)
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

                // Уменьшенные параметры для плотного прилегания к кнопке
                val cutoutHeight = 34f * density.density // Ниже (ближе к верху кнопки)
                val flatWidth = 30f * density.density   // Уже (ближе к боку кнопки)
                val slopeWidth = 40f * density.density
                val smoothing = 20f * density.density   // Компактнее сглаживание
                val topCornerRadius = 15f * density.density // Аккуратный угол

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



