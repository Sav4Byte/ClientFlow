package com.coffeecodedevs.clientflow.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeecodedevs.clientflow.data.Contact
import com.coffeecodedevs.clientflow.data.ContactType



import androidx.compose.ui.unit.Dp

// Custom shape for bottom bar with smooth semicircular cutout
class BottomBarWithCutoutShape(private val cutoutRadiusDp: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cornerRadius = size.height / 2f
                val R = with(density) { cutoutRadiusDp.toPx() }
                val r = 15f * density.density // Increased smoothing radius
                val cutoutCenterX = size.width / 2f
                
                // Calculate distance between centers of shoulder circle and cup circle
                // D^2 + r^2 = (R + r)^2
                // D = sqrt((R+r)^2 - r^2) = sqrt(R^2 + 2Rr)
                val D = kotlin.math.sqrt(R * R + 2 * R * r)
                
                // Calculate angles
                // theta is the angle of the vector from Shoulder Center to Cup Center
                // relative to the horizontal.
                // Vector is (D, -r) because Cup Center is at (0,0) relative to Shoulder's y=r,
                // but we are in y-down coordinates.
                // Actually, let's use standard math:
                // Shoulder Center: (cx - D, r)
                // Cup Center: (cx, 0)
                // Vector Shoulder->Cup: (D, -r)
                val theta = kotlin.math.atan2(-r, D)
                val thetaDeg = Math.toDegrees(theta.toDouble()).toFloat()
                
                // Cup Start Angle:
                // Vector Cup->Shoulder: (-D, r)
                val cupStartAngle = kotlin.math.atan2(r, -D)
                val cupStartDeg = Math.toDegrees(cupStartAngle.toDouble()).toFloat()
                
                // Cup End Angle:
                // Vector Cup->RightShoulder: (D, r)
                val cupEndAngle = kotlin.math.atan2(r, D)
                val cupEndDeg = Math.toDegrees(cupEndAngle.toDouble()).toFloat()
                
                // Start from top-left, after the corner
                moveTo(0f, cornerRadius)

                // Top-left corner
                arcTo(
                    rect = Rect(0f, 0f, cornerRadius * 2, cornerRadius * 2),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                // Line to start of shoulder
                lineTo(cutoutCenterX - D, 0f)

                // Left Shoulder Arc
                // Center: (cutoutCenterX - D, r)
                // Start: 270 (Top)
                // Sweep: to thetaDeg (which is approx -15, so 345)
                // Sweep angle = thetaDeg - (-90) = thetaDeg + 90
                arcTo(
                    rect = Rect(
                        cutoutCenterX - D - r, 0f,
                        cutoutCenterX - D + r, 2 * r
                    ),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = thetaDeg + 90f,
                    forceMoveTo = false
                )

                // Cup Arc
                // Center: (cutoutCenterX, 0)
                // Start: cupStartDeg (approx 165)
                // Sweep: to cupEndDeg (approx 15)
                // We go counter-clockwise (downwards), so sweep is negative
                // Sweep = cupEndDeg - cupStartDeg
                arcTo(
                    rect = Rect(
                        cutoutCenterX - R, -R,
                        cutoutCenterX + R, R
                    ),
                    startAngleDegrees = cupStartDeg,
                    sweepAngleDegrees = cupEndDeg - cupStartDeg,
                    forceMoveTo = false
                )

                // Right Shoulder Arc
                // Center: (cutoutCenterX + D, r)
                // Start: 180 - thetaDeg (approx 195)
                // Sweep: to 270
                // Sweep = 270 - (180 - thetaDeg) = 90 + thetaDeg
                // Wait, 180 - thetaDeg?
                // Vector RightShoulder->Cup is (-D, -r). Angle atan2(-r, -D) = -165 (or 195).
                // So start is correct.
                // Sweep is positive (clockwise) to 270.
                // 270 - 195 = 75. Correct.
                // Note: thetaDeg is negative. So 180 - (-15) = 195.
                // 90 + (-15) = 75. Correct.
                arcTo(
                    rect = Rect(
                        cutoutCenterX + D - r, 0f,
                        cutoutCenterX + D + r, 2 * r
                    ),
                    startAngleDegrees = 180f - thetaDeg,
                    sweepAngleDegrees = 90f + thetaDeg,
                    forceMoveTo = false
                )

                // Continue top edge to right corner
                lineTo(size.width - cornerRadius, 0f)

                // Top-right corner
                arcTo(
                    rect = Rect(
                        left = size.width - cornerRadius * 2,
                        top = 0f,
                        right = size.width,
                        bottom = cornerRadius * 2
                    ),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                // Right edge
                lineTo(size.width, size.height - cornerRadius)

                // Bottom-right corner
                arcTo(
                    rect = Rect(
                        left = size.width - cornerRadius * 2,
                        top = size.height - cornerRadius * 2,
                        right = size.width,
                        bottom = size.height
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                // Bottom edge
                lineTo(cornerRadius, size.height)

                // Bottom-left corner
                arcTo(
                    rect = Rect(0f, size.height - cornerRadius * 2, cornerRadius * 2, size.height),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                // Close path
                close()
            }
        )
    }
}

// Custom shape for CLIENTS tab - rounded top-left, wave cutout on top-right, bulge on bottom-right
// Custom shape for CLIENTS tab - rounded top-left, smooth S-curve on right
class ClientsTabShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val topLeftRadius = 20f * density.density
                val curveWidth = 55f * density.density // Increased width for much less vertical curve

                // Start from top-left
                moveTo(0f, topLeftRadius)

                // Top-left corner (rounded)
                arcTo(
                    rect = Rect(0f, 0f, topLeftRadius * 2, topLeftRadius * 2),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                // Top edge to start of curve
                val curveStart = size.width - curveWidth * 0.2f
                lineTo(curveStart, 0f)

                // Smooth S-curve down and to the right
                // Stronger rounding at corners (long horizontal handles)
                cubicTo(
                    curveStart + curveWidth * 0.9f, 0f, // CP1: Long horizontal handle from top
                    size.width + curveWidth * 0.1f, size.height, // CP2: Long horizontal handle from bottom
                    size.width + curveWidth, size.height // End point
                )

                // Bottom edge from curve end back to left
                lineTo(0f, size.height)

                // Left edge back to start
                lineTo(0f, topLeftRadius)

                close()
            }
        )
    }
}

