package com.coffeecodedevs.clientflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeecodedevs.clientflow.R

@Composable
fun ThirdScreen(
    onBackClick: () -> Unit = {}
) {
    val gradientColors = listOf(
        Color(0xFFBDA1D7), // Purple at top
        Color(0xFFC5ACD9), // Lighter purple
        Color(0xFFD5C0E0), // Even lighter purple
        Color(0xFFF5EFE6), // Light beige/cream
        Color(0xFFF5E6D3)  // Light beige at bottom
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
                .padding(top = 24.dp) // Space for status bar to show gradient
        ) {
            // Top white block with header and cutout for buttons
            Box(
                modifier = Modifier
                    .width(408.dp)
                    .height(165.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                // White block with cutout shape - cutout is at the TOP right
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(ThirdHeaderCutoutShape())
                        .background(Color.White)
                ) {
                    // Header content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    ) {
                        // Back button and title
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.back),
                                contentDescription = "Back",
                                modifier = Modifier
                                    .size(28.dp)
                                    .offset(y = (-3).dp)
                                    .clickable { onBackClick() },
                                tint = Color.Unspecified
                            )



                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Summer collection",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Info section - aligned with title text
                        Column(
                            modifier = Modifier.padding(start = 40.dp)
                        ) {
                            Text(
                                text = "Dan Dillan",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFAAAAAA)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "12 Madison Street, Miami, FL",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFAAAAAA)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Oct. 12, 14:45",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFAAAAAA)
                            )
                        }
                    }
                }


                // Action buttons positioned evenly in the cutout
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = 10.dp)
                        .width(220.dp), // Match cutout width
                    horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally)
                ) {
                    ThirdScreenActionButton(
                        painter = painterResource(R.drawable.trash),
                        onClick = { }
                    )
                    ThirdScreenActionButton(
                        painter = painterResource(R.drawable.share),
                        onClick = { }
                    )
                    ThirdScreenActionButton(
                        painter = painterResource(R.drawable.pen),
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Main content white block - fixed size at bottom
            Box(
                modifier = Modifier
                    .width(376.dp)
                    .height(648.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ProductTitle(text = "Linen A-Line Dress")
                    ProductDetail(text = "• Color: White")
                    ProductDetail(text = "• Size Range: S–XL")
                    ProductDetail(text = "• Quantity: 120 pcs")
                    ProductDetail(text = "• Unit Price: $38.00")

                    Spacer(modifier = Modifier.height(8.dp))

                    ProductTitle(text = "Cotton Oversized T-Shirt")
                    ProductDetail(text = "• Color: Sand / Coral")
                    ProductDetail(text = "• Size Range: XS–XXL")
                    ProductDetail(text = "• Quantity: 200 pcs")
                    ProductDetail(text = "• Unit Price: $12.50")

                    Spacer(modifier = Modifier.height(8.dp))

                    ProductTitle(text = "High-Waist Denim Shorts")
                    ProductDetail(text = "• Wash: Light Blue")
                    ProductDetail(text = "• Size Range: 24–32")
                    ProductDetail(text = "• Quantity: 150 pcs")
                    ProductDetail(text = "• Unit Price: $22.00")

                    Spacer(modifier = Modifier.height(8.dp))

                    ProductTitle(text = "Printed Satin Slip Dress")
                    ProductDetail(text = "• Pattern: Tropical Floral")
                    ProductDetail(text = "• Size Range: XS–L")
                    ProductDetail(text = "• Quantity: 80 pcs")
                    ProductDetail(text = "• Unit Price: $44.50")
                }
            }
        }
    }
}

@Composable
private fun ProductTitle(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF333333),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun ProductDetail(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Color(0xFF333333),
        lineHeight = 20.sp
    )
}

@Composable
private fun ThirdScreenActionButton(
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

private class ThirdHeaderCutoutShape : Shape {
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

