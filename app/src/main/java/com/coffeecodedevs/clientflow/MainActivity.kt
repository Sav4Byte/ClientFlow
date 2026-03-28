package com.coffeecodedevs.clientflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.unit.sp
import com.coffeecodedevs.clientflow.ui.screens.*
import com.coffeecodedevs.clientflow.ui.theme.ClientFlowTheme
import com.coffeecodedevs.clientflow.data.ContactViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClientFlowTheme {
                AppNavigation()
            }
        }
    }
}

sealed class Screen {
    object Contacts : Screen()
    object Notes : Screen()
    object Goals : Screen()
    data class GoalsDetail(val note: NoteItem) : Screen()
    object Calendar : Screen()
    data class ContactDetail(val contact: com.coffeecodedevs.clientflow.data.Contact, val showActivity: Boolean) : Screen()
}



@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val viewModel: ContactViewModel = viewModel()
    val screenStack = remember { mutableStateListOf<Screen>(Screen.Contacts) }
    val currentScreen = screenStack.last()

    
    var showCreateDialog by remember { mutableStateOf(false) }
    var contactToEdit by remember { mutableStateOf<com.coffeecodedevs.clientflow.data.Contact?>(null) }
    var activeContactTab by remember { mutableStateOf("CLIENT") }
    
    var callInProgressContact by remember { mutableStateOf<com.coffeecodedevs.clientflow.data.Contact?>(null) }
    var showCallResultDialog by remember { mutableStateOf(false) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            // Trigger when app comes back to the foreground
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                if (callInProgressContact != null) {
                    // Show the dialog anytime they return back after starting a call
                    showCallResultDialog = true
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    val selectedBottomTab = when (currentScreen) {
        is Screen.Contacts -> 0
        is Screen.Notes -> 1
        is Screen.Goals, is Screen.GoalsDetail -> 2
        is Screen.Calendar -> 3
        else -> 0
    }

    val allContactsFromDb by viewModel.allContacts.collectAsState(initial = emptyList())
    val allNotesFromDb by viewModel.allNotes.collectAsState(initial = emptyList())
    // Мапим контакты-заметки из БД в формат NoteItem для экрана заметок
    val dbNotes = allNotesFromDb.map { contact ->
        val fullText = contact.contact ?: ""
        val lines = fullText.split("\n\n")
        val preview = if (lines.size >= 2) lines.take(2).joinToString("\n\n") else fullText.take(150)
        
        NoteItem(
            id = contact.id,
            title = if (contact.noteTitle.isNotBlank()) contact.noteTitle else "New Note",
            description = preview,
            fullDescription = fullText,
            isGoalsNote = true
        )
    }

    androidx.activity.compose.BackHandler(enabled = screenStack.size > 1) {
        screenStack.removeAt(screenStack.size - 1)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Экраны
        when (currentScreen) {
            is Screen.Contacts -> ContactsScreen(
                onContactClick = { contact, full ->
                    screenStack.add(Screen.ContactDetail(contact, full))
                },
                onCallInitiated = { contact ->
                    callInProgressContact = contact
                },
                onCreateClick = { tab ->
                    activeContactTab = tab
                    showCreateDialog = true
                },
                onTabChange = { tab ->
                    activeContactTab = tab
                }
            )
            is Screen.Notes -> NotesScreen(
                notes = dbNotes,
                onBackClick = { 
                    if (screenStack.size > 1) screenStack.removeAt(screenStack.size - 1)
                    else {
                        screenStack.clear()
                        screenStack.add(Screen.Contacts)
                    }
                },
                onGoalsClick = { note ->
                    screenStack.add(Screen.GoalsDetail(note))
                }
            )
            is Screen.GoalsDetail -> {
                val contactFromDb = allContactsFromDb.find { it.id == currentScreen.note.id }
                    ?: allNotesFromDb.find { it.id == currentScreen.note.id }
                
                val displayNote = dbNotes.find { it.id == currentScreen.note.id } 
                    ?: NoteItem(
                        id = contactFromDb?.id ?: currentScreen.note.id,
                        title = contactFromDb?.let { if (it.orderName.isNotBlank() && !it.isStandaloneNote) it.orderName else it.noteTitle } ?: currentScreen.note.title,
                        description = contactFromDb?.contact ?: currentScreen.note.description,
                        fullDescription = contactFromDb?.contact ?: currentScreen.note.fullDescription,
                        isGoalsNote = true
                    )
                
                GoalsScreen(
                    noteTitle = displayNote.title,
                    noteDescription = displayNote.fullDescription ?: displayNote.description,
                    customerName = contactFromDb?.customerName?.ifBlank { "${contactFromDb.firstName} ${contactFromDb.lastName}".trim() },
                    orderAddress = contactFromDb?.orderAddress,
                    orderTime = if (contactFromDb?.reminderTime?.isNotBlank() == true) contactFromDb.reminderTime else null,
                    isOrder = contactFromDb != null && contactFromDb.orderName.isNotBlank() && !contactFromDb.isStandaloneNote,
                    onBackClick = { screenStack.removeAt(screenStack.size - 1) },
                    onDeleteClick = {
                        if (contactFromDb != null) {
                            viewModel.deleteContact(contactFromDb)
                        }
                        screenStack.removeAt(screenStack.size - 1)
                    },
                    onShareClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, displayNote.title)
                            putExtra(Intent.EXTRA_TEXT, "${displayNote.title}\n\n${displayNote.fullDescription ?: displayNote.description}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share note via"))
                    },
                    onUpdateClick = { newTitle, newFullDescription ->
                        val contactToUpdate = allContactsFromDb.find { it.id == displayNote.id }
                            ?: allNotesFromDb.find { it.id == displayNote.id }
                        if (contactToUpdate != null) {
                            val updatedContact = if (contactToUpdate.orderName.isNotBlank() && !contactToUpdate.isStandaloneNote) {
                                contactToUpdate.copy(
                                    orderName = newTitle,
                                    contact = newFullDescription
                                )
                            } else {
                                contactToUpdate.copy(
                                    noteTitle = newTitle,
                                    contact = newFullDescription
                                )
                            }
                            viewModel.updateContact(updatedContact)
                        }
                    }
                )
            }
            is Screen.Goals -> GoalsScreen(
                onBackClick = { 
                    if (screenStack.size > 1) screenStack.removeAt(screenStack.size - 1)
                    else {
                        screenStack.clear()
                        screenStack.add(Screen.Contacts)
                    }
                }
            )
            is Screen.Calendar -> CalendarScreen(
                initialTab = if (activeContactTab in listOf("ALL", "ORDERS", "REMINDER")) activeContactTab else "REMINDER",
                contacts = allContactsFromDb,
                onTabChange = { tab ->
                    activeContactTab = tab
                },
                onOrderClick = { order ->
                    screenStack.add(Screen.GoalsDetail(
                        NoteItem(
                            id = order.id,
                            title = order.title,
                            description = order.description,
                            fullDescription = order.description,
                            isGoalsNote = true
                        )
                    ))
                },
                onNavigate = { index ->
                    val nextScreen = when(index) {
                        0 -> Screen.Contacts
                        1 -> Screen.Notes
                        2 -> Screen.Goals
                        3 -> Screen.Calendar
                        else -> Screen.Contacts
                    }
                    screenStack.clear()
                    screenStack.add(nextScreen)
                }
            )
            is Screen.ContactDetail -> {
                val displayContact = allContactsFromDb.find { it.id == currentScreen.contact.id } ?: currentScreen.contact
                ContactDetailScreen(
                    contactName = "${displayContact.firstName} ${displayContact.lastName}",
                    contactNote = displayContact.contact,
                    company = displayContact.company,
                    phoneNumbers = displayContact.phones,
                    callLog = displayContact.callLog,
                    showActivity = currentScreen.showActivity,
                    onBackClick = { screenStack.removeAt(screenStack.size - 1) },
                    onCallClick = {
                        viewModel.logCall(displayContact)
                        callInProgressContact = displayContact
                    },
                    onEditClick = {
                        contactToEdit = displayContact
                    },
                    onDeleteClick = {
                        viewModel.deleteContact(displayContact)
                        screenStack.removeAt(screenStack.size - 1)
                    }
                )
            }


        }
        
        // Нижняя панель навигации
        if (currentScreen !is Screen.ContactDetail && currentScreen !is Screen.GoalsDetail) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .width(280.dp)
                    .padding(bottom = 65.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = BottomBarShape(cutoutRadiusDp = 36.dp),
                    color = Color(0xFF313131),
                    shadowElevation = 12.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomNavIcon(
                            painter = painterResource(R.drawable.contact),
                            selected = selectedBottomTab == 0,
                            onClick = {
                                screenStack.clear()
                                screenStack.add(Screen.Contacts)
                            }
                        )
                        BottomNavIcon(
                            painter = painterResource(R.drawable.notes),
                            selected = selectedBottomTab == 1,
                            onClick = {
                                screenStack.clear()
                                screenStack.add(Screen.Notes)
                            }
                        )
                        Spacer(modifier = Modifier.width(56.dp))
                        BottomNavIcon(
                            painter = painterResource(R.drawable.dial),
                            selected = selectedBottomTab == 2,
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL)
                                context.startActivity(intent)
                            }
                        )
                        BottomNavIcon(
                            painter = painterResource(R.drawable.calendar),
                            selected = selectedBottomTab == 3,
                            onClick = {
                                screenStack.clear()
                                screenStack.add(Screen.Calendar)
                            }
                        )
                    }
                }
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.align(Alignment.TopCenter).offset(y = (-28).dp).size(56.dp),
                    containerColor = Color(0xFF313131),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
        
        // Create Dialog
        if (showCreateDialog || contactToEdit != null) {
            CreateContactDialog(
                initialTab = when {
                    contactToEdit?.isStandaloneNote == true -> "NOTE"
                    contactToEdit != null -> "CONTACT"
                    currentScreen is Screen.Notes -> "NOTE"
                    currentScreen is Screen.Calendar && activeContactTab == "ORDERS" -> "ORDER"
                    currentScreen is Screen.Calendar && activeContactTab == "REMINDER" -> "REMINDER"
                    currentScreen is Screen.Calendar -> "REMINDER"
                    else -> "CONTACT"
                },
                editingContact = contactToEdit,
                onDismiss = { 
                    showCreateDialog = false
                    contactToEdit = null
                },
                onSave = { contact ->
                    val isNote = (currentScreen is Screen.Notes) || contact.isStandaloneNote
                    
                    if (contactToEdit != null) {
                        // При редактировании сохраняем исходные флаги isClient/isEmployee
                        viewModel.updateContact(contact.copy(
                            isClient = contactToEdit!!.isClient,
                            isEmployee = contactToEdit!!.isEmployee
                        ))
                    } else {
                        // При создании нового контакта учитываем активную вкладку
                        val updatedContact = if (!isNote && currentScreen is Screen.Contacts) {
                            contact.copy(
                                isClient = activeContactTab == "CLIENT",
                                isEmployee = activeContactTab == "EMPLOYEE",
                                isStandaloneNote = false,
                                firstName = if (contact.firstName.isBlank()) "New Contact" else contact.firstName
                            )
                        } else {
                            contact.copy(
                                isStandaloneNote = isNote,
                                firstName = if (contact.firstName.isBlank()) "Note" else contact.firstName
                            )
                        }
                        viewModel.addContact(updatedContact)
                    }
                    
                    showCreateDialog = false
                    contactToEdit = null
                }
            )
        }

        // Call Result Dialog
        if (showCallResultDialog && callInProgressContact != null) {
            CallResultDialog(
                contact = callInProgressContact!!,
                onDismiss = {
                    showCallResultDialog = false
                    callInProgressContact = null
                },
                onSave = { note, isNewClient, orderValue, reminderText, reminderDate, reminderTime ->
                    val contactToUpdate = allContactsFromDb.find { it.id == callInProgressContact!!.id } ?: callInProgressContact!!
                    var updatedContact = contactToUpdate

                    if (note.isNotBlank()) {
                        val newNoteWithSpace = if (updatedContact.contact.isNullOrBlank()) note else "${updatedContact.contact}\n\n$note"
                        updatedContact = updatedContact.copy(contact = newNoteWithSpace)
                    }
                    if (isNewClient) {
                        updatedContact = updatedContact.copy(isClient = true, isEmployee = false)
                    }
                    if (orderValue != null) {
                        updatedContact = updatedContact.copy(orderName = orderValue)
                    }
                    if (reminderText != null) {
                        updatedContact = updatedContact.copy(
                            reminderText = reminderText,
                            reminderDate = reminderDate ?: updatedContact.reminderDate,
                            reminderTime = reminderTime ?: updatedContact.reminderTime
                        )
                    }

                    viewModel.updateContact(updatedContact)

                    showCallResultDialog = false
                    callInProgressContact = null
                }
            )
        }
    }
}


@Composable
private fun BottomNavIcon(
    painter: androidx.compose.ui.graphics.painter.Painter,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (selected) Color(0x80AEE0FF) else Color.Transparent)
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

private class BottomBarShape(private val cutoutRadiusDp: androidx.compose.ui.unit.Dp) : Shape {
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

@Preview(showBackground = true)
@Composable
fun GoalsScreenPreview() {
    ClientFlowTheme {
        ContactsScreen()
    }
}



