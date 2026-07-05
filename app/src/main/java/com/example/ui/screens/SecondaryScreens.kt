package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.BuildConfig
import com.example.ui.MainViewModel
import com.example.ui.BusinessMetrics

// --- HELPER METRIC FORMATTING ---
private val indianCurrencyFormat: java.text.NumberFormat by lazy {
    java.text.NumberFormat.getCurrencyInstance(java.util.Locale("en", "IN"))
}

fun formatRupees(amount: Double): String {
    val result = synchronized(indianCurrencyFormat) {
        indianCurrencyFormat.format(amount)
    }
    return result.replace("₹", "").replace("Rs.", "").trim()
}

fun formatRupeesCompact(amount: Double): String {
    return when {
        amount >= 100000.0 -> String.format("%.2fL", amount / 100000.0)
        amount >= 1000.0 -> String.format("%.1fK", amount / 1000.0)
        else -> String.format("%.0f", amount)
    }
}

// --- 1. HOME SCREEN ---
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToTools: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentProfile by viewModel.profile.collectAsState()
    val websites by viewModel.websites.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val identities by viewModel.identities.collectAsState()
    val metrics by viewModel.metrics.collectAsState()
    val context = LocalContext.current

    val businessName = currentProfile?.name ?: "Indian Entrepreneur"
    val category = currentProfile?.category ?: "Retail"

    // Dashboard toggle state
    var selectedDashboardTab by remember { mutableStateOf(0) } // 0 = Performance Analytics, 1 = AI Output Hub
    var showAdjustMetrics by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // Welcoming card - Editorial Gothic Minimal Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(40.dp))
                .background(Color(0xFF21005D))
                .padding(28.dp)
        ) {
            Column {
                // Workspace Active Badge - Coral styling
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFFFB4AB), RoundedCornerShape(50.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF690005))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Workspace Active",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF690005),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Namaste, \n$businessName!",
                    fontSize = 32.sp,
                    lineHeight = 36.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Sector: $category",
                    fontSize = 13.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onNavigateToTools,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB4AB),
                        contentColor = Color(0xFF690005)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Stars")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Launch AI Generators",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Centralized Business Command Center Header
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Business Command Center",
                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Live Sync",
                fontSize = 11.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Centralized Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF7F2FA))
                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf("Performance Dashboard", "AI Generated Hub")
            tabs.forEachIndexed { index, title ->
                val selected = selectedDashboardTab == index
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) Color(0xFF21005D) else Color.Transparent)
                        .clickable { selectedDashboardTab = index }
                        .testTag("dashboard_tab_$index")
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) Color.White else Color(0xFF6750A4)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Toggleable Dashboard Sections
        if (selectedDashboardTab == 0) {
            // --- TAB 0: PERFORMANCE DASHBOARD (User Requested Key Metrics) ---

            // Adjust Metrics expandable panel
            AnimatedVisibility(
                visible = showAdjustMetrics,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7F6)),
                    border = BorderStroke(1.dp, Color(0xFFFFB4AB)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Adjust Business Metrics",
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF690005)
                            )
                            IconButton(onClick = { showAdjustMetrics = false }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF690005))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Custom Numeric inputs
                        var inputRevenue by remember { mutableStateOf(metrics.monthlyRevenue.toString()) }
                        var inputTarget by remember { mutableStateOf(metrics.targetRevenue.toString()) }
                        var inputTraffic by remember { mutableStateOf(metrics.websiteTraffic.toString()) }
                        var inputLeads by remember { mutableStateOf(metrics.leadsCount.toString()) }
                        var inputCustomers by remember { mutableStateOf(metrics.convertedCustomers.toString()) }
                        
                        var inputMarketing by remember { mutableStateOf(metrics.expenseMarketing.toString()) }
                        var inputInventory by remember { mutableStateOf(metrics.expenseInventory.toString()) }
                        var inputRentWages by remember { mutableStateOf(metrics.expenseRentSalaries.toString()) }
                        var inputAI by remember { mutableStateOf(metrics.expenseSoftwareAI.toString()) }
                        var inputMisc by remember { mutableStateOf(metrics.expenseMiscellaneous.toString()) }

                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = inputRevenue,
                                    onValueChange = { inputRevenue = it },
                                    label = { Text("Revenue (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = inputTarget,
                                    onValueChange = { inputTarget = it },
                                    label = { Text("Target (₹)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = inputTraffic,
                                    onValueChange = { inputTraffic = it },
                                    label = { Text("Traffic") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1.2f)
                                )
                                OutlinedTextField(
                                    value = inputLeads,
                                    onValueChange = { inputLeads = it },
                                    label = { Text("Leads") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(0.9f)
                                )
                                OutlinedTextField(
                                    value = inputCustomers,
                                    onValueChange = { inputCustomers = it },
                                    label = { Text("Clients") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(0.9f)
                                )
                            }

                            Text(
                                text = "EXPENSES BREAKDOWN (₹)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF690005)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = inputMarketing,
                                    onValueChange = { inputMarketing = it },
                                    label = { Text("Marketing") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = inputInventory,
                                    onValueChange = { inputInventory = it },
                                    label = { Text("Inventory") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = inputRentWages,
                                    onValueChange = { inputRentWages = it },
                                    label = { Text("Rent/Wages") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1.2f)
                                )
                                OutlinedTextField(
                                    value = inputAI,
                                    onValueChange = { inputAI = it },
                                    label = { Text("AI Tools") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(0.9f)
                                )
                                OutlinedTextField(
                                    value = inputMisc,
                                    onValueChange = { inputMisc = it },
                                    label = { Text("Misc") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(0.9f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val rev = inputRevenue.toDoubleOrNull() ?: metrics.monthlyRevenue
                                val tar = inputTarget.toDoubleOrNull() ?: metrics.targetRevenue
                                val traf = inputTraffic.toIntOrNull() ?: metrics.websiteTraffic
                                val lds = inputLeads.toIntOrNull() ?: metrics.leadsCount
                                val custs = inputCustomers.toIntOrNull() ?: metrics.convertedCustomers

                                val mkt = inputMarketing.toDoubleOrNull() ?: metrics.expenseMarketing
                                val inv = inputInventory.toDoubleOrNull() ?: metrics.expenseInventory
                                val rent = inputRentWages.toDoubleOrNull() ?: metrics.expenseRentSalaries
                                val ai = inputAI.toDoubleOrNull() ?: metrics.expenseSoftwareAI
                                val misc = inputMisc.toDoubleOrNull() ?: metrics.expenseMiscellaneous

                                viewModel.updateMetrics(
                                    BusinessMetrics(
                                        monthlyRevenue = rev,
                                        targetRevenue = tar,
                                        previousMonthRevenue = metrics.previousMonthRevenue,
                                        websiteTraffic = traf,
                                        leadsCount = lds,
                                        convertedCustomers = custs,
                                        expenseMarketing = mkt,
                                        expenseInventory = inv,
                                        expenseRentSalaries = rent,
                                        expenseSoftwareAI = ai,
                                        expenseMiscellaneous = misc
                                    )
                                )
                                showAdjustMetrics = false
                                Toast.makeText(context, "Metrics updated instantly!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF690005)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Metric 1: Revenue Growth Card (Dynamic & Visual)
            RevenueGrowthCard(
                metrics = metrics,
                onAdjustClick = { showAdjustMetrics = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Metric 2: Lead Conversion Funnel & Simulator
            LeadConversionFunnelCard(
                metrics = metrics
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Metric 3: Expenses Summary and Ring Donut Chart
            ExpenseBreakdownCard(
                metrics = metrics
            )

        } else {
            // --- TAB 1: AI GENERATED HUB (Standard Metrics overview) ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricCard(title = "Websites", count = websites.size, icon = Icons.Default.Language, modifier = Modifier.weight(1f))
                MetricCard(title = "Social Posts", count = posts.size, icon = Icons.Default.Celebration, modifier = Modifier.weight(1f))
                MetricCard(title = "Brand Kits", count = identities.size, icon = Icons.Default.Brush, modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // India Small Business Trends & Tips
        Text(
            text = "Custom Insights & Daily Tips",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        val tips = when (category) {
            "Retail & Shops" -> listOf(
                "📈 Indian retail trend: Festive sales increase by 40% when local WhatsApp greetings are sent 3 days in advance.",
                "💡 Tip: Mention home-delivery options clearly in your generated website header."
            )
            "Services & Consulting" -> listOf(
                "📈 Consultancy trend: Clear customer testimonial blocks increase trust conversion by 60%.",
                "💡 Tip: Use the 'Bold' design style to convey professional authority."
            )
            "Food & Beverage" -> listOf(
                "📈 Food trend: Vibrant images of hot meals drive 2x engagement on Instagram posts compared to standard vector templates.",
                "💡 Tip: Highlight the 'Reserve a Table' button on your published website."
            )
            else -> listOf(
                "📈 Indian small-business tip: Always include localized contact information (like phone or WhatsApp) to resolve customer doubts immediately.",
                "💡 Daily Tip: Download your high-definition brand logo and set it as your WhatsApp Business profile pic!"
            )
        }

        tips.forEach { tip ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = tip,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

// --- CORE DASHBOARD COMPONENT PARTS ---

@Composable
fun RevenueGrowthCard(
    metrics: BusinessMetrics,
    onAdjustClick: () -> Unit
) {
    val progress = if (metrics.targetRevenue > 0) (metrics.monthlyRevenue / metrics.targetRevenue).coerceIn(0.0, 1.0) else 1.0
    val momGrowth = if (metrics.previousMonthRevenue > 0) {
        ((metrics.monthlyRevenue - metrics.previousMonthRevenue) / metrics.previousMonthRevenue) * 100
    } else {
        0.0
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Revenue & Growth",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Real-time fiscal monitoring",
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .background(if (momGrowth >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${if (momGrowth >= 0) "+" else ""}${String.format("%.1f", momGrowth)}% MoM",
                        color = if (momGrowth >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "₹${formatRupees(metrics.monthlyRevenue)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "This Month's Sales",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹${formatRupees(metrics.targetRevenue)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Target (Progress: ${String.format("%.0f", progress * 100)}%)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Target progress bar
            LinearProgressIndicator(
                progress = { progress.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF21005D),
                trackColor = Color(0xFFE8DDFF)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Revenue Canvas Bar/Line Chart
            Text(
                text = "REVENUE TRENDS (LAST 5 MONTHS)",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.height(12.dp))

            val historicalData = listOf(
                Pair("Jan", 105000.0),
                Pair("Feb", 115000.0),
                Pair("Mar", 120000.0),
                Pair("Apr", metrics.previousMonthRevenue),
                Pair("May", metrics.monthlyRevenue)
            )

            RevenueTrendsChart(historicalData = historicalData)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onAdjustClick,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF6750A4)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adjust Monthly Metrics", fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif)
            }
        }
    }
}

@Composable
fun RevenueTrendsChart(historicalData: List<Pair<String, Double>>) {
    val maxVal = (historicalData.maxOfOrNull { it.second } ?: 1.0) * 1.15 // 15% padding top
    
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0).copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                val width = size.width
                val height = size.height
                val barCount = historicalData.size
                val barWidth = 28.dp.toPx()
                val spacing = (width - (barCount * barWidth)) / (barCount + 1)

                historicalData.forEachIndexed { index, item ->
                    val barHeight = ((item.second / maxVal) * height).toFloat()
                    val x = spacing + (index * (barWidth + spacing))
                    val y = height - barHeight

                    // Draw track background shadow for clean look
                    drawRoundRect(
                        color = Color(0xFFF3EDF7),
                        topLeft = androidx.compose.ui.geometry.Offset(x, 0f),
                        size = androidx.compose.ui.geometry.Size(barWidth, height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )

                    // Draw active value bar with rich vertical indigo gradient
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF6750A4), Color(0xFF21005D))
                        ),
                        topLeft = androidx.compose.ui.geometry.Offset(x, y),
                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Aligned Labels row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                historicalData.forEach {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = it.first,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF21005D),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                        )
                        Text(
                            text = "₹${formatRupeesCompact(it.second)}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6750A4),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeadConversionFunnelCard(
    metrics: BusinessMetrics
) {
    val traffic = metrics.websiteTraffic
    val leads = metrics.leadsCount
    val customers = metrics.convertedCustomers

    val leadRate = if (traffic > 0) (leads.toDouble() / traffic) * 100 else 0.0
    val customerRate = if (leads > 0) (customers.toDouble() / leads) * 100 else 0.0
    val overallRate = if (traffic > 0) (customers.toDouble() / traffic) * 100 else 0.0

    // Simulator State (Hoisted internally for fluid instant sliding)
    var simTraffic by remember { mutableFloatStateOf(traffic.toFloat()) }
    var simConvRate by remember { mutableFloatStateOf(overallRate.toFloat()) }

    val simCustomers = ((simTraffic * (simConvRate / 100)).toInt()).coerceAtLeast(0)
    val averageOrderValue = if (customers > 0) metrics.monthlyRevenue / customers else 3500.0
    val simEstimatedRevenue = simCustomers * averageOrderValue

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Lead Conversion Funnel",
                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Conversion dropoff and simulation sandbox",
                fontSize = 12.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Funnel Stage 1
            FunnelStageRow(
                stageName = "Website Visitors",
                value = "$traffic visitors",
                percentage = "100%",
                color = Color(0xFF21005D)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Funnel Stage 2
            FunnelStageRow(
                stageName = "Captured Leads",
                value = "$leads leads",
                percentage = "${String.format("%.1f", leadRate)}% rate",
                color = Color(0xFF6750A4)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Funnel Stage 3
            FunnelStageRow(
                stageName = "Paying Clients",
                value = "$customers customers",
                percentage = "${String.format("%.1f", customerRate)}% rate",
                color = Color(0xFFFFB4AB),
                textColor = Color(0xFF690005)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Overall Conversion Rate:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                )
                Text(
                    text = "${String.format("%.1f", overallRate)}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Simulator sandbox (runs inside native thread with ultra-fast responsiveness)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Sandbox",
                            tint = Color(0xFF6750A4),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BizGenie Scenario Simulator",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF21005D),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Website Traffic: ${simTraffic.toInt()} monthly visitors",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                    )
                    Slider(
                        value = simTraffic,
                        onValueChange = { simTraffic = it },
                        valueRange = 100f..5000f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF6750A4),
                            activeTrackColor = Color(0xFF6750A4),
                            inactiveTrackColor = Color(0xFFE8DDFF)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Conversion Rate: ${String.format("%.1f", simConvRate)}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                    )
                    Slider(
                        value = simConvRate,
                        onValueChange = { simConvRate = it },
                        valueRange = 0.1f..15f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF6750A4),
                            activeTrackColor = Color(0xFF6750A4),
                            inactiveTrackColor = Color(0xFFE8DDFF)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFFCAC4D0).copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(text = "CLIENTS OUTCOME", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF777683), fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif)
                            Text(text = "$simCustomers clients", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF21005D), fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "ESTIMATED REVENUE Impact", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF777683), fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif)
                            Text(text = "₹${formatRupees(simEstimatedRevenue)}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2E7D32), fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FunnelStageRow(
    stageName: String,
    value: String,
    percentage: String,
    color: Color,
    textColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(color)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stageName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor.copy(alpha = 0.8f),
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
            )
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
            )
        }
        Box(
            modifier = Modifier
                .background(textColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = percentage,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun ExpenseBreakdownCard(
    metrics: BusinessMetrics
) {
    val totalExpense = metrics.expenseMarketing + metrics.expenseInventory +
            metrics.expenseRentSalaries + metrics.expenseSoftwareAI + metrics.expenseMiscellaneous
    val netProfit = metrics.monthlyRevenue - totalExpense
    val profitMargin = if (metrics.monthlyRevenue > 0) (netProfit / metrics.monthlyRevenue) * 100 else 0.0

    // Indian themed high-contrast colors
    val categories = listOf(
        Triple("Ads & Marketing", metrics.expenseMarketing, Color(0xFF6750A4)),
        Triple("Inventory Stock", metrics.expenseInventory, Color(0xFFFFB4AB)),
        Triple("Rent & Wages", metrics.expenseRentSalaries, Color(0xFF006C4C)),
        Triple("AI & Tech Software", metrics.expenseSoftwareAI, Color(0xFF03A9F4)),
        Triple("Miscellaneous", metrics.expenseMiscellaneous, Color(0xFFFF9800))
    ).filter { it.second > 0 }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Monthly Expenses Summary",
                fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Outflow breakdowns and margins",
                fontSize = 12.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Donut Ring Canvas
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(110.dp)
                ) {
                    ExpenseDonutChart(categories = categories, total = totalExpense)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TOTAL",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF777683),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                        )
                        Text(
                            text = "₹${formatRupeesCompact(totalExpense)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF21005D),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                        )
                    }
                }

                // Legend
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { (name, amount, color) ->
                        val share = if (totalExpense > 0) (amount / totalExpense) * 100 else 0.0
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                                )
                            }
                            Text(
                                text = "${String.format("%.0f", share)}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = Color(0xFFCAC4D0).copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(16.dp))

            // Net Profit Margin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Net Profit Margin",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                    )
                    Text(
                        text = "₹${formatRupees(netProfit)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (netProfit >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                    )
                }

                Box(
                    modifier = Modifier
                        .background(if (netProfit >= 0) Color(0xFFE8F5E9) else Color(0xFFFFEBEE), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${String.format("%.1f", profitMargin)}% Margin",
                        color = if (netProfit >= 0) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseDonutChart(categories: List<Triple<String, Double, Color>>, total: Double) {
    if (total <= 0) return
    Canvas(modifier = Modifier.fillMaxSize()) {
        var startAngle = -90f
        categories.forEach { (_, amount, color) ->
            val sweepAngle = ((amount / total) * 360f).toFloat()
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(
                    width = 8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
            startAngle += sweepAngle
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = String.format("%02d", count),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


// --- 2. MY BUSINESS CATALOG SCREEN ---
@Composable
fun MyBusinessScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val websites by viewModel.websites.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val identities by viewModel.identities.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        Text(
            text = "My Business Catalog",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Manage and copy all content you have generated with AI.",
            fontSize = 13.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // WEBSITES CATALOG
        Text(
            text = "Saved Websites (${websites.size})",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (websites.isEmpty()) {
            EmptyCatalogCard(text = "No websites created yet. Use AI Tools tab to generate one!")
        } else {
            websites.forEach { web ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                    border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = "Web", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = web.templateName, fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = web.heroTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = web.heroDescription, fontSize = 12.sp, color = Color(0xFF464652), maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                Toast.makeText(context, "Navigating to: www.${web.businessName.lowercase().replace(" ","")}.bizgenie.ai", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(android.graphics.Color.parseColor(web.accentColor))),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Launch Live Site")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SOCIAL POSTS CATALOG
        Text(
            text = "Marketing Posts (${posts.size})",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (posts.isEmpty()) {
            EmptyCatalogCard(text = "No marketing posts created yet.")
        } else {
            posts.forEach { post ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                    border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Celebration, contentDescription = "Post", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = post.contentType, fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = post.caption, fontSize = 12.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif, color = MaterialTheme.colorScheme.onSurface, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = post.hashtags, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("marketing_caption", post.caption + "\n" + post.hashtags)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Copied caption to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEEF0))
                            ) {
                                Text("Copy Text", color = Color(0xFF191C1E))
                            }

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Opening WhatsApp Share...", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                            ) {
                                Text("Share")
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BRAND IDENTITIES CATALOG
        Text(
            text = "Brand Identity Kits (${identities.size})",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (identities.isEmpty()) {
            EmptyCatalogCard(text = "No brand kits saved yet.")
        } else {
            identities.forEach { id ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                    border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = id.logoImageUrl,
                            contentDescription = id.logoConceptName,
                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF3EDF7))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = id.logoConceptName, fontFamily = androidx.compose.ui.text.font.FontFamily.Serif, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(text = "Style: ${id.styleName}", fontSize = 12.sp, color = Color(0xFF464652))
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(id.primaryColor))))
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(id.secondaryColor))))
                                Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(android.graphics.Color.parseColor(id.tertiaryColor))))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCatalogCard(text: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(24.dp).fillMaxWidth()
        )
    }
}


// --- 3. PROFILE SCREEN ---
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentProfile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    val hasApiKey = BuildConfig.GEMINI_API_KEY.isNotEmpty() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        Text(
            text = "Profile & Settings",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Manage your global workspace configuration and credentials.",
            fontSize = 13.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // State for Edit Dialog
        var showEditDialog by remember { mutableStateOf(false) }

        if (showEditDialog) {
            var tempOwnerName by remember { mutableStateOf(currentProfile?.userName ?: "Bharat Soni") }
            var tempName by remember { mutableStateOf(currentProfile?.name ?: "") }
            var tempWebsiteName by remember { mutableStateOf(currentProfile?.websiteName ?: "") }
            var tempPhoneNumber by remember { mutableStateOf(currentProfile?.phoneNumber ?: "9352919258") }
            var tempEmailAddress by remember { mutableStateOf(currentProfile?.emailAddress ?: "bharatsoni8671@gmail.com") }
            var tempCategory by remember { mutableStateOf(currentProfile?.category ?: "") }

            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = {
                    Text(
                        text = "Edit Profile Details",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = tempOwnerName,
                            onValueChange = { tempOwnerName = it },
                            label = { Text("Owner's Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            label = { Text("Workspace / Business Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tempWebsiteName,
                            onValueChange = { tempWebsiteName = it },
                            label = { Text("Website Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tempPhoneNumber,
                            onValueChange = { tempPhoneNumber = it },
                            label = { Text("Contact Number") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tempEmailAddress,
                            onValueChange = { tempEmailAddress = it },
                            label = { Text("Email Address") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = tempCategory,
                            onValueChange = { tempCategory = it },
                            label = { Text("Business Sector") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val original = currentProfile ?: com.example.data.BusinessProfile(
                                id = 1,
                                name = tempName,
                                category = tempCategory,
                                userName = tempOwnerName,
                                phoneNumber = tempPhoneNumber,
                                emailAddress = tempEmailAddress,
                                websiteName = tempWebsiteName
                            )
                            val updated = original.copy(
                                userName = tempOwnerName,
                                name = tempName,
                                websiteName = tempWebsiteName,
                                phoneNumber = tempPhoneNumber,
                                emailAddress = tempEmailAddress,
                                category = tempCategory
                            )
                            viewModel.saveProfile(updated)
                            showEditDialog = false
                            Toast.makeText(context, "Profile details updated successfully!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Business Profile Summary card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Business Information",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                ProfileDetailRow(label = "Owner Name", value = currentProfile?.userName ?: "Bharat Soni")
                ProfileDetailRow(label = "Workspace Name", value = currentProfile?.name ?: "N/A")
                ProfileDetailRow(label = "Website Name", value = currentProfile?.websiteName ?: "N/A")
                ProfileDetailRow(label = "Contact Number", value = currentProfile?.phoneNumber ?: "9352919258")
                ProfileDetailRow(label = "Email Address", value = currentProfile?.emailAddress ?: "bharatsoni8671@gmail.com")
                ProfileDetailRow(label = "Business Sector", value = currentProfile?.category ?: "N/A")

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showEditDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.EditCalendar, contentDescription = "Edit Profile", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile Settings", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // API Status Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Engine Credentials",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Gemini API Connection", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (hasApiKey) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFBA1A1A).copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (hasApiKey) "Active (API)" else "Local Simulation",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (hasApiKey) Color(0xFF10B981) else Color(0xFFBA1A1A)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "If no API key is specified, BizGenie AI seamlessly switches to localized off-grid models to protect performance and cost.",
                    fontSize = 12.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subscription & Free Trial Simulation Card
        val loggedInMethod by viewModel.loggedInMethod.collectAsState()
        val userIdentifier by viewModel.userIdentifier.collectAsState()
        val isAutoPaymentSetup by viewModel.isAutoPaymentSetup.collectAsState()
        val isSubscribed by viewModel.isSubscribed.collectAsState()
        val trialDaysElapsed by viewModel.trialDaysElapsed.collectAsState()

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Subscription & Trial Management",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Auth Session", fontSize = 13.sp, color = Color(0xFF777683))
                    Text(text = "$loggedInMethod ($userIdentifier)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Auto-Payment (₹1 Refundable)", fontSize = 13.sp, color = Color(0xFF777683))
                    Text(
                        text = if (isAutoPaymentSetup) "Authorized (bharatsoni8671-1@okhdfcbank)" else "Pending Setup",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isAutoPaymentSetup) Color(0xFF10B981) else Color(0xFFBA1A1A)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Subscription Fee (₹200)", fontSize = 13.sp, color = Color(0xFF777683))
                    Text(
                        text = if (isSubscribed) "Paid & Unlocked" else if (trialDaysElapsed < 7) "Trial Period Active" else "Payment Required",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (isSubscribed || trialDaysElapsed < 7) Color(0xFF10B981) else Color(0xFFBA1A1A)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Trial Duration", fontSize = 13.sp, color = Color(0xFF777683))
                    Text(
                        text = "Day $trialDaysElapsed of 7",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (trialDaysElapsed < 7) Color(0xFF6750A4) else Color(0xFFBA1A1A)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Simulation Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            viewModel.incrementTrialDays()
                            val nextDay = trialDaysElapsed + 1
                            if (nextDay >= 7) {
                                Toast.makeText(context, "Trial expired! App will route to paywall shortly.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Trial advanced to Day $nextDay!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Day +1", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            viewModel.resetTrialDays()
                            viewModel.setSubscribed(false)
                            Toast.makeText(context, "Trial reset to Day 0!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reset Trial", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy Policy & Data Collection Info Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Privacy Policy",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Privacy Policy & Security",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Your trust is our absolute priority. Here is a clear summary of how BizGenie AI protects your information:",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                val policies = listOf(
                    "🔒  Data Protection" to "All enterprise workspaces, generated web assets, and posters are saved locally in private sandbox storage. Your data is owned entirely by you.",
                    "💳  Billing Transparency" to "Subscriptions cost ₹200/month after trial expiration. Additional fees of ₹200 are billed clearly with zero hidden or surprise charges.",
                    "📲  System Permissions" to "SMS permission (SEND_SMS) is utilized exclusively to securely send authentication OTP codes directly to your device. No private logs are accessed.",
                    "🚫  No Ads or Trackers" to "This application contains zero advertisement networks, third-party analytics SDKs, or background tracking mechanisms. Your operations remain confidential."
                )

                policies.forEach { (title, desc) ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF21005D)
                        )
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(start = 18.dp, top = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Reset workspace button
        Button(
            onClick = {
                viewModel.resetAllData()
                Toast.makeText(context, "Workspace successfully reset!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("reset_workspace_button")
        ) {
            Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Reset")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Reset & Clear Workspace", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = label, fontSize = 12.sp, color = Color(0xFF777683))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFEADDFF).copy(alpha = 0.5f)))
    }
}