// Custom shape for EMPLOYEES tab - smooth S-curve on left, rounded top-right
class EmployeesTabShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val topRightRadius = 30f * density.density
                val curveWidth = 70f * density.density // Increased width for much less vertical curve

                // Start from top-left (extended left)
                // Curve starts at bottom-left (extended) and goes to top-left (indented)
                val curveStartBottom = -curveWidth
                val curveEndTop = curveWidth * 0.2f

                moveTo(curveStartBottom, size.height)

                // Smooth S-curve up and to the right
                // Stronger rounding at corners (long horizontal handles)
                cubicTo(
                    curveStartBottom + curveWidth * 0.9f, size.height, // CP1: Long horizontal handle from bottom
                    curveEndTop - curveWidth * 0.9f, 0f, // CP2: Long horizontal handle from top
                    curveEndTop, 0f // End point
                )

                // Top edge to top-right corner
                lineTo(size.width - topRightRadius, 0f)

                // Top-right corner (rounded)
                arcTo(
                    rect = Rect(
                        left = size.width - topRightRadius * 2,
                        top = 0f,
                        right = size.width,
                        bottom = topRightRadius * 2
                    ),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                // Right edge
                lineTo(size.width, size.height)

                // Bottom edge
                lineTo(curveStartBottom, size.height)

                close()
            }
        )
    }
}

