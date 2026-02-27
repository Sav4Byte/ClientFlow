package com.coffeecodedevs.clientflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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

data class ContactOrder(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val color: Color
)

sealed class ContactTimelineItem {
    data class SimpleCall(
        val time: String,
        val isIncoming: Boolean = false
    ) : ContactTimelineItem()

    data class DetailedCall(
        val time: String,
        val description: String
    ) : ContactTimelineItem()

    data class OrderItem(
        val order: ContactOrder
    ) : ContactTimelineItem()
}

@Composable
fun ContactDetailScreen(
    contactName: String = "Daniel Brooks",
    contactNote: String? = "Works on Sun, Mon, Wed and Fri.\nDon't call after 16:00.",
    company: String? = null,
    phoneNumbers: List<String> = listOf("+380 67 895 50 89", "+380 93 578 90 28"),
    callLog: List<String> = emptyList(),
    selectedBottomTab: Int = 0,
    onTabSelected: (Int) -> Unit = {},
    showActivity: Boolean = true,
    onBackClick: () -> Unit = {},
    onCallClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {

    var selectedTab by remember { mutableStateOf("ALL") }
    val context = androidx.compose.ui.platform.LocalContext.current

    val orders = emptyList<ContactOrder>()

    // Build timeline from callLog (show latest first)
    val allTabItems: List<ContactTimelineItem> = remember(callLog) {
        callLog.reversed().map { timestamp ->
            ContactTimelineItem.SimpleCall(time = timestamp)
        }
    }

    val gradientColors = listOf(
        Color(0xFFAEDEF4),
        Color(0xFFB8E8E8),
        Color(0xFFF5EFE6),
        Color(0xFFF5E6D3)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
        ) {
            // White header block with contact info
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(ContactDetailHeaderShape())
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 24.dp, end = 16.dp)
                    ) {
                        // Header with back button and title
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { onBackClick() },
                                    tint = Color(0xFF334D6F)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = contactName,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF334D6F)
                                    )
                                    if (!company.isNullOrBlank()) {
                                        Text(
                                            text = company,
                                            fontSize = 14.sp,
                                            color = Color(0xFF334D6F).copy(alpha = 0.7f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(
                                    painter = painterResource(R.drawable.thrash),
                                    contentDescription = "Delete",
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { onDeleteClick() }
                                )
                                Icon(
                                    painter = painterResource(R.drawable.pensil),
                                    contentDescription = "Edit",
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable { onEditClick() }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Contact note
                        contactNote?.let {
                            Text(
                                text = it,
                                fontSize = 12.sp,
                                color = Color(0xFFAAAAAA),
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(start = 40.dp)
                            )
                        }


                        Spacer(modifier = Modifier.height(18.dp))

                        // Phone numbers
                        Column(modifier = Modifier.padding(start = 40.dp)) {
                            phoneNumbers.forEach { phone ->
                                Text(
                                    text = phone,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334D6F)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }

                // Action buttons (call, message, share)
                if (showActivity) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(y = 15.dp)
                            .padding(end = 36.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        ContactActionButton(
                            painter = painterResource(R.drawable.phone),
                            onClick = { 
                                phoneNumbers.firstOrNull()?.let { 
                                    onCallClick()
                                    com.coffeecodedevs.clientflow.utils.ContactActions.callContact(context, it)
                                }
                            }
                        )
                        ContactActionButton(
                            painter = painterResource(R.drawable.sms),
                            onClick = { 
                                phoneNumbers.firstOrNull()?.let { 
                                    com.coffeecodedevs.clientflow.utils.ContactActions.sendSms(context, it)
                                }
                            }
                        )
                        ContactActionButton(
                            painter = painterResource(R.drawable.share),
                            onClick = { 
                                com.coffeecodedevs.clientflow.utils.ContactActions.shareContact(
                                    context, 
                                    contactName, 
                                    phoneNumbers, 
                                    contactNote
                                )
                            }
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(30.dp))

            // Tabs and content (ALL / ORDERS) — всегда показываем, при showActivity = false списки пустые
            Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    // White content block with tab shape
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .clip(ContactDetailContentShape(selectedTab))
                            .background(Color.White)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    top = 50.dp,
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp
                                )
                        ) {
                            when (selectedTab) {
                                "ORDERS" -> {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        contentPadding = PaddingValues(bottom = 180.dp)
                                    ) {
                                        items(orders) { order ->
                                            ContactOrderItem(order)
                                        }
                                    }
                                }
                                "ALL" -> {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(16.dp),
                                        contentPadding = PaddingValues(bottom = 180.dp)
                                    ) {
                                        items(allTabItems) { item ->
                                            when (item) {
                                                is ContactTimelineItem.SimpleCall -> ContactSimpleCallItem(item)
                                                is ContactTimelineItem.DetailedCall -> ContactDetailedCallItem(item)
                                                is ContactTimelineItem.OrderItem -> ContactOrderItem(item.order)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Tab row positioned at top of white block
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-1).dp * (Modifier.fillMaxHeight().hashCode()))
                            .height(40.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            ContactTabItem("ALL", selectedTab == "ALL") { selectedTab = "ALL" }
                        }
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            ContactTabItem("ORDERS", selectedTab == "ORDERS") { selectedTab = "ORDERS" }
                        }
                    }
                }

                // Tabs positioned at top of content
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .offset(y = (-1).dp * 200) // Position above content
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tabs are embedded in shape
                }
            }

            // Fixed tabs on top of content block (ALL / ORDERS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 234.dp)
                    .height(40.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    ContactTabItem("ALL", selectedTab == "ALL") { selectedTab = "ALL" }
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    ContactTabItem("ORDERS", selectedTab == "ORDERS") { selectedTab = "ORDERS" }
                }
            }
        }
    }


