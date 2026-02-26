package com.coffeecodedevs.clientflow.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ContactActions {
    fun callContact(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not initiate call", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSms(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$phoneNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not send SMS", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareContact(context: Context, name: String, phones: List<String>, note: String?) {
        try {
            val shareText = buildString {
                append("Contact: $name\n")
                if (phones.isNotEmpty()) {
                    append("Phones: ${phones.joinToString(", ")}\n")
                }
                if (!note.isNullOrBlank()) {
                    append("Note: $note")
                }
            }
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Share Contact via"))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not share contact", Toast.LENGTH_SHORT).show()
        }
    }
}
