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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import androidx.compose.ui.res.stringResource

data class FifthOrder(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val time: String
)

sealed class FifthTimelineItem {
    data class SimpleCall(
        val time: String,
        val isIncoming: Boolean = false
    ) : FifthTimelineItem()

    data class DetailedCall(
        val time: String,
        val description: String
    ) : FifthTimelineItem()

    data class DetailedOrder(
        val title: String,
        val date: String,
        val time: String,
        val description: String
    ) : FifthTimelineItem()
}

@Composable
fun FifthScreen(
    onBackClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("ORDERS") }
    var selectedBottomTab by remember { mutableStateOf(1) }
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val backDesc = stringResource(R.string.back_desc)
    val workDaysSample = stringResource(R.string.work_days_sample)
    val callTimeLimitSample = stringResource(R.string.call_time_limit_sample)
    val allTab = stringResource(R.string.all_tab)
    val ordersTab = stringResource(R.string.orders_tab)
    val activityTab = stringResource(R.string.activity_tab)
    val addDesc = stringResource(R.string.add_desc)
    val callLabel = stringResource(R.string.call_label)
    val shareDesc = stringResource(R.string.share_desc)
    val editDesc = stringResource(R.string.edit_desc)
    val deleteBtn = stringResource(R.string.delete_desc)
    
    val orders = remember(context) {
        listOf(
            FifthOrder(
                id = 1,
                title = context.getString(R.string.sample_order_title_1),
                description = context.getString(R.string.sample_order_desc_1),
                time = "14:45",
                date = ""
            ),
            FifthOrder(
                id = 2,
                title = context.getString(R.string.sample_order_title_2),
                description = context.getString(R.string.sample_order_desc_2),
                time = "16:20",
                date = ""
            )
        )
    }

    val allTabItems = remember(context) {
        listOf(
            FifthTimelineItem.SimpleCall("15:02"),
            FifthTimelineItem.DetailedCall(
                "14:45",
                context.getString(R.string.sample_order_desc_1)
            ),
            FifthTimelineItem.SimpleCall("Вс, 14:34", isIncoming = true),
            FifthTimelineItem.SimpleCall("19 окт., 17:56"),
            FifthTimelineItem.DetailedOrder(
                context.getString(R.string.sample_order_title_2),
                "17 окт.,",
                "14:45",
                context.getString(R.string.sample_order_desc_2)
            ),
            FifthTimelineItem.SimpleCall("12 сен., 17:56"),
            FifthTimelineItem.DetailedOrder(
                context.getString(R.string.sample_order_title_1),
                "11 сен.,",
                "14:45",
                context.getString(R.string.sample_order_desc_1)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF9BE5D6), Color(0xFFF7F2E9)),
                    startY = 0f,
                    endY = 1200f
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.statusBarsPadding())

            // Header with back button, name, and action buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                // White block with cutout
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(FifthHeaderCutoutShape())
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    ) {
                        // Back button and name
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.back),
                                    contentDescription = backDesc,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { onBackClick() },
                                    tint = Color(0xFF334D6F)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Daniel Brooks",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333)
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.thrash),
                                    contentDescription = deleteBtn,
                                    modifier = Modifier.size(24.dp).clickable { /* Delete */ },
                                    tint = Color.Red.copy(alpha = 0.6f)
                                )
                                Icon(
                                    painter = painterResource(R.drawable.share),
                                    contentDescription = shareDesc,
                                    modifier = Modifier.size(24.dp).clickable { /* Share */ },
                                    tint = Color(0xFF334D6F)
                                )
                                Icon(
                                    painter = painterResource(R.drawable.pensil),
                                    contentDescription = editDesc,
                                    modifier = Modifier.size(24.dp).clickable { /* Edit */ },
                                    tint = Color(0xFF334D6F)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Info text
                        Column(
                            modifier = Modifier.padding(start = 40.dp)
                        ) {
                            Text(
                                text = workDaysSample,
                                fontSize = 14.sp,
                                color = Color(0xFF334D6F).copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = callTimeLimitSample,
                                fontSize = 14.sp,
                                color = Color(0xFF334D6F).copy(alpha = 0.6f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Phone numbers
                        Column(
                            modifier = Modifier.padding(start = 40.dp)
                        ) {
                            Text(
                                text = "+380 67 895 50 89",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "+380 93 578 90 28",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }

                // Action buttons in cutout
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = 10.dp)
                        .width(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
                ) {
                    FifthActionButton(
                        painter = painterResource(R.drawable.dial),
                        onClick = { }
                    )
                    FifthActionButton(
                        painter = painterResource(R.drawable.sms),
                        onClick = { }
                    )
                    FifthActionButton(
                        painter = painterResource(R.drawable.share),
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs section
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FifthTabItem(allTab, selectedTab == "ALL") { selectedTab = "ALL" }
                FifthTabItem(ordersTab, selectedTab == "ORDERS") { selectedTab = "ORDERS" }
                FifthTabItem(activityTab, selectedTab == "ACTIVITY") { selectedTab = "ACTIVITY" }
            }

            // White content block with orders - extends to bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                when (selectedTab) {
                    "ORDERS" -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            items(orders) { order ->
                                OrderItem(order)
                            }
                        }
                    }
                    "ALL" -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            items(allTabItems) { item ->
                                when (item) {
                                    is FifthTimelineItem.SimpleCall -> FifthCallItem(item, callLabel)
                                    is FifthTimelineItem.DetailedCall -> FifthDetailedCallItem(item, callLabel)
                                    is FifthTimelineItem.DetailedOrder -> FifthDetailedOrderItem(item)
                                }
                            }
                        }
                    }
                }
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
                shape = FifthScreenBottomBarShape(cutoutRadiusDp = 36.dp),
                color = Color(0xFF313131),
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FifthBottomNavIcon(painterResource(R.drawable.contact), selectedBottomTab == 0) { selectedBottomTab = 0 }
                    FifthBottomNavIcon(painterResource(R.drawable.notes), selectedBottomTab == 1) { selectedBottomTab = 1 }
                    Spacer(modifier = Modifier.width(70.dp))
                    FifthBottomNavIcon(painterResource(R.drawable.dial), selectedBottomTab == 2) { selectedBottomTab = 2 }
                    FifthBottomNavIcon(painterResource(R.drawable.calendar), selectedBottomTab == 3) { selectedBottomTab = 3 }
                }
            }
            FloatingActionButton(
                onClick = {},
                modifier = Modifier.align(Alignment.TopCenter).offset(y = (-25).dp).size(56.dp),
                containerColor = Color(0xFF313131),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = addDesc, tint = Color.White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
private fun OrderItem(order: FifthOrder) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE6D5F0))
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        painter = painterResource(R.drawable.notes),
                        contentDescription = null,
                        tint = Color(0xFF333333),
                        modifier = Modifier.size(24.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = order.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = order.date,
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                    if (order.time.isNotEmpty()) {
                        Text(
                            text = order.time,
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = order.description,
                fontSize = 14.sp,
                color = Color(0xFF333333),
                lineHeight = 20.sp
            )
        }
        
        // Arrow button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = 8.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF313131))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.pen),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun FifthTabItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
        color = if (selected) Color(0xFF334D6F) else Color(0xFF999999),
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun FifthActionButton(
    painter: androidx.compose.ui.graphics.painter.Painter,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF313131))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun FifthBottomNavIcon(
    painter: androidx.compose.ui.graphics.painter.Painter,
    selected: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = if (selected) Color.White else Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(22.dp)
        )
    }
}

