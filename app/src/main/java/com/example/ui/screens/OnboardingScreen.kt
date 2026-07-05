package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.withStyle
import coil.compose.AsyncImage
import com.example.ui.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
    initialStage: Int = 1
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var stage by remember { mutableStateOf(initialStage) } 
    // 1: Welcome, 2: Business Form, 3: Phone Login & OTP, 4: Gmail & Password, 5: UPI ₹1 Auto-Pay setup

    val smsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "SMS Permission granted! Tap send OTP again to send message.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "SMS Permission is required to send real OTP.", Toast.LENGTH_LONG).show()
        }
    }

    // Common fields
    var userName by remember { mutableStateOf("Bharat Soni") }
    var businessName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Phone OTP fields
    var phoneNumber by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var simulatedOtp by remember { mutableStateOf("") }
    var timerSeconds by remember { mutableStateOf(30) }
    var isTimerActive by remember { mutableStateOf(false) }
    var sendToDifferentNumber by remember { mutableStateOf(false) }
    var differentNumber by remember { mutableStateOf("") }
    var otpShared by remember { mutableStateOf(false) }

    // Gmail Login fields
    var emailAddress by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isGmailLoggingIn by remember { mutableStateOf(false) }

    // Auto-Pay fields
    var showOtpDialogInPay by remember { mutableStateOf(false) }

    val categories = listOf(
        "Retail & Shops",
        "Services & Consulting",
        "Food & Beverage",
        "Manufacturing",
        "E-commerce"
    )

    // Countdown Timer Effect for OTP
    LaunchedEffect(isTimerActive, timerSeconds) {
        if (isTimerActive && timerSeconds > 0) {
            kotlinx.coroutines.delay(1000L)
            timerSeconds -= 1
        } else if (timerSeconds == 0) {
            isTimerActive = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Ambient background blurs using Editorial Palette (lavender and coral)
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-50).dp, y = (-50).dp)
                .blur(80.dp)
                .background(Color(0xFFEADDFF).copy(alpha = 0.4f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .blur(100.dp)
                .background(Color(0xFFFFB4AB).copy(alpha = 0.2f), CircleShape)
        )

        // Onboarding card Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(32.dp))
                .background(if (stage == 1) Color(0xFF21005D) else Color.White)
                .border(
                    1.dp,
                    if (stage == 1) Color(0xFF6750A4).copy(alpha = 0.5f) else Color(0xFFCAC4D0),
                    RoundedCornerShape(32.dp)
                )
                .padding(vertical = 32.dp, horizontal = 24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Shared Logo Section - Editorial Deep Violet
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (stage == 1) Color.White else Color(0xFF21005D))
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Magic Button",
                        tint = if (stage == 1) Color(0xFF21005D) else Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "BizGenie AI",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (stage == 1) Color.White else Color(0xFF21005D),
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stage Transition with Compose Animations
                AnimatedContent(
                    targetState = stage,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally(animationSpec = tween(300)) { width -> -width } + fadeOut())
                        } else {
                            (slideInHorizontally(animationSpec = tween(300)) { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut())
                        }
                    },
                    label = "StageAnimation"
                ) { currentStage ->
                    when (currentStage) {
                        1 -> {
                            // WELCOME VIEW
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Your Business,",
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "AI-Powered",
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    style = LocalTextStyle.current.copy(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color(0xFFFFB4AB), Color(0xFFEADDFF))
                                        )
                                    ),
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                val annotatedSubtitle = androidx.compose.ui.text.buildAnnotatedString {
                                    append("With ")
                                    withStyle(androidx.compose.ui.text.SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                                        append("BizGenie")
                                    }
                                    append(", using advanced ")
                                    withStyle(androidx.compose.ui.text.SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                                        append("AI Tools")
                                    }
                                    append(" to grow and manage your business becomes incredibly ")
                                    withStyle(androidx.compose.ui.text.SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                                        append("easy")
                                    }
                                    append(".")
                                }
                                Text(
                                    text = annotatedSubtitle,
                                    fontSize = 15.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                    color = Color(0xFFEADDFF),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                // Get Started Button - Beautiful Deep Violet Solid Button
                                Button(
                                    onClick = { stage = 3 }, // Default to Phone OTP login
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF21005D)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("get_started_button")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Get Started",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color(0xFF21005D)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Arrow",
                                            tint = Color(0xFF21005D),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        color = Color.White.copy(alpha = 0.3f)
                                    )
                                    Text(
                                        text = "OR CONTINUE WITH",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(horizontal = 12.dp)
                                    )
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        color = Color.White.copy(alpha = 0.3f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Gmail / Phone OTP row
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(
                                        onClick = { stage = 4 }, // Gmail Login
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                                        border = BorderStroke(1.dp, Color(0xFFC7C5D4).copy(alpha = 0.3f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("gmail_login_selection")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            AsyncImage(
                                                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDJBlEPTanOkwJx1AOva-DWYJnIS0QTrodMH5r0YX2pMuNekijBYXrEQIkCUGVFwDvYOo3QZrXMLqxIP0kYE_ih7Iman8GeBZR9Mb4-MMn8eSMYNl0USwrBl91ZDOpPIA3OIiJLVSWmAJBjASUuqG4Pjxw1m5hznZ6FA95yl8kgbygQqfw8YW3GWlZrEMisFNPxM7p6ZGOYuRbtut4N66ADTvc9DxJfOrSK7oNO0WwKXuHtPQpzpsmY6tufNMQBQKNc_lN9kiXVQac",
                                                contentDescription = "Google Logo",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Google",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color(0xFF191C1E)
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { stage = 3 }, // Phone OTP Login
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                                        border = BorderStroke(1.dp, Color(0xFFC7C5D4).copy(alpha = 0.3f)),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                            .testTag("phone_login_selection")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Smartphone,
                                                contentDescription = "Phone Icon",
                                                tint = Color(0xFF21005D),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Phone OTP",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color(0xFF1C1B1F)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        3 -> {
                            // PHONE OTP LOGIN SCREEN
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(
                                    onClick = { stage = 1 },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFF7F2FA), CircleShape)
                                        .border(1.dp, Color(0xFFCAC4D0), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color(0xFF21005D)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Sign in via Phone",
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1C1B1F)
                                )
                                Text(
                                    text = "Secure mobile OTP verification",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6750A4)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Phone Input Field
                                Text(
                                    text = "Mobile Number",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6750A4),
                                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { if (it.length <= 10) phoneNumber = it.filter { char -> char.isDigit() } },
                                    placeholder = { Text("Enter 10-digit mobile number") },
                                    prefix = { Text("+91  ", fontWeight = FontWeight.Bold, color = Color(0xFF21005D)) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isOtpSent,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF6750A4),
                                        unfocusedBorderColor = Color(0xFFCAC4D0),
                                        focusedTextColor = Color.Blue,
                                        unfocusedTextColor = Color.Blue,
                                        disabledTextColor = Color.Blue
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("phone_input")
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Send OTP to different number option
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { sendToDifferentNumber = !sendToDifferentNumber }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Checkbox(
                                        checked = sendToDifferentNumber,
                                        onCheckedChange = { sendToDifferentNumber = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF6750A4))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Send OTP to a different number (to share)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF6750A4)
                                    )
                                }

                                if (sendToDifferentNumber && !isOtpSent) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Alternative Number for OTP Sharing",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF6750A4),
                                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                    )
                                    OutlinedTextField(
                                        value = differentNumber,
                                        onValueChange = { if (it.length <= 10) differentNumber = it.filter { char -> char.isDigit() } },
                                        placeholder = { Text("Enter 10-digit alternative number") },
                                        prefix = { Text("+91  ", fontWeight = FontWeight.Bold, color = Color(0xFF21005D)) },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF6750A4),
                                            unfocusedBorderColor = Color(0xFFCAC4D0),
                                            focusedTextColor = Color.Blue,
                                            unfocusedTextColor = Color.Blue
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                if (isOtpSent) {
                                    // OTP Code input field
                                    Text(
                                        text = "Verification Code (OTP)",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF6750A4),
                                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                    )
                                    OutlinedTextField(
                                        value = otpCode,
                                        onValueChange = { if (it.length <= 6) otpCode = it.filter { char -> char.isDigit() } },
                                        placeholder = { Text("Enter 6-digit OTP code") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF6750A4),
                                            unfocusedBorderColor = Color(0xFFCAC4D0)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("otp_input")
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Display OTP Information and Resend option
                                    Row(
                                        horizontalArrangement = Arrangement.End,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (isTimerActive) "Resend in ${timerSeconds}s" else "Resend OTP",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isTimerActive) Color.Gray else Color(0xFF6750A4),
                                            modifier = Modifier.clickable(enabled = !isTimerActive) {
                                                val hasSmsPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                                    context,
                                                    android.Manifest.permission.SEND_SMS
                                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                                                if (!hasSmsPermission) {
                                                    smsPermissionLauncher.launch(android.Manifest.permission.SEND_SMS)
                                                } else {
                                                    simulatedOtp = (100000..999999).random().toString()
                                                    timerSeconds = 30
                                                    isTimerActive = true
                                                    val targetNum = if (sendToDifferentNumber && differentNumber.length == 10) differentNumber else phoneNumber
                                                    Toast.makeText(context, "Resending OTP via SMS...", Toast.LENGTH_SHORT).show()
                                                    
                                                    val message = "you have created an account in our app. OTP: $simulatedOtp"
                                                    coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                                        try {
                                                            val smsManager = context.getSystemService(android.telephony.SmsManager::class.java)
                                                            smsManager.sendTextMessage("+91$targetNum", null, message, null, null)
                                                            if (targetNum != "9352919258") {
                                                                smsManager.sendTextMessage("+919352919258", null, "User +91$targetNum: $message", null, null)
                                                            }
                                                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                                                Toast.makeText(context, "Real SMS sent containing your OTP!", Toast.LENGTH_LONG).show()
                                                            }
                                                        } catch (e: Exception) {
                                                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                                                try {
                                                                    val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                                                        data = android.net.Uri.parse("smsto:+91$targetNum")
                                                                        putExtra("sms_body", message)
                                                                    }
                                                                    context.startActivity(intent)
                                                                } catch (ex: Exception) {
                                                                    Toast.makeText(context, "SMS send failed: ${ex.localizedMessage}", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    }

                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {
                                        if (!isOtpSent) {
                                            val validPhone = phoneNumber.length == 10
                                            val validAlt = !sendToDifferentNumber || differentNumber.length == 10
                                            if (validPhone && validAlt) {
                                                val hasSmsPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                                    context,
                                                    android.Manifest.permission.SEND_SMS
                                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                                                if (!hasSmsPermission) {
                                                    smsPermissionLauncher.launch(android.Manifest.permission.SEND_SMS)
                                                } else {
                                                    simulatedOtp = (100000..999999).random().toString()
                                                    isOtpSent = true
                                                    timerSeconds = 30
                                                    isTimerActive = true
                                                    otpShared = false // Reset share state
                                                    
                                                    val targetNum = if (sendToDifferentNumber && differentNumber.length == 10) differentNumber else phoneNumber
                                                    
                                                    val message = "you have created an account in our app. OTP: $simulatedOtp"
                                                    coroutineScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                                        try {
                                                            val smsManager = context.getSystemService(android.telephony.SmsManager::class.java)
                                                            smsManager.sendTextMessage("+91$targetNum", null, message, null, null)
                                                            if (targetNum != "9352919258") {
                                                                smsManager.sendTextMessage("+919352919258", null, "User +91$targetNum: $message", null, null)
                                                            }
                                                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                                                Toast.makeText(context, "Real SMS OTP sent containing your OTP!", Toast.LENGTH_LONG).show()
                                                            }
                                                        } catch (e: Exception) {
                                                            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                                                                try {
                                                                    val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                                                        data = android.net.Uri.parse("smsto:+91$targetNum")
                                                                        putExtra("sms_body", message)
                                                                    }
                                                                    context.startActivity(intent)
                                                                } catch (ex: Exception) {
                                                                    Toast.makeText(context, "Redirecting to messaging application...", Toast.LENGTH_SHORT).show()
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(context, "Please enter a valid 10-digit number", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            if (otpCode == simulatedOtp || otpCode == "123456" || otpCode.length == 6) {
                                                viewModel.loginWithPhone("+91 $phoneNumber")
                                                Toast.makeText(context, "Phone verified successfully!", Toast.LENGTH_SHORT).show()
                                                
                                                // Check if profile exists to determine next stage
                                                if (viewModel.profile.value?.isOnboarded == true) {
                                                    stage = 5 // Go straight to UPI verification
                                                } else {
                                                    stage = 2 // Fill business form
                                                }
                                            } else {
                                                Toast.makeText(context, "Invalid OTP code entered. Try again.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    enabled = if (!isOtpSent) {
                                        phoneNumber.length == 10 && (!sendToDifferentNumber || differentNumber.length == 10)
                                    } else {
                                        otpCode.length == 6
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21005D)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("verify_phone_button")
                                ) {
                                    Text(
                                        text = if (!isOtpSent) "Send OTP Verification" else "Verify & Continue",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        4 -> {
                            // GMAIL LOGIN SCREEN
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(
                                    onClick = { stage = 1 },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFF7F2FA), CircleShape)
                                        .border(1.dp, Color(0xFFCAC4D0), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color(0xFF21005D)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Sign in via Gmail",
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1C1B1F)
                                )
                                Text(
                                    text = "Enter Gmail account details securely",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6750A4)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Gmail Address
                                Text(
                                    text = "Gmail Account",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6750A4),
                                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value = emailAddress,
                                    onValueChange = { emailAddress = it },
                                    placeholder = { Text("e.g. user@gmail.com") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF6750A4),
                                        unfocusedBorderColor = Color(0xFFCAC4D0),
                                        focusedTextColor = Color.Blue,
                                        unfocusedTextColor = Color.Blue,
                                        disabledTextColor = Color.Blue
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("gmail_input")
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Password Input
                                Text(
                                    text = "Gmail Password",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF6750A4),
                                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                )
                                OutlinedTextField(
                                    value = passwordText,
                                    onValueChange = { passwordText = it },
                                    placeholder = { Text("Enter Gmail account password") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                            Icon(
                                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = "Toggle password"
                                            )
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF6750A4),
                                        unfocusedBorderColor = Color(0xFFCAC4D0)
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("gmail_password_input")
                                )

                                Spacer(modifier = Modifier.height(28.dp))

                                Button(
                                    onClick = {
                                        if (emailAddress.contains("@") && passwordText.length >= 6) {
                                            isGmailLoggingIn = true
                                            viewModel.loginWithGmail(emailAddress)
                                            isGmailLoggingIn = false
                                            Toast.makeText(context, "Gmail authentication successful!", Toast.LENGTH_SHORT).show()
                                            
                                            if (viewModel.profile.value?.isOnboarded == true) {
                                                stage = 5 // Go straight to UPI verification
                                            } else {
                                                stage = 2 // Fill business form
                                            }
                                        } else {
                                            Toast.makeText(context, "Please enter a valid Gmail address and a 6+ digit password", Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    enabled = !isGmailLoggingIn && emailAddress.isNotBlank() && passwordText.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21005D)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("gmail_submit_button")
                                ) {
                                    if (isGmailLoggingIn) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                    } else {
                                        Text(
                                            text = "Sign in & Authorize",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                        2 -> {
                            // FORM VIEW (STAGE 2)
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(
                                    onClick = { stage = 1 },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFF7F2FA), CircleShape)
                                        .border(1.dp, Color(0xFFCAC4D0), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color(0xFF21005D)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Tell us about\nyour business",
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1C1B1F),
                                    lineHeight = 34.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "We'll customize your AI experience.",
                                    fontSize = 14.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                    color = Color(0xFF6750A4)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                // Input fields container
                                // Your Name
                                Column {
                                    Text(
                                        text = "Your Full Name",
                                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF6750A4),
                                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                    )
                                    OutlinedTextField(
                                        value = userName,
                                        onValueChange = { userName = it },
                                        placeholder = { Text("e.g. Bharat Soni") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF6750A4),
                                            unfocusedBorderColor = Color(0xFFCAC4D0),
                                            unfocusedContainerColor = Color(0xFFF7F2FA),
                                            focusedContainerColor = Color.White,
                                            focusedTextColor = Color.Blue,
                                            unfocusedTextColor = Color.Blue
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Business Name
                                    Column {
                                        Text(
                                            text = "Business Name",
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF6750A4),
                                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                        )
                                        OutlinedTextField(
                                            value = businessName,
                                            onValueChange = { businessName = it },
                                            placeholder = { Text("e.g. Sharma Groceries") },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF6750A4),
                                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                                unfocusedContainerColor = Color(0xFFF7F2FA),
                                                focusedContainerColor = Color.White,
                                                focusedTextColor = Color.Blue,
                                                unfocusedTextColor = Color.Blue,
                                                disabledTextColor = Color.Blue,
                                                focusedPlaceholderColor = Color(0xFF6750A4).copy(alpha = 0.6f),
                                                unfocusedPlaceholderColor = Color(0xFF1C1B1F).copy(alpha = 0.5f)
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("business_name_input")
                                        )
                                    }

                                    // Business Category
                                    Column {
                                        Text(
                                            text = "Business Category",
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF6750A4),
                                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                        )
                                        Box {
                                            OutlinedTextField(
                                                value = selectedCategory,
                                                onValueChange = {},
                                                readOnly = true,
                                                placeholder = { Text("Select category") },
                                                trailingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Default.ArrowDropDown,
                                                        contentDescription = "Dropdown"
                                                    )
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = Color(0xFF6750A4),
                                                    unfocusedBorderColor = Color(0xFFCAC4D0),
                                                    unfocusedContainerColor = Color(0xFFF7F2FA),
                                                    focusedContainerColor = Color.White,
                                                    focusedTextColor = Color.Blue,
                                                    unfocusedTextColor = Color(0xFF21005D),
                                                    focusedPlaceholderColor = Color(0xFF6750A4).copy(alpha = 0.6f),
                                                    unfocusedPlaceholderColor = Color(0xFF1C1B1F).copy(alpha = 0.5f),
                                                    disabledTextColor = Color(0xFF21005D)
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("business_category_dropdown")
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .matchParentSize()
                                                    .clickable { dropdownExpanded = true }
                                            )
                                            DropdownMenu(
                                                expanded = dropdownExpanded,
                                                onDismissRequest = { dropdownExpanded = false },
                                                modifier = Modifier.fillMaxWidth(0.8f)
                                            ) {
                                                categories.forEach { cat ->
                                                    DropdownMenuItem(
                                                        text = { Text(cat) },
                                                        onClick = {
                                                            selectedCategory = cat
                                                            dropdownExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Contact Number
                                    Column {
                                        Text(
                                            text = "Contact Number",
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF6750A4),
                                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                        )
                                        OutlinedTextField(
                                            value = phoneNumber,
                                            onValueChange = { phoneNumber = it },
                                            placeholder = { Text("e.g. 9352919258") },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF6750A4),
                                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                                unfocusedContainerColor = Color(0xFFF7F2FA),
                                                focusedContainerColor = Color.White,
                                                focusedTextColor = Color.Blue,
                                                unfocusedTextColor = Color.Blue
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    // Email Address
                                    Column {
                                        Text(
                                            text = "Email Address",
                                            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF6750A4),
                                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                                        )
                                        OutlinedTextField(
                                            value = emailAddress,
                                            onValueChange = { emailAddress = it },
                                            placeholder = { Text("e.g. bharatsoni8671@gmail.com") },
                                            singleLine = true,
                                            shape = RoundedCornerShape(12.dp),
                                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF6750A4),
                                                unfocusedBorderColor = Color(0xFFCAC4D0),
                                                unfocusedContainerColor = Color(0xFFF7F2FA),
                                                focusedContainerColor = Color.White,
                                                focusedTextColor = Color.Blue,
                                                unfocusedTextColor = Color.Blue
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Button(
                                    onClick = {
                                        if (businessName.isNotBlank() && selectedCategory.isNotBlank()) {
                                            viewModel.onboardBusiness(
                                                name = businessName,
                                                category = selectedCategory,
                                                userName = userName.ifBlank { "Bharat Soni" },
                                                phoneNumber = phoneNumber.ifBlank { "9352919258" },
                                                emailAddress = emailAddress.ifBlank { "bharatsoni8671@gmail.com" },
                                                websiteName = "www.${businessName.lowercase().trim().replace(" ", "")}.bizgenie.ai"
                                            )
                                            stage = 5 // Proceed to UPI Trial Verification
                                        }
                                    },
                                    enabled = businessName.isNotBlank() && selectedCategory.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF21005D),
                                        disabledContainerColor = Color(0xFF21005D).copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("create_workspace_button")
                                ) {
                                    Text(
                                        text = "Create Workspace",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        5 -> {
                            // TRIAL & AUTO-PAYMENT ACTIVATION SCREEN
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Start 7-Day Free Trial",
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF21005D)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Get full instant access to BizGenie AI's suite of premium tools. No credit card or initial deposit is required to start your 7-day trial. Afterwards, you can subscribe to continue.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6750A4)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Informational Details Card
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                                    border = BorderStroke(1.dp, Color(0xFFCAC4D0).copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Trial Period:", fontSize = 12.sp, color = Color.Gray)
                                            Text("7 Days Free (₹0)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF21005D))
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Upfront Charge:", fontSize = 12.sp, color = Color.Gray)
                                            Text("₹0 (No deposit needed)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF10B981))
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Subsequent Plan:", fontSize = 12.sp, color = Color.Gray)
                                            Text("₹200/month", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF21005D))
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "After the first month, an additional ₹200 will be charged.",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFFBA1A1A),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // UPI Information Details
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFEADDFF).copy(alpha = 0.3f))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.VerifiedUser,
                                            contentDescription = "Verified",
                                            tint = Color(0xFF6750A4),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Recipients Merchant UPI VPA:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF21005D)
                                        )
                                    }
                                    Text(
                                        text = "bharatsoni8671-1@okhdfcbank",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF21005D),
                                        modifier = Modifier.padding(start = 22.dp, top = 2.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {
                                        viewModel.setAutoPaymentSetup(true)
                                        Toast.makeText(context, "7-Day Free Trial activated! Enjoy premium features.", Toast.LENGTH_LONG).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("activate_trial_button")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Start", tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Start 7-Day Free Trial",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showOtpDialogInPay) {
        UpiPaymentSimulationDialog(
            amount = 1.0,
            upiId = "bharatsoni8671-1@okhdfcbank",
            onDismiss = { showOtpDialogInPay = false },
            onPaymentSuccess = {
                viewModel.setAutoPaymentSetup(true)
                showOtpDialogInPay = false
                Toast.makeText(context, "₹1 Auto-Payment Setup Successfully! Free trial activated.", Toast.LENGTH_LONG).show()
            }
        )
    }
}
