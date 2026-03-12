package com.coffeecodedevs.clientflow.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.res.stringResource
import com.coffeecodedevs.clientflow.R
import com.coffeecodedevs.clientflow.data.Contact
import java.util.*
import java.text.SimpleDateFormat


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContactDialog(
    initialTab: String = "CONTACT",
    editingContact: Contact? = null,
    onDismiss: () -> Unit,
    onSave: (Contact) -> Unit
) {
    var selectedTab by remember { 
        mutableStateOf(if (editingContact?.isStandaloneNote == true) "NOTE" else initialTab) 
    }
    
    // Contact fields
    var firstName by remember { mutableStateOf<String>(editingContact?.firstName ?: "") }
    var lastName by remember { mutableStateOf<String>(editingContact?.lastName ?: "") }
    var company by remember { mutableStateOf<String>(editingContact?.company ?: "") }
    var phones by remember { mutableStateOf<List<String>>(editingContact?.phones ?: listOf("+380")) }
    
    // Order fields
    var orderTitle by remember { mutableStateOf<String>(editingContact?.orderName ?: "") }
    var orderCustomer by remember { mutableStateOf<String>(editingContact?.customerName ?: "") }
    var orderAddress by remember { mutableStateOf<String>(editingContact?.orderAddress ?: "") }
    
    var description by remember { mutableStateOf<String>(editingContact?.contact ?: "") }
    
    // Note fields
    var noteTitle by remember { mutableStateOf<String>(editingContact?.noteTitle ?: "") }
    
    // Reminder fields
    var reminderText by remember { mutableStateOf<String>(editingContact?.reminderText ?: "") }
    var reminderDate by remember { mutableStateOf<String>(editingContact?.reminderDate ?: "07.10.2026") }
    var reminderTime by remember { mutableStateOf<String>(editingContact?.reminderTime ?: "00:00") }
    var repeatEnabled by remember { mutableStateOf(false) }
    var selectedDays by remember { mutableStateOf(emptySet<String>()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var isEmployee by remember { mutableStateOf(editingContact?.isEmployee ?: false) }
    
    // Resource strings at the top
    val defaultContactName = stringResource(R.string.new_contact_default)
    val mo = stringResource(R.string.day_mo)
    val tu = stringResource(R.string.day_tu)
    val we = stringResource(R.string.day_we)
    val th = stringResource(R.string.day_th)
    val fr = stringResource(R.string.day_fr)
    val sa = stringResource(R.string.day_sa)
    val su = stringResource(R.string.day_su)
    val localizedDays = remember(mo, tu, we, th, fr, sa, su) {
        listOf(mo, tu, we, th, fr, sa, su)
    }

    val createHeader = stringResource(R.string.create_header)
    val contactTab = stringResource(R.string.contact_tab)
    val orderTabResource = stringResource(R.string.order_tab)
    val reminderTab = stringResource(R.string.reminder_tab)
    val noteTab = stringResource(R.string.note_tab)
    val namePlaceholder = stringResource(R.string.name_placeholder)
    val surnamePlaceholder = stringResource(R.string.surname_placeholder)
    val companyPlaceholder = stringResource(R.string.company_placeholder)
    val descriptionPlaceholder = stringResource(R.string.description_placeholder)
    val textPlaceholderResource = stringResource(R.string.text_placeholder)
    val dateLabel = stringResource(R.string.date_label)
    val timeLabel = stringResource(R.string.time_label)
    val okButton = stringResource(R.string.ok_button)
    val cancelButton = stringResource(R.string.cancel_button)
    val repeatLabel = stringResource(R.string.repeat_label)
    val titlePlaceholder = stringResource(R.string.title_placeholder)
    val customerPlaceholder = stringResource(R.string.customer_placeholder)
    val addressPlaceholder = stringResource(R.string.address_placeholder)
    val cancelBtnCaps = stringResource(R.string.cancel_button_caps)
    val saveBtn = stringResource(R.string.save_button)
    val removeDesc = stringResource(R.string.remove_desc)
    val addDesc = stringResource(R.string.add_desc)
    
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
                        .height(if (selectedTab == "CONTACT") 600.dp else 450.dp)
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(Color(0xFFAEDEF4))
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = createHeader,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                    }


                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // 1. Tabs first
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 0.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TabPill(contactTab, Color(0xFFAEDEF4), Color(0xFF334D6F), selectedTab == "CONTACT") { selectedTab = "CONTACT" }
                            TabPill(orderTabResource, Color(0xFFE5CCFF), Color(0xFF6C5A9B), selectedTab == "ORDER") { selectedTab = "ORDER" }
                            TabPill(reminderTab, Color(0xFFFDECDA), Color(0xFFD5A665), selectedTab == "REMINDER") { selectedTab = "REMINDER" }
                            TabPill(noteTab, Color(0xFF9BE5D6), Color(0xFF1ABBA8), selectedTab == "NOTE") { selectedTab = "NOTE" }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))

                        if (selectedTab == "CONTACT") {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                 verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    GrayFrameTextField(value = firstName, onValueChange = { firstName = it }, placeholder = namePlaceholder)
                                    GrayFrameTextField(value = lastName, onValueChange = { lastName = it }, placeholder = surnamePlaceholder)
                                }

                                GrayFrameTextField(value = company, onValueChange = { company = it }, placeholder = companyPlaceholder)

                                // Phone field
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    phones.forEachIndexed { index, phone ->
                                        PhoneFieldWithPlus(
                                            value = phone,
                                            onValueChange = { phones = phones.toMutableList().apply { set(index, it) } },
                                            onRemove = { if (phones.size > 1) phones = phones.toMutableList().apply { removeAt(index) } },
                                            onAdd = { phones = phones + "+380" },
                                            showPlus = (index == phones.lastIndex),
                                            removeDesc = removeDesc,
                                            addDesc = addDesc
                                        )
                                    }
                                }

                                GrayFrameTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    placeholder = descriptionPlaceholder,
                                    modifier = Modifier.height(80.dp),
                                    singleLine = false,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        } else if (selectedTab == "REMINDER") {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                SimpleUnderlineTextField(value = reminderText, onValueChange = { reminderText = it }, placeholder = textPlaceholderResource)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    OutlinedLabelField(
                                        label = dateLabel, 
                                        value = reminderDate, 
                                        modifier = Modifier.weight(0.9f).clickable { showDatePicker = true }
                                    )
                                    OutlinedLabelField(
                                        label = timeLabel, 
                                        value = reminderTime, 
                                        modifier = Modifier.weight(0.6f).clickable { showTimePicker = true }
                                    )
                                    Spacer(modifier = Modifier.weight(0.7f))
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

                                // Time Picker Dialog (Simple custom version)
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

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { repeatEnabled = !repeatEnabled }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .border(2.dp, if (repeatEnabled) Color(0xFF313131) else Color(0xFF999999), RoundedCornerShape(4.dp))
                                            .background(if (repeatEnabled) Color(0xFF313131) else Color.Transparent, RoundedCornerShape(4.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (repeatEnabled) {
                                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(repeatLabel, fontSize = 18.sp, color = Color(0xFF313131))
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    localizedDays.forEach { day ->
                                        DayCircle(
                                            text = day,
                                            isSelected = selectedDays.contains(day),
                                            onClick = {
                                                selectedDays = if (selectedDays.contains(day)) {
                                                    selectedDays - day
                                                } else {
                                                    selectedDays + day
                                                }
                                            }
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFE8CC))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(reminderTime.replace(":", "."), fontSize = 13.sp, color = Color(0xFF7A6B53), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else if (selectedTab == "ORDER") {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                SimpleUnderlineTextField(value = orderTitle, onValueChange = { orderTitle = it }, placeholder = titlePlaceholder)
                                
                                SimpleUnderlineTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    placeholder = descriptionPlaceholder,
                                    modifier = Modifier.height(60.dp),
                                    singleLine = false,
                                    showUnderline = false,
                                    fontWeight = FontWeight.Normal
                                )
                                
                                SimpleUnderlineTextField(value = orderCustomer, onValueChange = { orderCustomer = it }, placeholder = customerPlaceholder, fontWeight = FontWeight.Normal)
                                SimpleUnderlineTextField(value = orderAddress, onValueChange = { orderAddress = it }, placeholder = addressPlaceholder, fontWeight = FontWeight.Normal)
                            }
                        } else if (selectedTab == "NOTE") {
                            Column(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                SimpleUnderlineTextField(value = noteTitle, onValueChange = { noteTitle = it }, placeholder = titlePlaceholder)
                                
                                SimpleUnderlineTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    placeholder = descriptionPlaceholder,
                                    modifier = Modifier.height(60.dp),
                                    singleLine = false,
                                    showUnderline = false,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Actions (Always visible)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, bottom = 24.dp, top = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cancelBtnCaps,
                            color = Color(0xFF333333),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier
                                    .clickable { onDismiss() }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = {
                                if (firstName.isNotBlank() || lastName.isNotBlank() || 
                                    orderTitle.isNotBlank() || noteTitle.isNotBlank() || reminderText.isNotBlank()) {
                                    
                                    onSave(
                                        Contact(
                                            id = editingContact?.id ?: 0,
                                            firstName = when {
                                                firstName.isNotBlank() -> firstName
                                                selectedTab == "ORDER" && orderCustomer.isNotBlank() -> orderCustomer
                                                selectedTab == "ORDER" -> orderTitle
                                                selectedTab == "NOTE" -> noteTitle
                                                selectedTab == "REMINDER" -> reminderText
                                                 else -> defaultContactName
                                            },
                                            lastName = lastName,
                                            company = company,
                                            phones = phones,
                                            isClient = selectedTab != "REMINDER" && selectedTab != "NOTE" && selectedTab != "ORDER",
                                            isEmployee = isEmployee,
                                            contact = if (description.isNotBlank()) description else null,
                                            orderName = orderTitle,
                                            customerName = orderCustomer,
                                            orderAddress = orderAddress,
                                            noteTitle = noteTitle,
                                            reminderText = reminderText,
                                            reminderDate = reminderDate,
                                            reminderTime = reminderTime,
                                            isStandaloneNote = editingContact?.isStandaloneNote ?: (initialTab == "NOTE")
                                        )
                                    )
                                } else {
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF313131)),
                            shape = CircleShape,
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(saveBtn, fontSize = 14.sp, fontWeight = FontWeight.Normal, color = Color.White)
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun TabPill(text: String, bgColor: Color, textColor: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .background(if (selected) bgColor else bgColor.copy(alpha = 0.35f))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) textColor else textColor.copy(alpha = 0.6f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}


@Composable
private fun PhoneFieldWithPlus(
    value: String,
    onValueChange: (String) -> Unit,
    onRemove: () -> Unit,
    onAdd: () -> Unit,
    showPlus: Boolean,
    removeDesc: String,
    addDesc: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (showPlus) 20.dp else 0.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .then(
                    if (showPlus) Modifier.clip(PhoneFieldShape())
                    else Modifier.clip(RoundedCornerShape(12.dp))
                )
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = Color(0xFFEEEEEE),
                    shape = if (showPlus) PhoneFieldShape() else RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    modifier = Modifier.weight(1f),
                    cursorBrush = SolidColor(Color(0xFF313131))
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = removeDesc,
                    tint = Color(0xFF333333),
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onRemove() }
                )
            }
        }

        if (showPlus) {
            Box(
                modifier = Modifier
                    .offset(y = 20.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF313131))
                    .clickable { onAdd() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = addDesc,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun SimpleUnderlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    showUnderline: Boolean = true,
    fontWeight: FontWeight = FontWeight.Bold
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 4.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = Color(0xFFAAAAAA),
                    fontSize = 17.sp,
                    fontWeight = fontWeight
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 17.sp,
                    color = Color(0xFF313131),
                    fontWeight = fontWeight
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = singleLine,
                cursorBrush = SolidColor(Color(0xFF313131))
            )
        }
        if (showUnderline) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFDDDDDD))
            )
        }
    }
}

