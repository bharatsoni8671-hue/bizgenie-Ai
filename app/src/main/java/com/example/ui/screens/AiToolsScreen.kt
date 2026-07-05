package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.BrandIdentity
import com.example.data.GeneratedWebsite
import com.example.data.MarketingPost
import com.example.ui.MainViewModel
import com.example.ui.UiState
import kotlinx.coroutines.delay

@Composable
fun AiToolsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var activeTool by remember { mutableStateOf(0) } // 0: Website, 1: Marketing, 2: Brand Identity

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 96.dp)
    ) {
        // Hero header section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), Color.Transparent)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    text = "BizGenie AI Tools",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Indian entrepreneur's premium growth engine.",
                    fontSize = 14.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Horizontal Tool Tabs - Editorial High-Contrast Design
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF7F2FA))
                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tools = listOf("Website", "Marketing", "Brand Kit")
            tools.forEachIndexed { index, title ->
                val selected = activeTool == index
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) Color(0xFF21005D) else Color.Transparent)
                        .clickable { activeTool = index }
                        .testTag("tool_tab_$index")
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) Color.White else Color(0xFF6750A4)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tool Content Renderers
        when (activeTool) {
            0 -> WebsiteGeneratorTool(viewModel)
            1 -> MarketingSocialTool(viewModel)
            2 -> BrandIdentityTool(viewModel)
        }
    }
}

