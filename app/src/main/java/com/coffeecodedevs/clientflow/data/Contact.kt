package com.coffeecodedevs.clientflow.data

data class Contact(
    val id: Int,
    val name: String,
    val type: ContactType,
    val note: String? = null
)

enum class ContactType {
    CLIENT,
    EMPLOYEE
}
