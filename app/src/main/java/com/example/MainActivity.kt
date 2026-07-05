package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.screens.AiToolsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyBusinessScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PaywallScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val profileState by viewModel.profile.collectAsStateWithLifecycle()
                    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
                    val isAutoPaymentSetup by viewModel.isAutoPaymentSetup.collectAsStateWithLifecycle()
                    val isSubscribed by viewModel.isSubscribed.collectAsStateWithLifecycle()
                    val trialDaysElapsed by viewModel.trialDaysElapsed.collectAsStateWithLifecycle()

                    val isTrialExpired = trialDaysElapsed >= 7
                    val needsSubscriptionPayment = isTrialExpired && !isSubscribed

                    if (!isLoggedIn || profileState == null || !profileState!!.isOnboarded) {
                        OnboardingScreen(viewModel = viewModel)
                    } else if (!isAutoPaymentSetup) {
                        OnboardingScreen(viewModel = viewModel, initialStage = 5)
                    } else if (needsSubscriptionPayment) {
                        PaywallScreen(viewModel = viewModel)
                    } else {
                        MainAppFrame(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppFrame(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(1) } // Default to AI Tools (tab 1) as requested in visual mockups

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Logo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "BizGenie AI",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    val isSubscribed by viewModel.isSubscribed.collectAsStateWithLifecycle()
                    val trialDaysElapsed by viewModel.trialDaysElapsed.collectAsStateWithLifecycle()
                    if (!isSubscribed) {
                        val daysLeft = (7 - trialDaysElapsed).coerceAtLeast(0)
                        AssistChip(
                            onClick = {},
                            label = { Text("Trial: $daysLeft Days Left", fontWeight = FontWeight.Bold, color = Color(0xFFBA1A1A), fontSize = 12.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFFFFDAD6),
                                labelColor = Color(0xFFBA1A1A)
                            ),
                            border = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    IconButton(onClick = { selectedTab = 3 }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFDF8FD).copy(alpha = 0.95f)
                ),
                modifier = Modifier.testTag("top_app_bar")
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF3EDF7),
                tonalElevation = 4.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_tab_home")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI Tools") },
                    label = { Text("AI Tools", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_tab_tools")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(imageVector = Icons.Default.Storefront, contentDescription = "My Business") },
                    label = { Text("My Business", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_tab_business")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("nav_tab_profile")
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(viewModel = viewModel, onNavigateToTools = { selectedTab = 1 })
                1 -> AiToolsScreen(viewModel = viewModel)
                2 -> MyBusinessScreen(viewModel = viewModel)
                3 -> ProfileScreen(viewModel = viewModel)
            }
        }
    }
}