// Custom shape for header with cutout on the right for search bar
class HeaderWithSearchCutoutShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val bottomLeftRadius = 15f * density.density
                // Flat right corner as requested

                // Cutout parameters - smooth and natural
                val cutoutWidth = 310f * density.density
                val cutoutHeight = 54f * density.density
                val rightMargin = 0f * density.density

                // Position cutout at bottom right
                val cutoutRight = size.width - rightMargin
                val cutoutLeft = cutoutRight - cutoutWidth
                val cutoutBottom = size.height
                val cutoutTop = cutoutBottom - cutoutHeight

                // Control points for smooth transitions
                val smoothFactor = 25f * density.density // Reduced from 35f as requested

                // Start from top-left
                moveTo(0f, 0f)

                // Top edge
                lineTo(size.width, 0f)

                // Right edge down to bottom (flat corner)
                lineTo(size.width, size.height)

                // Bottom edge moving left to cutout start
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

                // Continue to bottom-left corner
                lineTo(bottomLeftRadius, size.height)

                // Bottom-left corner
                arcTo(
                    rect = Rect(
                        left = 0f,
                        top = size.height - bottomLeftRadius * 2,
                        right = bottomLeftRadius * 2,
                        bottom = size.height
                    ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                // Left edge back to start
                lineTo(0f, 0f)

                close()
            }
        )
    }
}

