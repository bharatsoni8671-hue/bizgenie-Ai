package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: Int = 1,
    val name: String, // Workspace/Business Name
    val category: String, // Sector
    val isOnboarded: Boolean = false,
    val userName: String = "Bharat Soni",
    val phoneNumber: String = "9352919258",
    val emailAddress: String = "bharatsoni8671@gmail.com",
    val websiteName: String = "www.bizgenie.ai"
)

@Entity(tableName = "generated_website")
data class GeneratedWebsite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val businessName: String,
    val businessCategory: String,
    val templateName: String,
    val heroTitle: String,
    val heroDescription: String,
    val buttonText: String,
    val accentColor: String,
    val imageUrl: String
)

@Entity(tableName = "marketing_post")
data class MarketingPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contentType: String, // "Festival Post" or "Product Promo"
    val prompt: String,
    val caption: String,
    val hashtags: String, // comma-separated
    val imageUrl: String
)

@Entity(tableName = "brand_identity")
data class BrandIdentity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val styleName: String, // "Minimal", "Bold", "Traditional"
    val primaryColor: String,
    val secondaryColor: String,
    val tertiaryColor: String,
    val logoConceptName: String,
    val logoImageUrl: String
)
