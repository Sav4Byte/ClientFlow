package com.coffeecodedevs.clientflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.coffeecodedevs.clientflow.R
import com.coffeecodedevs.clientflow.data.Contact
import com.coffeecodedevs.clientflow.utils.ContactActions

@Composable
fun PhoneSelectionDialog(
    contact: Contact,
    onDismiss: () -> Unit,
    onCallInitiated: (Contact) -> Unit = {},
    context: android.content.Context = androidx.compose.ui.platform.LocalContext.current
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color(0xFFF8F9FA)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "${contact.firstName} ${contact.lastName}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF334D6F),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Select number and action",
                    fontSize = 14.sp,
                    color = Color(0xFF8B9BA8),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                contact.phones.forEach { phone ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = phone,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C4A5E),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    ContactActions.callContact(context, phone)
                                    onCallInitiated(contact)
                                    onDismiss()
                                }
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            DialogActionButton(
                                painter = painterResource(R.drawable.phone),
                                iconSize = 22.dp,
                                onClick = {
                                    ContactActions.callContact(context, phone)
                                    onCallInitiated(contact)
                                    onDismiss()
                                }
                            )
                            DialogActionButton(
                                painter = painterResource(R.drawable.sms),
                                iconSize = 22.dp,
                                onClick = {
                                    ContactActions.sendSms(context, phone)
                                    onDismiss()
                                }
                            )
                            DialogActionButton(
                                painter = painterResource(R.drawable.share),
                                iconSize = 18.dp,
                                onClick = {
                                    ContactActions.shareContact(context, "${contact.firstName} ${contact.lastName}", listOf(phone), contact.contact)
                                    onDismiss()
                                }
                            )
                        }
                    }
                    
                    if (contact.phones.last() != phone) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFE9ECEF))
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "CANCEL",
                    color = Color(0xFF334D6F),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onDismiss() }
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun DialogActionButton(
    painter: androidx.compose.ui.graphics.painter.Painter,
    onClick: () -> Unit,
    iconSize: androidx.compose.ui.unit.Dp = 22.dp
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFF313131))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}
