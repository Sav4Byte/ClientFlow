package com.coffeecodedevs.clientflow.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
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

@Composable
fun GoalsScreen(
    noteTitle: String = "Daniel Brooks",
    noteDescription: String = "",
    onBackClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onUpdateClick: (String, String) -> Unit = { _, _ -> },
    onPencilClick: (() -> Unit)? = null
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(noteTitle) }
    var editedDescription by remember { mutableStateOf(noteDescription) }
    
    var selectedTab by remember { mutableStateOf("ORDERS") }

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
                .padding(top = 24.dp)
                .verticalScroll(rememberScrollState())
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
                        if (isEditing) {
                            androidx.compose.foundation.text.BasicTextField(
                                value = editedTitle,
                                onValueChange = { editedTitle = it },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333)
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Text(
                                text = editedTitle,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
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
                    GoalsActionButton(
                        painter = painterResource(R.drawable.trash),
                        onClick = { onDeleteClick() }
                    )
                    GoalsActionButton(
                        painter = painterResource(R.drawable.share),
                        onClick = { onShareClick() }
                    )
                    GoalsActionButton(
                        painter = if (isEditing) androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.Add) else painterResource(R.drawable.pensil),
                        onClick = { 
                            if (isEditing) {
                                onUpdateClick(editedTitle, editedDescription)
                                isEditing = false
                            } else {
                                if (onPencilClick != null) {
                                    onPencilClick()
                                } else {
                                    isEditing = true
                                }
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(44.dp))

            // Display the note description
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.7f))
                    .padding(20.dp)
            ) {
                if (isEditing) {
                    androidx.compose.foundation.text.BasicTextField(
                        value = editedDescription,
                        onValueChange = { editedDescription = it },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 15.sp,
                            color = Color(0xFF333333),
                            lineHeight = 22.sp,
                            letterSpacing = (-0.1).sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                } else if (editedDescription.isNotEmpty()) {
                    GoalParagraph(text = editedDescription)
                }
            }
        }
    }
}

@Composable
private fun GoalParagraph(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        color = Color(0xFF333333),
        lineHeight = 22.sp,
        letterSpacing = (-0.1).sp
    )
}

@Composable
private fun GoalsActionButton(
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
            modifier = Modifier.size(20.dp)
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
    }}