@Composable
fun ContactsScreen() {
    var selectedTab by remember { mutableStateOf(ContactType.CLIENT) }
    var searchQuery by remember { mutableStateOf("") }
    var expandedContactId by remember { mutableStateOf<Int?>(null) }
    var selectedBottomTab by remember { mutableStateOf(0) }

    // Sample data
    val contacts = remember {
        listOf(
            Contact(1, "Alan Smith", ContactType.CLIENT),
            Contact(2, "Ali Conors", ContactType.CLIENT),
            Contact(3, "Bailey Brown", ContactType.CLIENT),
            Contact(4, "Benjamin Smith", ContactType.CLIENT),
            Contact(5, "Brandon Johnson", ContactType.CLIENT),
            Contact(6, "Dan Dilan", ContactType.CLIENT),
            Contact(7, "Daniel Brooks", ContactType.CLIENT, "Works on San, Mon, Wed and Fri. And sth else.... Don' t call after 16:00."),
            Contact(8, "David Carter", ContactType.CLIENT),
            Contact(9, "Diana Collins", ContactType.CLIENT),
            Contact(10, "Eleanor Smith", ContactType.CLIENT),
            Contact(11, "Ethan Williams", ContactType.CLIENT),
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFAEDEF4), // Even lighter blue
                        Color(0xFFFFDAB9)  // Brighter beige
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Spacer for status bar area to show gradient
            Spacer(modifier = Modifier.height(24.dp))

            // Header section with CONTACTS text and cutout
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
            ) {
                // White background with cutout
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color.White,
                            shape = HeaderWithSearchCutoutShape()
                        )
                        .padding(start = 16.dp, top = 20.dp, bottom = 20.dp)
                ) {
                    Text(
                        text = "CONTACTS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334D6F)
                    )
                }

                // Search bar positioned in the cutout
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 39.dp, bottom = 3.dp) // Centered in 300dp cutout (0 margin + 39 side padding)
                        .width(222.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Search",
                            color = Color(0xFF8B9BA8),
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF8B9BA8),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                 }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs and Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                // Tabs Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(37.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Clients Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .graphicsLayer { clip = false }
                            .zIndex(if (selectedTab == ContactType.CLIENT) 1f else 0f)
                            .drawBehind {
                                if (selectedTab == ContactType.CLIENT) {
                                    val outline = ClientsTabShape().createOutline(size, layoutDirection, this)
                                    if (outline is Outline.Generic) {
                                        drawPath(outline.path, Color.White)
                                    }
                                }
                            }
                            .clickable { selectedTab = ContactType.CLIENT },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CLIENTS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334D6F)
                        )
                    }

                    // Employees Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .graphicsLayer { clip = false }
                            .zIndex(if (selectedTab == ContactType.EMPLOYEE) 1f else 0f)
                            .drawBehind {
                                if (selectedTab == ContactType.EMPLOYEE) {
                                    val outline = EmployeesTabShape().createOutline(size, layoutDirection, this)
                                    if (outline is Outline.Generic) {
                                        drawPath(outline.path, Color.White)
                                    }
                                }
                            }
                            .clickable { selectedTab = ContactType.EMPLOYEE },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EMPLOYEES",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334D6F)
                        )
                    }
                }

                // Contact list in White Card
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            Color.White,
                            when (selectedTab) {
                                ContactType.CLIENT -> RoundedCornerShape(topEnd = 10.dp)
                                ContactType.EMPLOYEE -> RoundedCornerShape(topStart = 10.dp)
                            }
                        ),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 130.dp)
            ) {
                val filteredContacts = contacts.filter { it.type == selectedTab }
                val groupedContacts = filteredContacts.groupBy { it.name.first().toString() }

                groupedContacts.forEach { (letter, contactsInGroup) ->
                    item {
                        Text(
                            text = "${letter}${letter.lowercase()}",
                            fontSize = 13.sp,
                            color = Color(0xFF7A8C99),
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                        )
                    }

                    items(contactsInGroup) { contact ->
                        ContactItem(
                            contact = contact,
                            isExpanded = expandedContactId == contact.id,
                            onToggleExpand = {
                                expandedContactId = if (expandedContactId == contact.id) null else contact.id
                            }
                        )
                    }
                    
                    // Divider after each letter group
                    item {
                        androidx.compose.material3.HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = Color(0xFFE0E0E0)
                        )
                    }
                }
                }
            }
        }

        // Bottom Navigation Bar with FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 64.dp, end = 64.dp, bottom = 60.dp)
        ) {
            // Bottom Navigation Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = BottomBarWithCutoutShape(cutoutRadiusDp = 36.dp),
                color = Color(0xFF313131),
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // First icon - Person with blue circular background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0x80AEE0FF))
                            .clickable { selectedBottomTab = 0 },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    BottomNavIcon(
                        icon = Icons.Default.List,
                        selected = selectedBottomTab == 1,
                        onClick = { selectedBottomTab = 1 }
                    )

                    Spacer(modifier = Modifier.width(70.dp)) // Space for FAB

                    BottomNavIcon(
                        icon = Icons.Default.Menu,
                        selected = selectedBottomTab == 2,
                        onClick = { selectedBottomTab = 2 }
                    )

                    BottomNavIcon(
                        icon = Icons.Default.DateRange,
                        selected = selectedBottomTab == 3,
                        onClick = { selectedBottomTab = 3 }
                    )
                }
            }

            // Floating Action Button centered on top
            FloatingActionButton(
                onClick = { },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-25).dp)
                    .size(56.dp),
                containerColor = Color(0xFF313131),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                if (selected) Color.White.copy(alpha = 0.95f)
                else Color.White.copy(alpha = 0.4f)            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = Color(0xFF2C4A5E)
        )
    }
}

@Composable
fun ContactItem(
    contact: Contact,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "chevron rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickable(onClick = onToggleExpand),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = contact.name,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2C4A5E)
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = Color(0xFF8B9BA8),
                modifier = Modifier
                    .size(22.dp)
                    .rotate(rotationAngle)
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp)
            ) {
                if (contact.note != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF8DC5E0))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = contact.note,
                            fontSize = 13.sp,
                            color = Color(0xFF2C4A5E),
                            lineHeight = 18.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    ActionButton(
                        icon = Icons.Default.Phone,
                        onClick = { }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    ActionButton(
                        icon = Icons.Default.Email,
                        onClick = { }
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    ActionButton(
                        icon = Icons.Default.Info,
                        onClick = { }
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color(0xFF34495E))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun BottomNavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) Color.White else Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(22.dp)
        )
    }
}
