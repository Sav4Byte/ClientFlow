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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GoalsScreen(
    onBackClick: () -> Unit = {}
) {
    val gradientColors = listOf(
        Color(0xFF9FD9D9), // Turquoise/mint at top
        Color(0xFFB8F2F2), // Lighter and more saturated mint
        Color(0xFFB8F2F2),
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
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                // White block with cutout shape - cutout is at the TOP right
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(GoalsHeaderWithCutoutShape())
                        .background(Color.White)
                ) {
                    // Header with back button and title inside white block
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onBackClick() },
                            tint = Color(0xFF333333)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Goals for next month",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
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
                    GoalsActionButton(
                        icon = Icons.Default.Delete,
                        onClick = { }
                    )
                    GoalsActionButton(
                        icon = Icons.Default.Share,
                        onClick = { }
                    )
                    GoalsActionButton(
                        icon = Icons.Default.Edit,
                        onClick = { }
                    )
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            // Main content white block - full width with padding only for content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GoalParagraph(
                        text = "Increase overall sales by 15–18% by focusing on\npromoting the new seasonal collection and highlighting\nbest-selling items across all channels."
                    )
                    GoalParagraph(
                        text = "Close at least four active deals with boutique retailers\nand multi-brand stores, especially those interested in\nour upcoming Fall/Winter line."
                    )
                    GoalParagraph(
                        text = "Grow the B2B lead pipeline by identifying 25 new\npotential retail partners through trade shows, social\nmedia outreach, and referrals from existing clients."
                    )
                    GoalParagraph(
                        text = "Improve follow-up consistency with current prospects\nby implementing a structured weekly check-in\nschedule and providing personalized lookbooks based\non their store profile."
                    )
                    GoalParagraph(
                        text = "Strengthen relationships with existing partners by\noffering early access to new arrivals, updated\nwholesale pricing, and limited-edition items to\nencourage repeat orders."
                    )
                    GoalParagraph(
                        text = "Increase online wholesale inquiries by updating\nproduct descriptions, enhancing visual content (model\nshots, detail close-ups), and promoting the collection\nvia targeted email campaigns."
                    )
                    GoalParagraph(
                        text = "Reduce response time to new leads to under 6 hours\nto improve conversion rates and maintain a\ncompetitive advantage in the fast-moving fashion\nmarket."
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalParagraph(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Color(0xFF333333),
        lineHeight = 24.sp,
        letterSpacing = (-0.3).sp
    )
}

@Composable
private fun GoalsActionButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF3A3A3A))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

class GoalsHeaderWithCutoutShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                // Cutout parameters
                val cutoutWidth = 220f * density.density
                val cutoutHeight = 36f * density.density
                val rightMargin = 0f * density.density
                
                // Position cutout at bottom right
                val cutoutRight = size.width - rightMargin
                val cutoutLeft = cutoutRight - cutoutWidth
                val cutoutBottom = size.height
                val cutoutTop = cutoutBottom - cutoutHeight
                
                // Control points for smooth transitions (S-curve)
                val smoothFactor = 25f * density.density

                // Start from top-left corner
                moveTo(0f, 0f)
                
                // Top edge
                lineTo(size.width, 0f)
                
                // Right edge down
                lineTo(size.width, size.height)
                
                // Bottom edge moving left to cutout start
                lineTo(cutoutRight, size.height)
                
                // Right side: curve UP and LEFT into the cutout
                cubicTo(
                    cutoutRight - smoothFactor, size.height,
                    cutoutRight - smoothFactor, cutoutTop,
                    cutoutRight - smoothFactor * 2, cutoutTop
                )
                
                // Line across top of cutout
                lineTo(cutoutLeft + smoothFactor * 2, cutoutTop)
                
                // Left side: curve DOWN and LEFT out of the cutout
                cubicTo(
                    cutoutLeft + smoothFactor, cutoutTop,
                    cutoutLeft + smoothFactor, size.height,
                    cutoutLeft, size.height
                )
                
                // Continue to left edge
                lineTo(0f, size.height)
                
                // Left edge up
                lineTo(0f, 0f)
                
                close()
            }
        )
    }
}
