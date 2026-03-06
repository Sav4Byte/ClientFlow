package com.coffeecodedevs.clientflow.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.coffeecodedevs.clientflow.data.Contact
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallResultDialog(
    contact: Contact,
    onDismiss: () -> Unit,
    onSave: (note: String, isNewClient: Boolean, orderValue: String?, reminderText: String?, reminderDate: String?, reminderTime: String?) -> Unit
) {
    var callNote by remember { mutableStateOf("") }
    
    var isNewClient by remember { mutableStateOf(false) }
    
    var isMadeOrder by remember { mutableStateOf(false) }
    var orderValue by remember { mutableStateOf("") }

    var isRemind by remember { mutableStateOf(true) }
    var reminderText by remember { mutableStateOf("") }
    var reminderDate by remember { mutableStateOf("07.10.2026") }
    var reminderTime by remember { mutableStateOf("09:00") }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    // No longer need pickingFor as it's only for Remind now

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(initialHour = 9, initialMinute = 0)

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .animateContentSize()
            .width(350.dp)
            .clip(RoundedCornerShape(32.dp)),
        content = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .background(Color(0xFFB3E5FC))
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "How did the talk go?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334D6F)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        BasicTextField(
                            value = callNote,
                            onValueChange = { callNote = it },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = Color(0xFF313131),
                                fontWeight = FontWeight.Normal,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 60.dp),
                            cursorBrush = SolidColor(Color(0xFF313131)),
                            decorationBox = { innerTextField ->
                                if (callNote.isEmpty()) {
                                    Text(
                                        text = "Client asked about availability of...",
                                        color = Color(0xFFAAAAAA),
                                        fontSize = 16.sp,
                                        lineHeight = 22.sp
                                    )
                                }
                                innerTextField()
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // New Client Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isNewClient = !isNewClient }
                        ) {
                            Checkbox(
                                checked = isNewClient,
                                onCheckedChange = { isNewClient = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF4A4A4A),
                                    uncheckedColor = Color(0xFF4A4A4A)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "New client",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF313131)
                            )
                        }

                        // Made an order Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isMadeOrder = !isMadeOrder }
                        ) {
                            Checkbox(
                                checked = isMadeOrder,
                                onCheckedChange = { isMadeOrder = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF4A4A4A),
                                    uncheckedColor = Color(0xFF4A4A4A)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Made an order",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF313131)
                            )
                        }
                        
                        // Show text field for order when checked, or just always show but disable?
                        if (isMadeOrder) {
                            BasicTextField(
                                value = orderValue,
                                onValueChange = { orderValue = it },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    color = Color(0xFF888888),
                                    fontWeight = FontWeight.Normal
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 52.dp, bottom = 12.dp),
                                cursorBrush = SolidColor(Color(0xFF313131)),
                                decorationBox = { innerTextField ->
                                    if (orderValue.isEmpty()) {
                                        Text(
                                            text = "Linen A-Line Dress",
                                            color = Color(0xFFAAAAAA),
                                            fontSize = 15.sp
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // Remind Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { isRemind = !isRemind }
                        ) {
                            Checkbox(
                                checked = isRemind,
                                onCheckedChange = { isRemind = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF4A4A4A),
                                    uncheckedColor = Color(0xFF4A4A4A)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Remind",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF313131)
                            )
                        }
                        
                        BasicTextField(
                            value = reminderText,
                            onValueChange = { reminderText = it },
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = Color(0xFF313131),
                                fontWeight = FontWeight.Normal
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 52.dp, top = 2.dp, bottom = 12.dp),
                            cursorBrush = SolidColor(Color(0xFF313131)),
                            decorationBox = { innerTextField ->
                                if (reminderText.isEmpty()) {
                                    Text(
                                        text = "Call soon",
                                        color = Color(0xFFAAAAAA),
                                        fontSize = 15.sp
                                    )
                                }
                                innerTextField()
                            }
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 52.dp, bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CallResultOutlinedLabelField(
                                label = "Date", 
                                value = reminderDate, 
                                modifier = Modifier.weight(1f).clickable { showDatePicker = true }
                            )
                            CallResultOutlinedLabelField(
                                label = "Time", 
                                value = reminderTime, 
                                modifier = Modifier.weight(1f).clickable { showTimePicker = true }
                            )
                        }

                        // Date Picker Dialog
                        if (showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                            reminderDate = sdf.format(Date(millis))
                                        }
                                        showDatePicker = false
                                    }) { Text("OK") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }

                        // Time Picker Dialog
                        if (showTimePicker) {
                            AlertDialog(
                                onDismissRequest = { showTimePicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        reminderTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                                        showTimePicker = false
                                    }) { Text("OK") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                                },
                                text = {
                                    TimePicker(state = timePickerState)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Actions
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp, bottom = 24.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CANCEL",
                            color = Color(0xFF333333),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                    .clickable { onDismiss() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                onSave(
                                    callNote,
                                    isNewClient,
                                    if (isMadeOrder) orderValue.ifBlank { "Linen A-Line Dress" } else null,
                                    if (isRemind) reminderText.ifBlank { "Call soon" } else null,
                                    if (isRemind) reminderDate else null,
                                    if (isRemind) reminderTime else null
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF313131)),
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("SAVE", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun CallResultOutlinedLabelField(label: String, value: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .border(1.dp, Color(0xFFBBBBBB), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Text(value, fontSize = 16.sp, color = Color(0xFF888888))
        }
        Box(
            modifier = Modifier
                .offset(x = 12.dp)
                .background(Color.White)
                .padding(horizontal = 4.dp)
        ) {
            Text(label, fontSize = 12.sp, color = Color(0xFF999999))
        }
    }
}
