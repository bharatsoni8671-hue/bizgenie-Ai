package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

data class BusinessMetrics(
    val monthlyRevenue: Double = 145000.0,
    val targetRevenue: Double = 200000.0,
    val previousMonthRevenue: Double = 122400.0,
    val websiteTraffic: Int = 1500,
    val leadsCount: Int = 180,
    val convertedCustomers: Int = 45,
    val expenseMarketing: Double = 15000.0,
    val expenseInventory: Double = 25000.0,
    val expenseRentSalaries: Double = 22000.0,
    val expenseSoftwareAI: Double = 4500.0,
    val expenseMiscellaneous: Double = 3500.0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application)
    private val prefs = application.getSharedPreferences("bizgenie_metrics", android.content.Context.MODE_PRIVATE)

    // Subscription and Auth states
    val isLoggedIn = MutableStateFlow(prefs.getBoolean("isLoggedIn", false))
    val loggedInMethod = MutableStateFlow(prefs.getString("loggedInMethod", "") ?: "")
    val userIdentifier = MutableStateFlow(prefs.getString("userIdentifier", "") ?: "")
    val isAutoPaymentSetup = MutableStateFlow(prefs.getBoolean("isAutoPaymentSetup", false))
    val isSubscribed = MutableStateFlow(prefs.getBoolean("isSubscribed", false))
    val trialDaysElapsed = MutableStateFlow(prefs.getInt("trialDaysElapsed", 0))

    fun loginWithPhone(phoneNumber: String) {
        prefs.edit().apply {
            putBoolean("isLoggedIn", true)
            putString("loggedInMethod", "Phone")
            putString("userIdentifier", phoneNumber)
            apply()
        }
        isLoggedIn.value = true
        loggedInMethod.value = "Phone"
        userIdentifier.value = phoneNumber
    }

    fun loginWithGmail(email: String) {
        prefs.edit().apply {
            putBoolean("isLoggedIn", true)
            putString("loggedInMethod", "Gmail")
            putString("userIdentifier", email)
            apply()
        }
        isLoggedIn.value = true
        loggedInMethod.value = "Gmail"
        userIdentifier.value = email
    }

    fun setAutoPaymentSetup(status: Boolean) {
        prefs.edit().putBoolean("isAutoPaymentSetup", status).apply()
        isAutoPaymentSetup.value = status
    }

    fun setSubscribed(status: Boolean) {
        prefs.edit().putBoolean("isSubscribed", status).apply()
        isSubscribed.value = status
    }

    fun incrementTrialDays() {
        val next = (trialDaysElapsed.value + 1).coerceAtMost(10)
        prefs.edit().putInt("trialDaysElapsed", next).apply()
        trialDaysElapsed.value = next
    }

    fun resetTrialDays() {
        prefs.edit().putInt("trialDaysElapsed", 0).apply()
        trialDaysElapsed.value = 0
    }

    // Data streams from SQLite database
    val profile: StateFlow<BusinessProfile?> = repository.profileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val websites: StateFlow<List<GeneratedWebsite>> = repository.websitesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val posts: StateFlow<List<MarketingPost>> = repository.postsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val identities: StateFlow<List<BrandIdentity>> = repository.identitiesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Centralized Dashboard metrics flow
    private val _metrics = MutableStateFlow(loadMetrics())
    val metrics: StateFlow<BusinessMetrics> = _metrics.asStateFlow()

    private fun loadMetrics(): BusinessMetrics {
        return BusinessMetrics(
            monthlyRevenue = prefs.getFloat("monthlyRevenue", 145000f).toDouble(),
            targetRevenue = prefs.getFloat("targetRevenue", 200000f).toDouble(),
            previousMonthRevenue = prefs.getFloat("previousMonthRevenue", 122400f).toDouble(),
            websiteTraffic = prefs.getInt("websiteTraffic", 1500),
            leadsCount = prefs.getInt("leadsCount", 180),
            convertedCustomers = prefs.getInt("convertedCustomers", 45),
            expenseMarketing = prefs.getFloat("expenseMarketing", 15000f).toDouble(),
            expenseInventory = prefs.getFloat("expenseInventory", 25000f).toDouble(),
            expenseRentSalaries = prefs.getFloat("expenseRentSalaries", 22000f).toDouble(),
            expenseSoftwareAI = prefs.getFloat("expenseSoftwareAI", 4500f).toDouble(),
            expenseMiscellaneous = prefs.getFloat("expenseMiscellaneous", 3500f).toDouble()
        )
    }

    fun updateMetrics(newMetrics: BusinessMetrics) {
        _metrics.value = newMetrics
        prefs.edit().apply {
            putFloat("monthlyRevenue", newMetrics.monthlyRevenue.toFloat())
            putFloat("targetRevenue", newMetrics.targetRevenue.toFloat())
            putFloat("previousMonthRevenue", newMetrics.previousMonthRevenue.toFloat())
            putInt("websiteTraffic", newMetrics.websiteTraffic)
            putInt("leadsCount", newMetrics.leadsCount)
            putInt("convertedCustomers", newMetrics.convertedCustomers)
            putFloat("expenseMarketing", newMetrics.expenseMarketing.toFloat())
            putFloat("expenseInventory", newMetrics.expenseInventory.toFloat())
            putFloat("expenseRentSalaries", newMetrics.expenseRentSalaries.toFloat())
            putFloat("expenseSoftwareAI", newMetrics.expenseSoftwareAI.toFloat())
            putFloat("expenseMiscellaneous", newMetrics.expenseMiscellaneous.toFloat())
            apply()
        }
    }

    // UI generation states
    private val _websiteGenState = MutableStateFlow<UiState<GeneratedWebsite>>(UiState.Idle)
    val websiteGenState: StateFlow<UiState<GeneratedWebsite>> = _websiteGenState.asStateFlow()

    private val _postGenState = MutableStateFlow<UiState<MarketingPost>>(UiState.Idle)
    val postGenState: StateFlow<UiState<MarketingPost>> = _postGenState.asStateFlow()

    private val _identityGenState = MutableStateFlow<UiState<BrandIdentity>>(UiState.Idle)
    val identityGenState: StateFlow<UiState<BrandIdentity>> = _identityGenState.asStateFlow()

    // Save profile from Onboarding screen
    fun onboardBusiness(
        name: String,
        category: String,
        userName: String = "Bharat Soni",
        phoneNumber: String = "9352919258",
        emailAddress: String = "bharatsoni8671@gmail.com",
        websiteName: String = "www.bizgenie.ai"
    ) {
        viewModelScope.launch {
            val prof = BusinessProfile(
                name = name,
                category = category,
                isOnboarded = true,
                userName = userName,
                phoneNumber = phoneNumber,
                emailAddress = emailAddress,
                websiteName = websiteName
            )
            repository.saveProfile(prof)
        }
    }

    fun saveProfile(profile: BusinessProfile) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }

    // Trigger AI website creation
    fun generateWebsite(templateName: String) {
        val currentProfile = profile.value ?: return
        viewModelScope.launch {
            _websiteGenState.value = UiState.Loading
            try {
                val result = repository.generateWebsite(
                    businessName = currentProfile.name,
                    businessCategory = currentProfile.category,
                    templateName = templateName
                )
                repository.insertWebsite(result)
                _websiteGenState.value = UiState.Success(result)
            } catch (e: Exception) {
                _websiteGenState.value = UiState.Error(e.localizedMessage ?: "Generation failed")
            }
        }
    }

    // Trigger AI marketing post creation
    fun generatePost(contentType: String, prompt: String) {
        val currentProfile = profile.value ?: return
        viewModelScope.launch {
            _postGenState.value = UiState.Loading
            try {
                val result = repository.generateMarketingPost(
                    businessName = currentProfile.name,
                    businessCategory = currentProfile.category,
                    contentType = contentType,
                    promptText = prompt
                )
                repository.insertPost(result)
                _postGenState.value = UiState.Success(result)
            } catch (e: Exception) {
                _postGenState.value = UiState.Error(e.localizedMessage ?: "Generation failed")
            }
        }
    }

    // Trigger AI brand identity design
    fun generateBrandIdentity(styleName: String) {
        val currentProfile = profile.value ?: return
        viewModelScope.launch {
            _identityGenState.value = UiState.Loading
            try {
                val result = repository.generateBrandIdentity(
                    businessName = currentProfile.name,
                    businessCategory = currentProfile.category,
                    styleName = styleName
                )
                repository.insertIdentity(result)
                _identityGenState.value = UiState.Success(result)
            } catch (e: Exception) {
                _identityGenState.value = UiState.Error(e.localizedMessage ?: "Generation failed")
            }
        }
    }

    // Reset generation states to Idle
    fun resetWebsiteState() { _websiteGenState.value = UiState.Idle }
    fun resetPostState() { _postGenState.value = UiState.Idle }
    fun resetIdentityState() { _identityGenState.value = UiState.Idle }

    fun updateGeneratedWebsite(website: GeneratedWebsite) {
        viewModelScope.launch {
            repository.insertWebsite(website)
            _websiteGenState.value = UiState.Success(website)
        }
    }

    // Clear all profile and generator data
    fun resetAllData() {
        viewModelScope.launch {
            repository.clearAll()
            prefs.edit().clear().apply()
            _metrics.value = BusinessMetrics()
            _websiteGenState.value = UiState.Idle
            _postGenState.value = UiState.Idle
            _identityGenState.value = UiState.Idle

            // Reset local states
            isLoggedIn.value = false
            loggedInMethod.value = ""
            userIdentifier.value = ""
            isAutoPaymentSetup.value = false
            isSubscribed.value = false
            trialDaysElapsed.value = 0
        }
    }
}
