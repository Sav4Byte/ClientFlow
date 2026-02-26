package com.coffeecodedevs.clientflow.ui.screens


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.coffeecodedevs.clientflow.R


// Custom shape for bottom bar with smooth semicircular cutout

val CustomContactIcon: ImageVector
    get() {
        if (_customContactIcon != null) return _customContactIcon!!
        _customContactIcon = ImageVector.Builder(
            name = "Contact",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Head
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(12f, 4f)
                curveTo(14.21f, 4f, 16f, 5.79f, 16f, 8f)
                curveTo(16f, 10.21f, 14.21f, 12f, 12f, 12f)
                curveTo(9.79f, 12f, 8f, 10.21f, 8f, 8f)
                curveTo(8f, 5.79f, 9.79f, 4f, 12f, 4f)
                close()
            }
            // Body (Arc)
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round
            ) {
                moveTo(4f, 20f)
                quadTo(12f, 13f, 20f, 20f)
            }
        }.build()
        return _customContactIcon!!
    }
private var _customContactIcon: ImageVector? = null




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

// Custom shape for comment box with cutout for action buttons
class CommentBoxWithCutoutShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val cornerRadius = 8f * density.density
                
                // Parameters for the cutout
                val cutoutHeight = 23f * density.density // Increased to maintain ceiling position
                val flatWidth = 120f * density.density // Reverted to 120dp
                val slopeWidth = 60f * density.density // Tilt
                val smoothing = 40f * density.density // Smoothing
                val topCornerRadius = 10f * density.density // Reduced radius for cutout corner

                // Start from top-left
                moveTo(cornerRadius, 0f)

                // Top edge
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

                // Right edge down to start of cutout top corner
                lineTo(size.width, size.height - cutoutHeight - topCornerRadius)

                // Rounded corner into the cutout "ceiling"
                quadraticBezierTo(
                    size.width, size.height - cutoutHeight,
                    size.width - topCornerRadius, size.height - cutoutHeight
                )

                // Line to the start of the S-curve
                lineTo(size.width - flatWidth, size.height - cutoutHeight)

                // Smooth S-curve transition down to the bottom
                // Using cubic bezier for better control over "tilt" and "rounding"
                cubicTo(
                    size.width - flatWidth - smoothing, size.height - cutoutHeight, // CP1: Horizontal from top
                    size.width - flatWidth - slopeWidth + smoothing, size.height,   // CP2: Horizontal from bottom
                    size.width - flatWidth - slopeWidth, size.height                // End point
                )

                // Bottom edge to left corner
                lineTo(cornerRadius, size.height)

                // Bottom-left corner
                arcTo(
                    rect = Rect(
                        left = 0f,
                        top = size.height - cornerRadius * 2,
                        right = cornerRadius * 2,
                        bottom = size.height
                    ),
                    startAngleDegrees = 90f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                // Left edge
                lineTo(0f, cornerRadius)

                // Top-left corner
                arcTo(
                    rect = Rect(
                        left = 0f,
                        top = 0f,
                        right = cornerRadius * 2,
                        bottom = cornerRadius * 2
                    ),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )

                close()
            }
        )
    }

}


