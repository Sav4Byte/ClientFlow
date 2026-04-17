package com.coffeecodedevs.clientflow.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.res.stringResource
import com.coffeecodedevs.clientflow.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallResultDialog(
    contact: Contact,
    onDismiss: () -> Unit,
    onSave: (note: String, isNewClient: Boolean, orderValue: String?, reminderText: String?, reminderDate: String?, reminderTime: String?) -> Unit
) {
    var callNote by remember { mutableStateOf<String>("") }
    
    var isNewClient by remember { mutableStateOf<Boolean>(false) }
    
    var isMadeOrder by remember { mutableStateOf<Boolean>(false) }
    var orderValue by remember { mutableStateOf<String>("") }

    val now = remember { java.time.LocalDateTime.now() }
    val defaultDate = remember(now) { 
        now.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) 
    }
    val defaultTime = remember(now) { 
        now.plusHours(1).format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")) 
    }

    var isRemind by remember(contact.id) { mutableStateOf<Boolean>(false) }
    var reminderText by remember(contact.id) { mutableStateOf<String>("") }
    var reminderDate by remember(contact.id, defaultDate) { mutableStateOf<String>(defaultDate) }
    var reminderTime by remember(contact.id, defaultTime) { mutableStateOf<String>(defaultTime) }

    var showDatePicker by remember { mutableStateOf<Boolean>(false) }
    var showTimePicker by remember { mutableStateOf<Boolean>(false) }
    // No longer need pickingFor as it's only for Remind now

    val defaultOrder = stringResource(R.string.sample_order_item)
    val defaultRemind = stringResource(R.string.call_soon_placeholder)
    
    val talkHowHeader = stringResource(R.string.talk_how_header)
    val talkPlaceholder = stringResource(R.string.talk_placeholder)
    val newClientCheckbox = stringResource(R.string.new_client_checkbox)
    val madeOrderCheckbox = stringResource(R.string.made_order_checkbox)
    val remindCheckbox = stringResource(R.string.remind_checkbox)
    val dateLabel = stringResource(R.string.date_label)
    val timeLabel = stringResource(R.string.time_label)
    val okButton = stringResource(R.string.ok_button)
    val cancelButton = stringResource(R.string.cancel_button)
    val cancelBtnCaps = stringResource(R.string.cancel_button_caps)
    val saveBtn = stringResource(R.string.save_button)

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = now.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
    )
    val timePickerState = rememberTimePickerState(
        initialHour = now.plusHours(1).hour,
        initialMinute = now.minute
    )
    val interactionSource = remember { MutableInteractionSource() }

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
                            text = talkHowHeader,
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
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            decorationBox = { innerTextField ->
                                if (callNote.isEmpty()) {
                                    Text(
                                        text = talkPlaceholder,
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
                            modifier = Modifier.padding(vertical = 4.dp).clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { isNewClient = !isNewClient }
                        ) {
                            RoundedSquareCheckbox(
                                checked = isNewClient,
                                onCheckedChange = { isNewClient = it }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.new_client_checkbox),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF313131)
                            )
                        }

                        // Made an order Checkbox
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp).clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { isMadeOrder = !isMadeOrder }
                        ) {
                            RoundedSquareCheckbox(
                                checked = isMadeOrder,
                                onCheckedChange = { isMadeOrder = it }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.made_order_checkbox),
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
                                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                                decorationBox = { innerTextField ->
                                    if (orderValue.isEmpty()) {
                                        Text(
                                            text = defaultOrder,
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
                            modifier = Modifier.padding(vertical = 4.dp).clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { isRemind = !isRemind }
                        ) {
                            RoundedSquareCheckbox(
                                checked = isRemind,
                                onCheckedChange = { isRemind = it }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = remindCheckbox,
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
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            decorationBox = { innerTextField ->
                                if (reminderText.isEmpty()) {
                                    Text(
                                        text = defaultRemind,
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
                                label = dateLabel, 
                                value = reminderDate, 
                                modifier = Modifier.width(130.dp).clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) { showDatePicker = true }
                            )
                            CallResultOutlinedLabelField(
                                label = timeLabel, 
                                value = reminderTime, 
                                modifier = Modifier.width(100.dp).clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) { showTimePicker = true }
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
                                    }) { Text(okButton) }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) { Text(cancelButton) }
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
                                    }) { Text(okButton) }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showTimePicker = false }) { Text(cancelButton) }
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
                            text = stringResource(R.string.cancel_button_caps),
                            color = Color(0xFF333333),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                    .clickable(
                                        interactionSource = interactionSource,
                                        indication = null
                                    ) { onDismiss() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                onSave(
                                    callNote,
                                    isNewClient,
                                    if (isMadeOrder) orderValue.ifBlank { defaultOrder } else null,
                                    if (isRemind) reminderText.ifBlank { defaultRemind } else null,
                                    if (isRemind) reminderDate else null,
                                    if (isRemind) reminderTime else null
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF313131)),
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(stringResource(R.string.save_button), fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.White)
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun RoundedSquareCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White)
            .border(1.5.dp, Color(0xFF313131), RoundedCornerShape(4.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF313131),
                modifier = Modifier.size(18.dp)
            )
        }
    }
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