@Composable
private fun ContactTabItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        color = Color(0xFF334D6F),
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun ContactActionButton(
    painter: androidx.compose.ui.graphics.painter.Painter,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color(0xFF313131))
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

@Composable
private fun ContactOrderItem(order: ContactOrder) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 2.dp, bottom = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ContactDetailOrderItemShape())
                .background(order.color)
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.notess),
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
                    text = order.date,
                    fontSize = 12.sp,
                    color = Color(0xFF334D6F).copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = order.description,
                fontSize = 13.sp,
                color = Color(0xFF334D6F),
                lineHeight = 20.sp
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

@Composable
private fun ContactSimpleCallItem(item: ContactTimelineItem.SimpleCall) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFAEDEF4))
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(R.drawable.phone),
                contentDescription = null,
                tint = Color(0xFF334D6F),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "CALL",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334D6F)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(R.drawable.arrow_up_right),
                contentDescription = null,
                tint = Color(0xFF334D6F),
                modifier = Modifier
                    .size(20.dp)
                    .let { if (item.isIncoming) it else it }
            )
        }
        Text(
            text = item.time,
            fontSize = 14.sp,
            color = Color(0xFF334D6F).copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun ContactDetailedCallItem(item: ContactTimelineItem.DetailedCall) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(ContactDetailOrderItemShape())
                .background(Color(0xFFAEDEF4))
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.phone),
                        contentDescription = null,
                        tint = Color(0xFF334D6F),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "CALL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334D6F)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(R.drawable.arrow_up_right),
                        contentDescription = null,
                        tint = Color(0xFF334D6F),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = item.time,
                    fontSize = 14.sp,
                    color = Color(0xFF334D6F).copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.description,
                fontSize = 13.sp,
                color = Color(0xFF334D6F),
                lineHeight = 20.sp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(y = 15.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFF313131)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.pensil),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private class ContactDetailHeaderShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cutoutWidth = 230f * density.density
                val cutoutHeight = 32f * density.density
                val rightMargin = 0f * density.density

                val cutoutRight = size.width - rightMargin
                val cutoutLeft = cutoutRight - cutoutWidth
                val cutoutBottom = size.height
                val cutoutTop = cutoutBottom - cutoutHeight

                val smoothFactor = 25f * density.density

                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height)
                lineTo(cutoutRight, size.height)

                cubicTo(
                    cutoutRight - smoothFactor, size.height,
                    cutoutRight - smoothFactor, cutoutTop,
                    cutoutRight - smoothFactor * 2, cutoutTop
                )

                lineTo(cutoutLeft + smoothFactor * 2, cutoutTop)

                cubicTo(
                    cutoutLeft + smoothFactor, cutoutTop,
                    cutoutLeft + smoothFactor, size.height,
                    cutoutLeft, size.height
                )

                lineTo(0f, size.height)
                lineTo(0f, 0f)

                close()
            }
        )
    }
}

private class ContactDetailContentShape(val selectedTab: String) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cornerRadius = 32f * density.density
                val bodyCornerRadius = 16f * density.density
                val tabHeight = 40f * density.density
                val smoothFactor = 28f * density.density
                val bodyTop = tabHeight

                val sectionWidth = size.width / 2f
                val tabWidth = sectionWidth

                val tabStartX = when (selectedTab) {
                    "ALL" -> 0f
                    "ORDERS" -> sectionWidth
                    else -> 0f
                }
                val tabEndX = tabStartX + tabWidth

                moveTo(0f, size.height)
                lineTo(size.width, size.height)

                if (selectedTab == "ORDERS") {
                    lineTo(size.width, cornerRadius)
                    quadraticTo(size.width, 0f, size.width - cornerRadius, 0f)
                    lineTo(tabStartX + smoothFactor, 0f)
                    cubicTo(
                        tabStartX, 0f,
                        tabStartX, bodyTop,
                        tabStartX - smoothFactor, bodyTop
                    )
                    lineTo(bodyCornerRadius, bodyTop)
                    quadraticTo(0f, bodyTop, 0f, bodyTop + bodyCornerRadius)
                } else {
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
                }

                close()
            }
        )
    }
}

private class ContactDetailOrderItemShape : Shape {
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