// Custom shape for header with cutout on the right for search bar
@Composable
fun ContactsScreen(
    onContactClick: (com.coffeecodedevs.clientflow.data.Contact, Boolean) -> Unit = { _, _ -> },
    onCreateClick: () -> Unit = {},
    viewModel: com.coffeecodedevs.clientflow.data.ContactViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    var selectedTab by remember { mutableStateOf("CLIENT") }
    var searchQuery by remember { mutableStateOf("") }
    var expandedContactId by remember { mutableStateOf<Int?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val allContacts by viewModel.allContacts.collectAsState(initial = emptyList())

    val filteredContacts = allContacts.filter {
        val matchesType = when (selectedTab) {
            "CLIENT" -> it.isClient
            "EMPLOYEE" -> it.isEmployee
            else -> true
        }
        val matchesSearch = it.firstName.contains(searchQuery, ignoreCase = true) ||
                it.lastName.contains(searchQuery, ignoreCase = true)
        matchesType && matchesSearch
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFAEDEF4),
                        Color(0xFFFFDAB9)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.White, shape = HeaderWithSearchCutoutShape())
                        .padding(start = 16.dp, top = 20.dp, bottom = 5.dp)
                ) {
                    Text(
                        text = "CONTACTS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334D6F)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 36.dp, bottom = 0.dp)
                        .offset(y = 10.dp)
                        .width(228.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f).fillMaxHeight().padding(end = 8.dp),
                            textStyle = TextStyle(color = Color(0xFF334D6F), fontSize = 14.sp),
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                                    if (searchQuery.isEmpty()) {
                                        Text("Search", color = Color(0xFF8B9BA8), fontSize = 14.sp)
                                    }
                                    innerTextField()
                                }
                            }
                        )
                        Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search", tint = Color(0xFF8B9BA8), modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Tabs and Content
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(37.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight().graphicsLayer { clip = false }.zIndex(if (selectedTab == "CLIENT") 1f else 0f)
                            .drawBehind {
                                if (selectedTab == "CLIENT") {
                                    val outline = ClientsTabShape().createOutline(size, layoutDirection, this)
                                    if (outline is Outline.Generic) drawPath(outline.path, Color.White)
                                }
                            }
                            .clickable { selectedTab = "CLIENT" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CLIENTS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334D6F))
                    }

                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight().graphicsLayer { clip = false }.zIndex(if (selectedTab == "EMPLOYEE") 1f else 0f)
                            .drawBehind {
                                if (selectedTab == "EMPLOYEE") {
                                    val outline = EmployeesTabShape().createOutline(size, layoutDirection, this)
                                    if (outline is Outline.Generic) drawPath(outline.path, Color.White)
                                }
                            }
                            .clickable { selectedTab = "EMPLOYEE" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("EMPLOYEES", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334D6F))
                    }
                }

                // List background
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                        .background(
                            Color.White,
                            if (selectedTab == "CLIENT") RoundedCornerShape(topEnd = 10.dp)
                            else RoundedCornerShape(topStart = 10.dp)
                        )
                ) {
                    if (filteredContacts.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(R.drawable.contact), 
                                    contentDescription = null, 
                                    tint = Color(0xFF8B9BA8).copy(alpha = 0.5f),
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No contacts",
                                    fontSize = 18.sp,
                                    color = Color(0xFF8B9BA8),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 120.dp)
                        ) {
                            val groupedContacts = filteredContacts.groupBy { it.firstName.first().toString().uppercase() }
                            groupedContacts.entries.sortedBy { it.key }.forEach { (letter, contactsInGroup) ->
                                item {
                                    Text(
                                        text = "${letter}${letter.lowercase()}",
                                        fontSize = 13.sp,
                                        color = Color(0xFF7A8C99),
                                        modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                                    )
                                }
                                items(contactsInGroup) { contact ->
                                    ContactItem(
                                        contact = contact,
                                        isExpanded = expandedContactId == contact.id,
                                        onToggleExpand = {
                                            expandedContactId = if (expandedContactId == contact.id) null else contact.id
                                        },
                                        onCallClick = { 
                                            contact.phones.firstOrNull()?.let { 
                                                com.coffeecodedevs.clientflow.utils.ContactActions.callContact(context, it)
                                            }
                                        },
                                        onSmsClick = {
                                            contact.phones.firstOrNull()?.let { 
                                                com.coffeecodedevs.clientflow.utils.ContactActions.sendSms(context, it)
                                            }
                                        },
                                        onViewClick = { 
                                            onContactClick(contact, true) 
                                        }

                                    )
                                }
                            }
                        }
                    }
                }
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
    contact: com.coffeecodedevs.clientflow.data.Contact,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onCallClick: () -> Unit = {},
    onSmsClick: () -> Unit = {},
    onViewClick: () -> Unit = {}
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "chevron rotation"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .clickable(onClick = onToggleExpand),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${contact.firstName} ${contact.lastName}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF334D6F)
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
                    .padding(top = 4.dp, bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (contact.note != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 13.dp)
                                .clip(CommentBoxWithCutoutShape())
                                .background(Color(0xFFAEE0FF))
                                .padding(start = 7.dp, top = 10.dp, end = 15.dp, bottom = 35.dp)
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
                            .align(Alignment.BottomEnd)
                            .padding(end = 8.dp, bottom = 0.dp)
                            .offset(y = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionButton(
                            painter = painterResource(R.drawable.phone),
                            onClick = onCallClick,
                        )
                        ActionButton(
                            painter = painterResource(R.drawable.sms),
                            onClick = onSmsClick,
                        )
                        ActionButton(
                            painter = painterResource(R.drawable.eye),
                            onClick = onViewClick,
                        )
                    }
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
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF313131))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ActionButton(
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
            tint = Color.White,
            modifier = Modifier.size(35.dp)
        )
    }
}

@Composable
fun BottomNavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    color: Color? = null,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color ?: if (selected) Color.White else Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun BottomNavIcon(
    painter: androidx.compose.ui.graphics.painter.Painter,
    selected: Boolean,
    color: Color? = null,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = color ?: if (selected) Color.White else Color.White.copy(alpha = 0.4f),
            modifier = Modifier.size(22.dp)
        )
    }
}

// Shared shape for header with search cutout (used by ContactsScreen and ThirdScreen)
internal class HeaderWithSearchCutoutShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val bottomLeftRadius = 15f * density.density

                // Cutout parameters - smooth and natural
                val cutoutWidth = 300f * density.density
                val cutoutHeight = 42f * density.density
                val rightMargin = 0f * density.density

                // Position cutout at bottom right
                val cutoutRight = size.width - rightMargin
                val cutoutLeft = cutoutRight - cutoutWidth
                val cutoutBottom = size.height
                val cutoutTop = cutoutBottom - cutoutHeight

                // Control points for smooth transitions
                val smoothFactor = 25f * density.density

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
