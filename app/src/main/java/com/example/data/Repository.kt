package com.example.data

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Locale

class Repository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val profileDao = db.businessProfileDao()
    private val websiteDao = db.generatedWebsiteDao()
    private val postDao = db.marketingPostDao()
    private val identityDao = db.brandIdentityDao()

    // Flow API
    val profileFlow: Flow<BusinessProfile?> = profileDao.getProfileFlow()
    val websitesFlow: Flow<List<GeneratedWebsite>> = websiteDao.getAllWebsitesFlow()
    val postsFlow: Flow<List<MarketingPost>> = postDao.getAllPostsFlow()
    val identitiesFlow: Flow<List<BrandIdentity>> = identityDao.getAllIdentitiesFlow()

    suspend fun getProfile(): BusinessProfile? = withContext(Dispatchers.IO) {
        profileDao.getProfile()
    }

    suspend fun saveProfile(profile: BusinessProfile) = withContext(Dispatchers.IO) {
        profileDao.saveProfile(profile)
    }

    suspend fun insertWebsite(website: GeneratedWebsite) = withContext(Dispatchers.IO) {
        websiteDao.insert(website)
    }

    suspend fun insertPost(post: MarketingPost) = withContext(Dispatchers.IO) {
        postDao.insert(post)
    }

    suspend fun insertIdentity(identity: BrandIdentity) = withContext(Dispatchers.IO) {
        identityDao.insert(identity)
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        profileDao.clear()
        websiteDao.clear()
        postDao.clear()
        identityDao.clear()
    }

    // AI website generator
    suspend fun generateWebsite(
        businessName: String,
        businessCategory: String,
        templateName: String
    ): GeneratedWebsite = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasApiKey = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        if (hasApiKey) {
            val prompt = """
                Generate a professional website landing page data for a business.
                Business Name: "$businessName"
                Category: "$businessCategory"
                Selected Template Theme Style: "$templateName"
                
                Respond ONLY with a valid JSON object. Do not include markdown formatting or backticks outside of the JSON block itself.
                The JSON must contain these exactly:
                {
                  "heroTitle": "Catchy headline centered around the business",
                  "heroDescription": "Short but highly persuasive description explaining their values and services",
                  "buttonText": "An actionable button text (e.g. 'Explore Catalog', 'Book Consultation')",
                  "accentColor": "A hex color code that fits this business (e.g. '#2E3192')"
                }
            """.trimIndent()

            try {
                val reqBody = RetrofitClient.createRequestBody(prompt)
                val responseBody = RetrofitClient.service.generateContent(apiKey, reqBody)
                val rawResponse = responseBody.string()
                val responseText = RetrofitClient.extractTextFromResponse(rawResponse)
                if (responseText != null) {
                    val cleanedJson = extractJson(responseText)
                    val json = JSONObject(cleanedJson)
                    val title = json.optString("heroTitle", "Transforming Your Business Future.")
                    val desc = json.optString("heroDescription", "Expert solutions tailored to elevate your presence in the modern digital marketplace.")
                    val btn = json.optString("buttonText", "Get Started")
                    val color = json.optString("accentColor", "#2E3192")
                    
                    return@withContext GeneratedWebsite(
                        businessName = businessName,
                        businessCategory = businessCategory,
                        templateName = templateName,
                        heroTitle = title,
                        heroDescription = desc,
                        buttonText = btn,
                        accentColor = color,
                        imageUrl = getImageUrlForCategory(businessCategory)
                    )
                }
            } catch (e: Exception) {
                Log.e("Repository", "Gemini API error, falling back to offline", e)
            }
        }

        // Contextual Fallback
        val fallback = getOfflineWebsite(businessName, businessCategory, templateName)
        return@withContext fallback
    }

    // AI Marketing Post generator
    suspend fun generateMarketingPost(
        businessName: String,
        businessCategory: String,
        contentType: String,
        promptText: String
    ): MarketingPost = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasApiKey = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        if (hasApiKey) {
            val prompt = """
                Generate a social media promotion caption and relevant hashtags.
                Business Name: "$businessName"
                Category: "$businessCategory"
                Content Type Required: "$contentType"
                User Goal Description: "$promptText"
                
                Respond ONLY with a valid JSON object. Do not include markdown formatting or backticks outside of the JSON block itself.
                The JSON must contain these exactly:
                {
                  "caption": "An engaging post caption with emojis relevant to Indian businesses and the specified category",
                  "hashtags": "A space separated list of 4-6 smart hashtags starting with # (e.g. '#Diwali2024 #SmallBusiness')"
                }
            """.trimIndent()

            try {
                val reqBody = RetrofitClient.createRequestBody(prompt)
                val responseBody = RetrofitClient.service.generateContent(apiKey, reqBody)
                val rawResponse = responseBody.string()
                val responseText = RetrofitClient.extractTextFromResponse(rawResponse)
                if (responseText != null) {
                    val cleanedJson = extractJson(responseText)
                    val json = JSONObject(cleanedJson)
                    val caption = json.optString("caption", "Sparkle brighter this festive season! ✨")
                    val hashtags = json.optString("hashtags", "#BizGenieAI #SmallBusiness")
                    
                    return@withContext MarketingPost(
                        contentType = contentType,
                        prompt = promptText,
                        caption = caption,
                        hashtags = hashtags,
                        imageUrl = getMarketingPostImageUrl(contentType, businessCategory)
                    )
                }
            } catch (e: Exception) {
                Log.e("Repository", "Gemini API error, falling back to offline", e)
            }
        }

        // Contextual Fallback
        val fallback = getOfflinePost(businessName, businessCategory, contentType, promptText)
        return@withContext fallback
    }

    // AI Brand Identity Generator
    suspend fun generateBrandIdentity(
        businessName: String,
        businessCategory: String,
        styleName: String
    ): BrandIdentity = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasApiKey = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY"

        if (hasApiKey) {
            val prompt = """
                Generate a custom brand identity layout profile for a business.
                Business Name: "$businessName"
                Category: "$businessCategory"
                Style Required: "$styleName"
                
                Respond ONLY with a valid JSON object. Do not include markdown formatting or backticks outside of the JSON block itself.
                The JSON must contain these exactly:
                {
                  "logoConceptName": "A catchy concept name (e.g. 'Concept A: Tech Modern', 'Concept B: Traditional Touch')",
                  "primaryColor": "A premium primary hex color (e.g. '#15157D')",
                  "secondaryColor": "A secondary hex color (e.g. '#6B38D4')",
                  "tertiaryColor": "A mint green or sharp accent hex color (e.g. '#4EDEA3')"
                }
            """.trimIndent()

            try {
                val reqBody = RetrofitClient.createRequestBody(prompt)
                val responseBody = RetrofitClient.service.generateContent(apiKey, reqBody)
                val rawResponse = responseBody.string()
                val responseText = RetrofitClient.extractTextFromResponse(rawResponse)
                if (responseText != null) {
                    val cleanedJson = extractJson(responseText)
                    val json = JSONObject(cleanedJson)
                    val logoConceptName = json.optString("logoConceptName", "Concept A: Tech Modern")
                    val pColor = json.optString("primaryColor", "#15157D")
                    val sColor = json.optString("secondaryColor", "#6B38D4")
                    val tColor = json.optString("tertiaryColor", "#4EDEA3")
                    
                    return@withContext BrandIdentity(
                        styleName = styleName,
                        primaryColor = pColor,
                        secondaryColor = sColor,
                        tertiaryColor = tColor,
                        logoConceptName = logoConceptName,
                        logoImageUrl = getLogoImageUrl(styleName)
                    )
                }
            } catch (e: Exception) {
                Log.e("Repository", "Gemini API error, falling back to offline", e)
            }
        }

        // Contextual Fallback
        val fallback = getOfflineBrandIdentity(businessName, businessCategory, styleName)
        return@withContext fallback
    }

    // Clean JSON wrapper
    private fun extractJson(raw: String): String {
        var str = raw.trim()
        if (str.startsWith("```json")) {
            str = str.substringAfter("```json").substringBeforeLast("```")
        } else if (str.startsWith("```")) {
            str = str.substringAfter("```").substringBeforeLast("```")
        }
        return str.trim()
    }

    // Static hotlinks mapping as requested
    private fun getImageUrlForCategory(category: String): String {
        // We use the browser website mockup image as a highly professional preview
        return "https://lh3.googleusercontent.com/aida-public/AB6AXuA-aLUGbOIbg9VVfAV8Vkkdykg8i-tvWwYq4WIoubS1rrHx-RoVVHV6pnZqWuwDjpTXdGW4W5_w_RLA1KaiqSkAVbFiX8v4_kM8ag9Be1lzWmjESQGi9K7WAsRfv-VUDEY5bhbuJALQ7UpLp_Bit5eg4eh0NBOHoSMsF-nIJxx0JKSG0ItXlRYD2-IE1k7SmYFtbHntEzeA3r8U3LgV8Er3Cs8h50un3gHwop5nyUFfk704BlO7_eb5Bc1ePCZRqTAxZpeld-rrzhY"
    }

    private fun getMarketingPostImageUrl(contentType: String, category: String): String {
        // Diwali jewelry mockup image represents a beautiful festival/product promo
        return "https://lh3.googleusercontent.com/aida-public/AB6AXuDMLSicDUmsj5yQ6r7UVeFuGphRl-2-7W-1BapAL_dnb1HEE8FVPEJoKEyBMODXUQZIoQgYFEonqJDtGevpr9z2LAiAyWknvt4NthKogcS9l3wwiktT1g7q5iq1rYBw02ACY-52N1z5BjuotiE0A8KB69HrrpKhoKciVj259vY6N5mAYISbL5-DWAaBUR_F2KtWAkL5q9DPHsIgKTAtM8wbroxuzHGCbm206dQ0MCp1GGfe5tedFikGJBNWkABPEntEhhGos6Y6X2s"
    }

    private fun getLogoImageUrl(style: String): String {
        return when (style.lowercase(Locale.getDefault())) {
            "minimal" -> "https://lh3.googleusercontent.com/aida-public/AB6AXuD1fyyMkxR9je-YsJn1Cj_e1XakZVXrvNrqU0ueYu4_0HhWjcmdAXnxQcpLmMb9v-CczQjQ-S8cFgJHBLblfqX5BcTZtXnunmmAbjNtMVhp6gCRfKJT4QAyf_z7lQYb8fceNhBu1fJAqfPscwycCLYVXbobzBJnGjVckQ_YkTItgSZP61HLdVltf17CAG7ldiRC0WL8asGTPdOi7TUDjsusp5Z8QMFYFQtvb903spZ078HfcIZHg25R_8ykY2nq3ga6f1StI2-Lehc"
            "bold" -> "https://lh3.googleusercontent.com/aida-public/AB6AXuAbRsSKtWnQejQkkihwx72-K5B0LRKEt9_qkfVLP8SjtIFlG2NEqhbl-B5GxxvNaHH4yXzpeO93ToK9EZoi_8bJlrasB9a5D9l9eEqSD_GMHvuOJIqm55a3aW1HczP-6vYV9u4unyPAthFf4ArT0YWudVy7WXBLGnAx0QJxPo-4CUqXQEMeEKciFYwIRw6SfgIr8lZwRKI3lQ8N3TJbiuLYzdokBCz1d-T14hUraJ8KFMTlNkPsv1Mz0UVGqChQAvBVdXjp-UTai8Y"
            "traditional" -> "https://lh3.googleusercontent.com/aida-public/AB6AXuDNePeFYCYnHr5azPQTVUIEwP2Bx2RyY9PaUPfEAMkibHa3GolAGmfjL1fV0Xejq5nyZPjB_FTJhteDOLTIFOnY4YbCY6HgUEB3ZJbRVlK-7kgtE2CHTrj6_VP5aCVY5hU7sn9oY3OKfKQ5dsTV7VzMlGasIJXOLIIlZxzYTZUQaAVULS1h_sx2-9LhmKQo2fc4XrjI-u9zKwyZlhtpJp7gZalK8Iow46EVPtY7YFK0IEKoznH3gw_iJndbb-nHEznq5HKT0M0QEuQ"
            else -> "https://lh3.googleusercontent.com/aida-public/AB6AXuBmDFjMST4MCKsIk5AbeMWecC_H8syqSuGAeZn-LxbrqLdI822o9pkYNPjxQ5GCW3gRrAtjrqbCrrwyiC0fR16N2iCOWdrW6hWG04fweJFVAxOAVcCzsFnQYIMtqUC86TK8Gzy-cLDUbhAdGv6VR2rOfCMEv1KoEdBKyZ3Yy344auXpRHtRGsZ-p1NcOKjnYp6PWfR4FdDXt7-mzQ5rXTb2EBsMLEm_1Y-jw6RifxSRHPmHVWx6xItEpL06Q7PQp-dAIDAd1Gh8ve0"
        }
    }

    // Contextual Offline Website fallbacks
    private fun getOfflineWebsite(
        name: String,
        category: String,
        template: String
    ): GeneratedWebsite {
        val (title, desc, btn, color) = when (category) {
            "Retail & Shops" -> tuple(
                "Fresh Deals Daily at $name",
                "Your premium source for premium grocery, retail items, and domestic essentials with lightning-fast local delivery.",
                "Order Online Now",
                "#15157D"
            )
            "Services & Consulting" -> tuple(
                "Strategic Solutions by $name",
                "Unlocking exponential business outcomes for modern startups, services, and growth consultations in India.",
                "Book Consultation",
                "#6B38D4"
            )
            "Food & Beverage" -> tuple(
                "Taste the Magic of $name",
                "Authentic flavors crafted by world-class chefs, served fresh with clean hygiene standards.",
                "Reserve a Table",
                "#10B981"
            )
            "E-commerce" -> tuple(
                "Curated Luxury from $name",
                "Handcrafted luxury and exclusive products designed to celebrate unique style and ultimate comfort.",
                "Shop Festive Sale",
                "#8455EF"
            )
            else -> tuple(
                "Grow Your Brand with $name",
                "Customized corporate and modern solutions designed to scale your business presence globally and locally.",
                "Get Started",
                "#2E3192"
            )
        }

        return GeneratedWebsite(
            businessName = name,
            businessCategory = category,
            templateName = template,
            heroTitle = title,
            heroDescription = desc,
            buttonText = btn,
            accentColor = color,
            imageUrl = getImageUrlForCategory(category)
        )
    }

    // Contextual Offline Post fallbacks
    private fun getOfflinePost(
        name: String,
        category: String,
        contentType: String,
        promptText: String
    ): MarketingPost {
        val userPrompt = if (promptText.isEmpty()) "our premium collection" else promptText
        val (caption, hashtags) = if (contentType == "Festival Post") {
            Pair(
                "Sparkle brighter this festive season with $name! ✨ Our exclusive hand-crafted collection is here. Celebrate the festival of lights with 20% off today. Limited slots available, shop now!",
                "#Diwali2024 #FestiveVibes #HandmadeWithLove #ShopLocal #BizGenieAI"
            )
        } else {
            Pair(
                "Transforming your daily routine with $name! 🚀 Discover why Indian entrepreneurs trust us for $userPrompt. High quality, amazing prices, and top-tier support. Try it today!",
                "#ProductLaunch #Innovation #BusinessGrowth #AIPowered #IndianStartup"
            )
        }

        return MarketingPost(
            contentType = contentType,
            prompt = promptText,
            caption = caption,
            hashtags = hashtags,
            imageUrl = getMarketingPostImageUrl(contentType, category)
        )
    }

    // Contextual Offline Brand Identity fallbacks
    private fun getOfflineBrandIdentity(
        name: String,
        category: String,
        style: String
    ): BrandIdentity {
        val conceptName = when (style.lowercase(Locale.getDefault())) {
            "minimal" -> "Concept A: Tech Modern"
            "bold" -> "Concept B: Bold Inverse"
            "traditional" -> "Concept C: Organic Tech"
            else -> "Concept D: Line Minimal"
        }

        val (p, s, t) = when (style.lowercase(Locale.getDefault())) {
            "minimal" -> Triple("#15157D", "#6B38D4", "#4EDEA3")
            "bold" -> Triple("#2E3192", "#8455EF", "#FFFBFF")
            "traditional" -> Triple("#002F1E", "#22C087", "#004830")
            else -> Triple("#191C1E", "#777683", "#ECEEF0")
        }

        return BrandIdentity(
            styleName = style,
            primaryColor = p,
            secondaryColor = s,
            tertiaryColor = t,
            logoConceptName = conceptName,
            logoImageUrl = getLogoImageUrl(style)
        )
    }

    // Helper tuple
    private fun tuple(t: String, d: String, b: String, c: String) = Quadruple(t, d, b, c)
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
