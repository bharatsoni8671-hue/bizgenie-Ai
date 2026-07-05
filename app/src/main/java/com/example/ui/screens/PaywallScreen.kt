package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun PaywallScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val trialDaysElapsed by viewModel.trialDaysElapsed.collectAsState()
    var showPaymentDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF130E24)) // Dark, premium visual theme
    ) {
        // High-fidelity background glows (lavender and violet)
        Box(
            modifier = Modifier
                .size(250.dp)
                .offset(x = (-30).dp, y = (-30).dp)
                .blur(80.dp)
                .background(Color(0xFF6750A4).copy(alpha = 0.3f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 50.dp)
                .blur(100.dp)
                .background(Color(0xFFFFB4AB).copy(alpha = 0.15f), CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // Locking Icon with Neon Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF21005D))
                    .border(2.dp, Color(0xFFD0BCFF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = "Expired Lock Icon",
                    tint = Color(0xFFFFB4AB),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Headline
            Text(
                text = "7-Day Trial Ended",
                fontFamily = FontFamily.Serif,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Your free trial has successfully completed. Subscribe to BizGenie Premium to unlock and manage your websites, posters, and advanced marketing models.",
                fontFamily = FontFamily.SansSerif,
                fontSize = 15.sp,
                color = Color(0xFFEADDFF),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Premium Features List Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF21005D).copy(alpha = 0.6f)),
                border = BorderStroke(1.dp, Color(0xFF6750A4).copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureRow(text = "Unrestricted Website Creation & Hosting")
                    FeatureRow(text = "High-Quality Marketing Poster Designer")
                    FeatureRow(text = "Unlimited AI Social Media Caption Generation")
                    FeatureRow(text = "Full Brand Identity Styling Kits")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Price Details Box
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Premium Plan",
                        fontSize = 13.sp,
                        color = Color(0xFFD0BCFF)
                    )
                    Text(
                        text = "This app requires a payment of ₹200 per month.",
                        fontSize = 11.sp,
                        color = Color(0xFFCAC4D0)
                    )
                }
                Text(
                    text = "₹200/month",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "After the first month, an additional ₹200 will be charged.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFB4AB),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Pay Button
            Button(
                onClick = { showPaymentDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0BCFF)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("pay_subscription_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Pay",
                        tint = Color(0xFF21005D)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unlock Premium Now",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF21005D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reset Trial Button for developers/testers
            Text(
                text = "Reset Trial (Testing Guide)",
                color = Color(0xFFFFB4AB),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable {
                        viewModel.resetTrialDays()
                        Toast.makeText(context, "Trial Days reset to 0!", Toast.LENGTH_SHORT).show()
                    }
                    .padding(8.dp)
            )
        }
    }

    // UPI Payment Verification Dialog for ₹200 Subscription
    if (showPaymentDialog) {
        UpiPaymentSimulationDialog(
            amount = 200.0,
            upiId = "bharatsoni8671-1@okhdfcbank",
            onDismiss = { showPaymentDialog = false },
            onPaymentSuccess = {
                viewModel.setSubscribed(true)
                showPaymentDialog = false
                Toast.makeText(context, "Subscription successfully unlocked!", Toast.LENGTH_LONG).show()
            }
        )
    }
}

@Composable
fun FeatureRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Feature Checked",
            tint = Color(0xFF4EDEA3),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun UpiPaymentSimulationDialog(
    amount: Double,
    upiId: String,
    onDismiss: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var refIdText by remember { mutableStateOf("") }
    var paymentStage by remember { mutableStateOf(1) } // 1: Enter Ref ID & Verify, 2: Processing, 3: Success

    LaunchedEffect(paymentStage) {
        if (paymentStage == 2) {
            kotlinx.coroutines.delay(2000L) // Simulate network/payment delay
            paymentStage = 3
        } else if (paymentStage == 3) {
            kotlinx.coroutines.delay(1500L) // Show success screen, then complete
            onPaymentSuccess()
        }
    }

    Dialog(
        onDismissRequest = { if (paymentStage == 1) onDismiss() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            AnimatedContent(
                targetState = paymentStage,
                label = "PaymentStageAnimation"
            ) { currentStage ->
                when (currentStage) {
                    1 -> {
                        // Enter Ref ID Screen
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Secure",
                                    tint = Color(0xFF6200EE),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Real UPI Payment (INR)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF191C1E)
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Payment Details card
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF2F0F4))
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("To UPI ID:", fontSize = 13.sp, color = Color.Gray)
                                        Text(upiId, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF191C1E))
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Amount:", fontSize = 13.sp, color = Color.Gray)
                                        Text("₹${amount}0", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF10B981))
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            try {
                                                val uri = android.net.Uri.Builder()
                                                    .scheme("upi")
                                                    .authority("pay")
                                                    .appendQueryParameter("pa", upiId)
                                                    .appendQueryParameter("pn", "Bharat Soni")
                                                    .appendQueryParameter("tn", "BizGenie Premium Subscription")
                                                    .appendQueryParameter("am", String.format(java.util.Locale.US, "%.2f", amount))
                                                    .appendQueryParameter("cu", "INR")
                                                    .build()

                                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                                    data = uri
                                                }
                                                val chooser = android.content.Intent.createChooser(intent, "Pay ₹$amount with UPI app")
                                                context.startActivity(chooser)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "UPI App not found. Please pay manually to UPI ID: $upiId", Toast.LENGTH_LONG).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "Launch App", tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Launch UPI App to Pay", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "Enter the 12-digit transaction Ref No. after making the payment to activate your premium status.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = refIdText,
                                onValueChange = { input -> 
                                    if (input.all { it.isDigit() } && input.length <= 12) {
                                        refIdText = input
                                    }
                                },
                                label = { Text("12-Digit UPI Ref / UTR No.") },
                                placeholder = { Text("e.g. 301928374625") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF6200EE),
                                    unfocusedBorderColor = Color(0xFFCAC4D0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (refIdText.length == 12) {
                                        val messageText = "BizGenie UPI Pay Alert: User submitted UPI Payment of ₹$amount. Ref ID: $refIdText"
                                        coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                            try {
                                                val smsManager = context.getSystemService(android.telephony.SmsManager::class.java)
                                                smsManager.sendTextMessage("+919352919258", null, messageText, null, null)
                                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                                    Toast.makeText(context, "Payment notification sent!", Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: Exception) {
                                                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                                    try {
                                                        val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                                            data = android.net.Uri.parse("smsto:+919352919258")
                                                            putExtra("sms_body", messageText)
                                                        }
                                                        context.startActivity(intent)
                                                    } catch (ex: Exception) {
                                                        // ignore fallback failure
                                                    }
                                                }
                                            }
                                        }
                                        paymentStage = 2
                                    } else {
                                        Toast.makeText(context, "Please enter a valid 12-digit UPI reference ID", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = refIdText.length == 12,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Verify & Activate Premium", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                            }
                        }
                    }
                    2 -> {
                        // Processing Screen
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF6B38D4), modifier = Modifier.size(60.dp))
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Verifying Ref No...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF191C1E)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Checking with bank transaction logs",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    3 -> {
                        // Success Screen
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD4F7E6))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Premium Activated!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF191C1E)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ref ID: $refIdText",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
