package com.coffeecodedevs.clientflow.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val contactDao = AppDatabase.getDatabase(application).contactDao()
    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts()
    val allNotes: Flow<List<Contact>> = contactDao.getAllNotes()

    fun addContact(contact: Contact) {
        viewModelScope.launch {
            contactDao.insertContact(contact)
        }
    }

    fun updateContact(contact: Contact) {
        viewModelScope.launch {
            contactDao.updateContact(contact)
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            contactDao.deleteContact(contact)
        }
    }

    fun logCall(contact: Contact) {
        viewModelScope.launch {
            val now = Date()
            // Format: "Sep. 12, 17:56" — same style as in the screenshots
            val dateFormatter = SimpleDateFormat("MMM. d,", Locale.ENGLISH)
            val timeFormatter = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val timestamp = "${dateFormatter.format(now)} ${timeFormatter.format(now)}"
            val updatedLog = contact.callLog + timestamp
            contactDao.updateContact(contact.copy(callLog = updatedLog))
        }
    }
}