// 1. AI WEBSITE GENERATOR TOOL
@Composable
fun WebsiteGeneratorTool(viewModel: MainViewModel) {
    val genState by viewModel.websiteGenState.collectAsState()
    val context = LocalContext.current
    val isSubscribed by viewModel.isSubscribed.collectAsState()
    var showSubscribeDialog by remember { mutableStateOf(false) }

    if (!isSubscribed) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Feature Locked",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Website Builder is Locked",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "AI Website Generator is a premium feature. Your 7-day free trial gives you full access to Brand Kits and Marketing posts, but hosting and publishing websites requires an active subscription.",
                        fontSize = 14.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { showSubscribeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("subscribe_to_unlock_website_button")
                    ) {
                        Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = "Subscribe", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Subscribe to Unlock (₹200/month)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        if (showSubscribeDialog) {
            UpiPaymentSimulationDialog(
                amount = 200.0,
                upiId = "bharatsoni8671-1@okhdfcbank",
                onDismiss = { showSubscribeDialog = false },
                onPaymentSuccess = {
                    viewModel.setSubscribed(true)
                    showSubscribeDialog = false
                    Toast.makeText(context, "Subscription activated! Website Builder is now unlocked.", Toast.LENGTH_LONG).show()
                }
            )
        }
        return
    }

    var isPublished by remember { mutableStateOf(false) }
    var showLiveBrowserDialog by remember { mutableStateOf(false) }

    // Templates definition
    val templates = listOf(
        TemplateCardItem(
            name = "Nexus Portfolio",
            desc = "Modern, clean, and centered on visuals. Best for creative agencies.",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAnsbjR9VJPikNprIcv4nCOLnftWZbrlAqL_E5c6q0iR052NcwpXyzXQgKeWmfn6ufy5Xhaabb-Tbd6nfKueBvkHUI-62RONLiGNYIiPAXBmDLY_3nnlOVTPOEPqRTgQuQLvldiG5eHYrFKZapN7C9pJEP4ZecVgH0Y5-0rHXQ3qcifdJJNhH0byS75Z_H9BWWKjBPQvkPEkvR8h_hLMNV2br0zpbIlMBLaC0L9d0GKLw1t31Qc_wFNhFNZaQG8NmTtvEm0BPuYk0s"
        ),
        TemplateCardItem(
            name = "Commerce Pro",
            desc = "Optimized for conversion. Includes inventory and payment features.",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAtD1pM46s2oPRFcWDyt8uadZw_c17HQz0AS-Tewap4MLNaxMI1Vs8piyJw0J2fiH4kSJ4p4FmTB5tnlCrCSDUAYERnwoyaISSjkExGkg21TR0JrZCqOmUrNW7mioT7Yn_3CpyViYXBXuDFkFqvkhNgTHwnMhuRsPi1U5EKcWz1jhQju9Sng0gyWz3s6LnfA0CrZ2P3CTMRkvqReN0d3jHdtwXkADjO6P6QhbFLoPfYZkE_3gOzCIveWSdynFkblIbKT0niJX-tnwg"
        ),
        TemplateCardItem(
            name = "Enterprise Hub",
            desc = "Authority and trust. Best for consulting and professional services.",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBVTyqzjEaCzVIhw-GkI6PG-kY11AOuf_Bs5HEVn2j2U7yds7N--QscA8reO5Ui56-YPLNuukOwEI7CWBlZqAXe9UYC4HcHyNjCJGKWa5A73mgcF4PzF6plBDFYS6rO-JzE5LmNNjaaZU5TdyQEOriFBPwHaWU6mdkFqO8KrTBgCaXbj_4E1P9Te59tlbHCg6Ig4ERlxn_FvawTyJ4eggx_waW917YIvGeJ_yICuVI9KyM7NzhIOX6mKpVFxtYM2VldkxpGqYcwdIY"
        ),
        TemplateCardItem(
            name = "Service Bloom",
            desc = "Welcoming and approachable. Perfect for local restaurants and spas.",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBMyVjDdy3VELFBIrBCuTNEvffzXq-qGG6rZu9kE4tdX9X0kBT-9pzEVnHjBqOz4XCZJrM4EKE5TiNLEI7X_FKauqjLLIEcxBDXwL6xgLqPY0VtHISnbFv8CIwaDTRhuDk5yFnh8MilsuQed5Oe7Npd4_7qk6rxVnhtQDv4p0SRgR_r46Ws2YVE-JYm82hMX0OuuxNi9x9sSlLKI0vSNnccud3Qa9XE9AKgxQvJn7VxA_0np_u67IPwEUu5wy1scXGZ21hwizG7yE4"
        )
    )

    when (genState) {
        is UiState.Idle -> {
            // STEP 1: CHOOSE CANVAS
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Choose your digital canvas",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Our AI adapts these structures to perfectly match your brand's unique identity.",
                    fontSize = 14.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                templates.forEach { temp ->
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
                        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable { viewModel.generateWebsite(temp.name) }
                            .testTag("template_${temp.name.replace(" ", "_")}")
                    ) {
                        Column {
                            AsyncImage(
                                model = temp.imageUrl,
                                contentDescription = temp.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = temp.name,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = temp.desc,
                                    fontSize = 13.sp,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
        is UiState.Loading -> {
            // STEP 2: GENERATION SCREEN
            var progress by remember { mutableStateOf(0.1f) }
            var currentStatus by remember { mutableStateOf("Connecting to AI nodes...") }

            LaunchedEffect(Unit) {
                val statuses = listOf(
                    "Analyzing business context...",
                    "Drafting unique business copy...",
                    "Curating professional assets...",
                    "Generating responsive layout blocks...",
                    "Polishing UI elements..."
                )
                for (i in 0 until statuses.size) {
                    currentStatus = statuses[i]
                    progress = (i + 1).toFloat() / statuses.size
                    delay(800)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color(0xFF21005D))
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Stars",
                        tint = Color(0xFFFFB4AB),
                        modifier = Modifier
                            .size(48.dp)
                            .rotate(30f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "BizGenie AI is crafting your masterpiece...",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Progress Arc Indicator - Editorial styling
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(140.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(130.dp),
                            color = Color(0xFFFFB4AB),
                            strokeWidth = 8.dp,
                            trackColor = Color.White.copy(alpha = 0.2f),
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Complete",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = currentStatus,
                        fontSize = 14.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFFFFB4AB),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        is UiState.Success -> {
            val website = (genState as UiState.Success<GeneratedWebsite>).data
            var showQuickEditDialog by remember { mutableStateOf(false) }

            if (showQuickEditDialog) {
                var editBusinessName by remember { mutableStateOf(website.businessName) }
                var editHeroTitle by remember { mutableStateOf(website.heroTitle) }
                var editHeroDescription by remember { mutableStateOf(website.heroDescription) }
                var editButtonText by remember { mutableStateOf(website.buttonText) }

                AlertDialog(
                    onDismissRequest = { showQuickEditDialog = false },
                    title = {
                        Text(
                            text = "Quick Edit Website Content",
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
                            Text(
                                text = "Edit your business name, headings, descriptions, and CTA button labels directly.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            OutlinedTextField(
                                value = editBusinessName,
                                onValueChange = { editBusinessName = it },
                                label = { Text("Business / Owner Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = editHeroTitle,
                                onValueChange = { editHeroTitle = it },
                                label = { Text("Hero Title") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = editHeroDescription,
                                onValueChange = { editHeroDescription = it },
                                label = { Text("Hero Description") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = editButtonText,
                                onValueChange = { editButtonText = it },
                                label = { Text("Button CTA Text") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val updatedWebsite = website.copy(
                                    businessName = editBusinessName,
                                    heroTitle = editHeroTitle,
                                    heroDescription = editHeroDescription,
                                    buttonText = editButtonText
                                )
                                viewModel.updateGeneratedWebsite(updatedWebsite)
                                showQuickEditDialog = false
                                Toast.makeText(context, "Website updated successfully!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Save Changes", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showQuickEditDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // STEP 3: PREVIEW SCREEN
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Preview Your Site",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Generated exclusively for your business.",
                            fontSize = 13.sp,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = { viewModel.resetWebsiteState() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Regenerate")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Browser Mockup
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFC7C5D4).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Browser top bar
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFE6E8EA))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFBA1A1A)))
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF8455EF)))
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4EDEA3)))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .border(0.5.dp, Color(0xFFC7C5D4).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "SSL",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "www.${website.businessName.lowercase().replace(" ", "")}.bizgenie.ai",
                                        fontSize = 11.sp,
                                        color = Color(0xFF464652),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Browser Landing Content
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Navbar
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = website.businessName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(android.graphics.Color.parseColor(website.accentColor))
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFFFBFF))
                                        .border(1.dp, Color(0xFF15157D), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Contact",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15157D)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Hero Section
                            Text(
                                text = website.heroTitle,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF191C1E),
                                lineHeight = 28.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = website.heroDescription,
                                fontSize = 13.sp,
                                color = Color(0xFF464652),
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action button with customized dynamic color
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Website Button Clicked", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(android.graphics.Color.parseColor(website.accentColor))
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = website.buttonText, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Card Illustration
                            AsyncImage(
                                model = website.imageUrl,
                                contentDescription = "Website Banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                val liveUrl = "https://bizgenie.ai/site/${website.businessName.lowercase().replace(" ", "")}"

                if (isPublished) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD4F7E6)),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Published Logo",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Your website is live & public!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF065F46)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = liveUrl,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF047857),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                    .border(1.dp, Color(0xFF10B981).copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        val clipboardManager = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        val clipData = android.content.ClipData.newPlainText("Website URL", liveUrl)
                                        clipboardManager.setPrimaryClip(clipData)
                                        Toast.makeText(context, "URL copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(8.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showLiveBrowserDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "Open", tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Open Live Website", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // Bottom CTA buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = {
                            showQuickEditDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quick Edit")
                    }

                    Button(
                        onClick = {
                            isPublished = true
                            Toast.makeText(context, "Website successfully published to public URL!", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPublished) Color.Gray else Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = "Publish")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isPublished) "Published" else "Publish Now")
                    }
                }
            }
        }
        is UiState.Error -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Text(text = (genState as UiState.Error).message, color = Color.Red)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.resetWebsiteState() }) {
                    Text("Retry")
                }
            }
        }
    }

    if (showLiveBrowserDialog) {
        val website = (genState as UiState.Success<GeneratedWebsite>).data
        val liveUrl = "https://bizgenie.ai/site/${website.businessName.lowercase().replace(" ", "")}"

        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showLiveBrowserDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF3F4F6))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Browser Top Bar Mockup
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .border(BorderStroke(0.5.dp, Color.LightGray))
                    ) {
                        IconButton(onClick = { showLiveBrowserDialog = false }, modifier = Modifier.size(36.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Browser Address Field
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF3F4F6))
                                .padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "SSL Secure",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = liveUrl,
                                fontSize = 12.sp,
                                color = Color(0xFF374151),
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(onClick = { Toast.makeText(context, "Refreshed live site", Toast.LENGTH_SHORT).show() }, modifier = Modifier.size(36.dp)) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.Gray)
                        }
                    }

                    // Standalone Website Public View
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                    ) {
                        // Logo / Header
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = website.businessName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(android.graphics.Color.parseColor(website.accentColor))
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE5E7EB))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Contact",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF374151)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Hero Headline
                        Text(
                            text = website.heroTitle,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF111827),
                            lineHeight = 34.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Hero Subtitle
                        Text(
                            text = website.heroDescription,
                            fontSize = 15.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Main Call-To-Action Button
                        Button(
                            onClick = { Toast.makeText(context, "Action: ${website.buttonText}", Toast.LENGTH_LONG).show() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(android.graphics.Color.parseColor(website.accentColor))),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = website.buttonText, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Hero Media Banner
                        AsyncImage(
                            model = website.imageUrl,
                            contentDescription = "Live Website Banner",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        // Footer
                        Divider(color = Color(0xFFE5E7EB))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "© 2026 ${website.businessName}. Hosted securely via BizGenie AI.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// 2. SOCIAL MEDIA & MARKETING COPILOT
@Composable
fun MarketingSocialTool(viewModel: MainViewModel) {
    val genState by viewModel.postGenState.collectAsState()
    val currentProfile by viewModel.profile.collectAsState()
    val context = LocalContext.current

    var selectedType by remember { mutableStateOf("Festival Post") } // "Festival Post" or "Product Promo"
    var promotePrompt by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Marketing & Social Media",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Create professional-grade promotional content and greetings in seconds.",
            fontSize = 13.sp,
            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content Type selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF7F2FA))
                .border(1.dp, Color(0xFFCAC4D0), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            val types = listOf("Festival Post", "Product Promo")
            types.forEach { type ->
                val active = selectedType == type
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (active) Color(0xFF21005D) else Color.Transparent)
                        .clickable { selectedType = type }
                ) {
                    Text(
                        text = type,
                        fontSize = 13.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        color = if (active) Color.White else Color(0xFF6750A4)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Prompt input form
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "What are you promoting?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = promotePrompt,
                    onValueChange = { promotePrompt = it },
                    placeholder = {
                        Text(
                            text = if (selectedType == "Festival Post") {
                                "e.g. Special Diwali sale for our hand-crafted jewelry collection. 20% off for first 50 customers."
                            } else {
                                "e.g. Launching our fresh organic milk delivery service. Sourced directly from local farms."
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("marketing_prompt_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF21005D),
                        unfocusedBorderColor = Color(0xFFCAC4D0),
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Styling Chip tags
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFF2F4F6))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Language, contentDescription = "Lang", modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("English & Hindi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFFF2F4F6))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Style, contentDescription = "Style", modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Vibrant Style", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Generate Button
                Button(
                    onClick = {
                        viewModel.generatePost(selectedType, promotePrompt)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21005D)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("generate_post_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Stars")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Magic Post", fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Tip Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Tip",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "AI Tip",
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Mention your shop name or city for a localized touch in your festival greetings!",
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Output Section (Dynamic based on states)
        AnimatedVisibility(
            visible = genState !is UiState.Idle,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            when (genState) {
                is UiState.Loading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        CircularProgressIndicator(color = Color(0xFF6B38D4))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "BizGenie AI is composing your post...",
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF464652)
                        )
                    }
                }
                is UiState.Success -> {
                    val post = (genState as UiState.Success<MarketingPost>).data
                    val bName = currentProfile?.name ?: "your_business_name"

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Social Media Preview",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191C1E)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Instagram Mockup
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFC7C5D4).copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                // Profile Header
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFF6B38D4), Color(0xFF2E3192))
                                                )
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = bName.lowercase().replace(" ", "_"),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF191C1E)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.MoreHoriz,
                                        contentDescription = "Options",
                                        tint = Color(0xFF464652)
                                    )
                                }

                                // Generated Image with overlay badge
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(280.dp)
                                ) {
                                    AsyncImage(
                                        model = post.imageUrl,
                                        contentDescription = "Post Background",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    // Glassmorphic translucent text card
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.85f))
                                            .border(0.5.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = if (post.contentType == "Festival Post") "HAPPY DIWALI SALE" else "SPECIAL LIMITED LAUNCH",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color(0xFF15157D),
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                text = "20% OFF ALL ORDERS",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 11.sp,
                                                color = Color(0xFF10B981),
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                // Actions row (Heart, comment, share, bookmark)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Like", modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Icon(imageVector = Icons.Default.ChatBubbleOutline, contentDescription = "Comment", modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = "Save", modifier = Modifier.size(24.dp))
                                }

                                // Caption & tags
                                Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 16.dp)) {
                                    Text(
                                        text = "${bName.lowercase().replace(" ", "_")} Sparkle brighter this Diwali! ✨",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF191C1E)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = post.caption,
                                        fontSize = 12.sp,
                                        color = Color(0xFF464652),
                                        lineHeight = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = post.hashtags,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E3192)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Controls
                        Button(
                            onClick = {
                                Toast.makeText(context, "Opening WhatsApp to share...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share on WhatsApp", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Saved to device storage", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = "Download")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download")
                            }

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Opening Image Design Studio...", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFECEEF0)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF464652))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Design", color = Color(0xFF464652))
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Text(text = (genState as UiState.Error).message, color = Color.Red)
                }
                else -> {}
            }
        }
    }
}