private class FifthHeaderCutoutShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cutoutWidth = 220f * density.density
                val cutoutHeight = 36f * density.density
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

private class FifthScreenBottomBarShape(private val cutoutRadiusDp: androidx.compose.ui.unit.Dp) : Shape {
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

@Composable
private fun FifthCallItem(item: FifthTimelineItem.SimpleCall, callLabel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFAEDEF4))
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painterResource(R.drawable.phone),
                contentDescription = null,
                tint = Color(0xFF334D6F),
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                callLabel,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334D6F)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                painterResource(R.drawable.arrow_up_right),
                contentDescription = null,
                tint = Color(0xFF334D6F),
                modifier = Modifier.size(20.dp).rotate(if (item.isIncoming) 180f else 0f)
            )
        }
        Text(
            item.time,
            fontSize = 14.sp,
            color = Color(0xFF334D6F).copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun FifthDetailedCallItem(item: FifthTimelineItem.DetailedCall, callLabel: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(FifthItemShape())
                .background(Color(0xFFAEDEF4))
                .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painterResource(R.drawable.phone),
                        contentDescription = null,
                        tint = Color(0xFF334D6F),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        callLabel,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334D6F)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        painterResource(R.drawable.arrow_up_right),
                        contentDescription = null,
                        tint = Color(0xFF334D6F),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    item.time,
                    fontSize = 14.sp,
                    color = Color(0xFF334D6F).copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = item.description,
                fontSize = 14.sp,
                color = Color(0xFF334D6F),
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Pencil button
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

@Composable
private fun FifthDetailedOrderItem(item: FifthTimelineItem.DetailedOrder) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(FifthItemShape())
                .background(Color(0xFFE6D5F0))
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painterResource(R.drawable.notes),
                        contentDescription = null,
                        tint = Color(0xFF334D6F),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334D6F)
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        item.date,
                        fontSize = 12.sp,
                        color = Color(0xFF334D6F).copy(alpha = 0.6f)
                    )
                    Text(
                        item.time,
                        fontSize = 12.sp,
                        color = Color(0xFF334D6F).copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = item.description,
                fontSize = 14.sp,
                color = Color(0xFF334D6F),
                lineHeight = 20.sp
            )
             Spacer(modifier = Modifier.height(12.dp))
        }

        // Arrow button
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
                painter = painterResource(R.drawable.arrow_up_right),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private class FifthItemShape : Shape {
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
                quadraticBezierTo(size.width, 0f, size.width, cornerRadius)

                lineTo(size.width, size.height - cutoutHeight - topCornerRadius)

                quadraticBezierTo(
                    size.width, size.height - cutoutHeight,
                    size.width - topCornerRadius, size.height - cutoutHeight
                )

                lineTo(size.width - flatWidth, size.height - cutoutHeight)

                cubicTo(
                    size.width - flatWidth - smoothing, size.height - cutoutHeight,
                    size.width - flatWidth - slopeWidth + smoothing, size.height,
                    size.width - flatWidth - slopeWidth, size.height
                )

                lineTo(cornerRadius, size.height)
                quadraticBezierTo(0f, size.height, 0f, size.height - cornerRadius)
                lineTo(0f, cornerRadius)
                quadraticBezierTo(0f, 0f, cornerRadius, 0f)

                close()
            }
        )
    }
}
