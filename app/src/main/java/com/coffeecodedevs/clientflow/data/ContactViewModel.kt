package com.coffeecodedevs.clientflow.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    private val contactDao = AppDatabase.getDatabase(application).contactDao()
    val allContacts: Flow<List<Contact>> = contactDao.getAllContacts()

    fun addContact(contact: Contact) {
        viewModelScope.launch {
            contactDao.insertContact(contact)
        }
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch {
            contactDao.deleteContact(contact)
        }
    }
}