// 3. AI BRAND IDENTITY GENERATOR TOOL
@Composable
fun BrandIdentityTool(viewModel: MainViewModel) {
    val genState by viewModel.identityGenState.collectAsState()
    val context = LocalContext.current

    var selectedStyle by remember { mutableStateOf("Minimal") } // "Minimal", "Bold", "Traditional"

    // Custom design configurations
    val currentPalette = when (selectedStyle) {
        "Minimal" -> listOf(Color(0xFF15157D), Color(0xFF6B38D4), Color(0xFF4EDEA3), Color(0xFFE0E3E5))
        "Bold" -> listOf(Color(0xFF2E3192), Color(0xFF8455EF), Color(0xFFFFFBFF), Color(0xFF191C1E))
        "Traditional" -> listOf(Color(0xFF002F1E), Color(0xFF22C087), Color(0xFF004830), Color(0xFF6FFBCE))
        else -> listOf(Color(0xFF191C1E), Color(0xFF777683), Color(0xFFECEEF0), Color(0xFFC7C5D4))
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "Your AI Brand Identity",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Curated logo options and visual guidelines.",
                    fontSize = 13.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { viewModel.generateBrandIdentity(selectedStyle) }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Regenerate")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Left Row: Edit Style
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Edit Style",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                val styles = listOf("Minimal", "Bold", "Traditional")
                styles.forEach { style ->
                    val active = selectedStyle == style
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) Color(0xFF21005D).copy(alpha = 0.1f) else Color.Transparent)
                            .border(
                                1.dp,
                                if (active) Color(0xFF21005D) else Color(0xFFCAC4D0),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedStyle = style
                                viewModel.generateBrandIdentity(style)
                            }
                            .padding(12.dp)
                    ) {
                        Text(
                            text = style,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (active) Color(0xFF21005D) else Color(0xFF6750A4)
                        )
                        if (active) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Checked",
                                tint = Color(0xFF21005D),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Brand Kit section
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
            border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Brand Kit",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "PRIMARY PALETTE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Color Blocks
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    currentPalette.forEach { color ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(color)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "TYPOGRAPHY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Serif Display", fontFamily = androidx.compose.ui.text.font.FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = "Headers", fontSize = 12.sp, color = Color(0xFF777683))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Inter Regular", fontSize = 14.sp, color = Color(0xFF191C1E))
                    Text(text = "Body", fontSize = 12.sp, color = Color(0xFF777683))
                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Brand kit assets downloaded!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Download Brand Assets")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Concepts Output Grid
        when (genState) {
            is UiState.Loading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    CircularProgressIndicator(color = Color(0xFF21005D))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("BizGenie AI is designing custom logos...", fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif)
                }
            }
            is UiState.Success -> {
                val identity = (genState as UiState.Success<BrandIdentity>).data
                LogoConceptCard(identity)
            }
            else -> {
                // Pre-populated Concept card based on style selection
                val staticConcept = BrandIdentity(
                    styleName = selectedStyle,
                    primaryColor = "#15157D",
                    secondaryColor = "#6B38D4",
                    tertiaryColor = "#4EDEA3",
                    logoConceptName = "Concept A: Tech Modern",
                    logoImageUrl = when (selectedStyle) {
                        "Minimal" -> "https://lh3.googleusercontent.com/aida-public/AB6AXuD1fyyMkxR9je-YsJn1Cj_e1XakZVXrvNrqU0ueYu4_0HhWjcmdAXnxQcpLmMb9v-CczQjQ-S8cFgJHBLblfqX5BcTZtXnunmmAbjNtMVhp6gCRfKJT4QAyf_z7lQYb8fceNhBu1fJAqfPscwycCLYVXbobzBJnGjVckQ_YkTItgSZP61HLdVltf17CAG7ldiRC0WL8asGTPdOi7TUDjsusp5Z8QMFYFQtvb903spZ078HfcIZHg25R_8ykY2nq3ga6f1StI2-Lehc"
                        "Bold" -> "https://lh3.googleusercontent.com/aida-public/AB6AXuAbRsSKtWnQejQkkihwx72-K5B0LRKEt9_qkfVLP8SjtIFlG2NEqhbl-B5GxxvNaHH4yXzpeO93ToK9EZoi_8bJlrasB9a5D9l9eEqSD_GMHvuOJIqm55a3aW1HczP-6vYV9u4unyPAthFf4ArT0YWudVy7WXBLGnAx0QJxPo-4CUqXQEMeEKciFYwIRw6SfgIr8lZwRKI3lQ8N3TJbiuLYzdokBCz1d-T14hUraJ8KFMTlNkPsv1Mz0UVGqChQAvBVdXjp-UTai8Y"
                        else -> "https://lh3.googleusercontent.com/aida-public/AB6AXuDNePeFYCYnHr5azPQTVUIEwP2Bx2RyY9PaUPfEAMkibHa3GolAGmfjL1fV0Xejq5nyZPjB_FTJhteDOLTIFOnY4YbCY6HgUEB3ZJbRVlK-7kgtE2CHTrj6_VP5aCVY5hU7sn9oY3OKfKQ5dsTV7VzMlGasIJXOLIIlZxzYTZUQaAVULS1h_sx2-9LhmKQo2fc4XrjI-u9zKwyZlhtpJp7gZalK8Iow46EVPtY7YFK0IEKoznH3gw_iJndbb-nHEznq5HKT0M0QEuQ"
                    }
                )
                LogoConceptCard(staticConcept)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom Feedback banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(40.dp))
                .background(Color(0xFF21005D))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Not finding the perfect fit?",
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Refine the AI results by giving us more specific details about your business values, or let the AI analyze your industry competitors.",
                    fontSize = 12.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                    color = Color.White.copy(alpha = 0.8f),
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Analyzing local competitors...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB4AB)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.5f)
                    ) {
                        Text("Analyze Competitors", color = Color(0xFF690005), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Opening Custom Prompt studio...", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Custom Prompt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Subcomponent: Logo concept display card
@Composable
fun LogoConceptCard(identity: BrandIdentity) {
    val context = LocalContext.current
    var isLiked by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2FA)),
        border = BorderStroke(1.dp, Color(0xFFCAC4D0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFF3EDF7))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = identity.logoImageUrl,
                    contentDescription = identity.logoConceptName,
                    modifier = Modifier.size(160.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = identity.logoConceptName,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Primary Style: ${identity.styleName}",
                        fontSize = 12.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { isLiked = !isLiked }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color.Red else Color(0xFF777683)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            Toast.makeText(context, "Logo HD downloaded successfully!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Download", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("HD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Helper data class for templates
data class TemplateCardItem(
    val name: String,
    val desc: String,
    val imageUrl: String
)