@Composable
private fun GrayFrameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (singleLine) 48.dp else 100.dp)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = if (singleLine) Arrangement.Center else Arrangement.Top
        ) {
            if (!singleLine) Spacer(modifier = Modifier.height(12.dp))
            Box(contentAlignment = Alignment.CenterStart) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFFAAAAAA),
                        fontSize = 16.sp,
                        fontWeight = fontWeight
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFF313131),
                        fontWeight = fontWeight
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = singleLine,
                    cursorBrush = SolidColor(Color(0xFF313131))
                )
            }
        }
    }
}


@Composable
private fun OutlinedLabelField(label: String, value: String, modifier: Modifier = Modifier) {
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

@Composable
private fun DayCircle(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFFFFE8CC) else Color.Transparent)
            .border(1.dp, Color(0xFFFFE8CC), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text, 
            fontSize = 12.sp, 
            color = if (isSelected) Color(0xFF7A6B53) else Color(0xFF999999),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

class PhoneFieldShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path()
        val r = with(density) { 26.dp.toPx() }
        val c = with(density) { 12.dp.toPx() }
        val cx = size.width / 2f
        val h = size.height
        val w = size.width


        
        path.moveTo(c, 0f)
        path.lineTo(w - c, 0f)
        path.quadraticBezierTo(w, 0f, w, c)
        path.lineTo(w, h - c)
        path.quadraticBezierTo(w, h, w - c, h)
        
        // Bottom edge with cutout
        path.lineTo(cx + r + 10f, h)
        path.cubicTo(
            cx + r, h,
            cx + r, h - r,
            cx, h - r
        )
        path.cubicTo(
            cx - r, h - r,
            cx - r, h,
            cx - r - 10f, h
        )
        
        path.lineTo(c, h)
        path.quadraticBezierTo(0f, h, 0f, h - c)
        path.lineTo(0f, c)
        path.quadraticBezierTo(0f, 0f, c, 0f)
        path.close()
        
        return Outline.Generic(path)
    }
}



