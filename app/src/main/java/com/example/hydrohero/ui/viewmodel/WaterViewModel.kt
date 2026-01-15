package com.example.hydrohero.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.hydrohero.data.DataRepository
import com.example.hydrohero.data.Reminder
import com.example.hydrohero.data.ShopCategory
import com.example.hydrohero.data.ShopItem
import com.example.hydrohero.data.UserData
import com.example.hydrohero.notifications.ReminderScheduler

class WaterViewModel(
    private val dataRepository: DataRepository,
    private val reminderScheduler: ReminderScheduler
) {
    // Don't call getUserData() here because it has side-effects (prototype resets).
    // We'll load it once in init.
    var userData by mutableStateOf(UserData())
        private set
    
    var shopItems by mutableStateOf(
        listOf(
            // Avatars
            ShopItem("water", "Water Drop", 0, "💧", true, ShopCategory.AVATAR, false), // Free default
            ShopItem("bear", "Sleepy Bear", 100, "🐻", false, ShopCategory.AVATAR, false),
            ShopItem("fox", "Curious Fox", 150, "🦊", false, ShopCategory.AVATAR, false),
            ShopItem("bunny", "Hopping Bunny", 180, "🐰", false, ShopCategory.AVATAR, false),
            ShopItem("panda", "Cute Panda", 200, "🐼", false, ShopCategory.AVATAR, false),
            ShopItem("cat", "Cool Cat", 220, "🐱", false, ShopCategory.AVATAR, false),
            ShopItem("dog", "Happy Dog", 250, "🐶", false, ShopCategory.AVATAR, false),
            ShopItem("tiger", "Wild Tiger", 300, "🐯", false, ShopCategory.AVATAR, true), // Premium
            ShopItem("lion", "Brave Lion", 350, "🦁", false, ShopCategory.AVATAR, true), // Premium
            ShopItem("dragon", "Mystic Dragon", 500, "🐲", false, ShopCategory.AVATAR, true), // Premium
            
            // Effects
            ShopItem("whale", "Whale Spout", 130, "🐋", false, ShopCategory.EFFECT, false),
            ShopItem("bottle1", "Classic Bottle", 100, "🍼", false, ShopCategory.EFFECT, false),
            ShopItem("bottle2", "Sports Bottle", 150, "🥤", false, ShopCategory.EFFECT, false),
            ShopItem("cup", "Magic Cup", 180, "☕", false, ShopCategory.EFFECT, true), // Premium
            
            // Backgrounds
            ShopItem("none", "No Background", 0, "⚪", true, ShopCategory.BACKGROUND, false), // Free default
            ShopItem("sea", "Deep Blue Sea", 220, "🌊", false, ShopCategory.BACKGROUND, false),
            ShopItem("stars", "Starry Night", 200, "⭐", false, ShopCategory.BACKGROUND, false),
            ShopItem("rainbow", "Rainbow Sky", 250, "🌈", false, ShopCategory.BACKGROUND, true), // Premium
            ShopItem("sunset", "Sunset View", 280, "🌅", false, ShopCategory.BACKGROUND, true), // Premium
            ShopItem("forest", "Forest Path", 300, "🌲", false, ShopCategory.BACKGROUND, true), // Premium
            ShopItem("beach", "Beach Paradise", 320, "🏖️", false, ShopCategory.BACKGROUND, true) // Premium
        )
    )
        private set
    
    var reminderCompletions by mutableStateOf(0)
        private set
    
    var reminderRewardClaimed by mutableStateOf(false)
        private set
    
    fun updateDailyGoal(newGoal: Int) {
        // Recalculate goal completion status based on new goal
        val wasGoalReached = userData.currentIntake >= newGoal
        
        userData = userData.copy(dailyGoal = newGoal)
        dataRepository.updateDailyGoal(newGoal)
        
        // If goal was previously reached but isn't anymore, reset celebration flags
        if (!wasGoalReached && showCelebration) {
            showCelebration = false
        }
    }

    var presetReminders by mutableStateOf(
        listOf(
            Reminder(
                id = "morning",
                title = "Start Your Day",
                description = "Start your day with a glass of water before 8:00 AM.",
                time = "8:00 AM",
                isEnabled = true,
                isPreset = true
            ),
            Reminder(
                id = "midday",
                title = "Midday Check",
                description = "Have you drunk at least half of your daily amount? Check around noon.",
                time = "12:00 PM",
                isEnabled = true,
                isPreset = true
            ),
            Reminder(
                id = "evening",
                title = "Evening Wind-down",
                description = "A final sip before bed at 9:00 PM.",
                time = "9:00 PM",
                isEnabled = true,
                isPreset = true
            )
        )
    )
        private set

    var customReminders by mutableStateOf(
        listOf(
            Reminder(
                id = "afternoon",
                title = "Afternoon Hydration",
                description = "1:00 PM, Daily.",
                time = "1:00 PM",
                isEnabled = true,
                isPreset = false
            ),
            Reminder(
                id = "bedtime",
                title = "Bedtime Sip",
                description = "9:30 PM, Weekdays.",
                time = "9:30 PM",
                isEnabled = false,
                isPreset = false
            )
        )
    )
        private set

    var showCelebration by mutableStateOf(false)
        private set
    
    var showProgressFeedback by mutableStateOf(false)
        private set
    
    var progressMessage by mutableStateOf("Good job!")
        private set
    
    var showCoinsEarned by mutableStateOf(false)
        private set
    
    var coinsEarnedAmount by mutableStateOf(0)
        private set

    var coinsEarnedSubtitle by mutableStateOf("")
        private set

    // Mock "Complete 3 reminders" based on hydration milestones:
    // 25% -> 1/3, 50% -> 2/3, 75% -> 3/3
    var reminderMilestoneStage by mutableStateOf(0)
        private set

    // One-shot toast event for milestone completion
    var reminderMilestoneToastMessage by mutableStateOf("")
        private set
    var reminderMilestoneToastEvent by mutableStateOf(0)
        private set

    // Ads: trigger an interstitial after completing daily goal (handled in UI layer; only shown for free users)
    var showGoalPopupAd by mutableStateOf(false)
        private set
    
    fun dismissCoinsEarned() {
        showCoinsEarned = false
        coinsEarnedSubtitle = ""
    }

    fun addWater(amount: Int) {
        val newIntake = userData.currentIntake + amount
        val wasGoalReached = userData.currentIntake >= userData.dailyGoal
        val isGoalReached = newIntake >= userData.dailyGoal
        
        val progressPercentage = if (userData.dailyGoal > 0) {
            (newIntake.toFloat() / userData.dailyGoal.toFloat() * 100f).coerceIn(0f, 100f)
        } else 0f
        
        val newGlassesCount = userData.glassesCount + 1
        
        userData = userData.copy(
            currentIntake = newIntake,
            glassesCount = newGlassesCount
        )
        
        // Save to persistence
        dataRepository.updateIntake(newIntake, newGlassesCount)

        // Mock reminder completion milestones based on hydration progress
        handleReminderMilestones(progressPercentage)
        
        // Award coins and update streak when goal is reached (only once per day)
        if (!wasGoalReached && isGoalReached) {
            // Award coins for completing daily goal
            val coinsEarned = 50
            val newCoins = userData.coins + coinsEarned
            
            // Increment streak immediately when goal is reached
            val newStreak = userData.streak + 1
            
            userData = userData.copy(
                coins = newCoins,
                streak = newStreak
            )
            dataRepository.updateCoins(newCoins)
            dataRepository.updateStreak(newStreak)
            
            // Show coins earned feedback
            coinsEarnedAmount = coinsEarned
            showCoinsEarned = true
            coinsEarnedSubtitle = "Daily goal completed!"
            
            showProgressFeedback = false // Cancel any progress feedback
            showCelebration = true
            showGoalPopupAd = true
        } else if (!isGoalReached) {
            // Show progress feedback for milestones (only if not at goal)
            val message = when {
                progressPercentage >= 75 -> "Almost there! 💪"
                progressPercentage >= 50 -> "Halfway there! 😊"
                progressPercentage >= 25 -> "Great progress! 🌟"
                else -> "Good job! Keep going! 💧"
            }
            progressMessage = message
            showProgressFeedback = true
        }
    }

    private fun handleReminderMilestones(progressPercentage: Float) {
        val targetStage = when {
            progressPercentage >= 75f -> 3
            progressPercentage >= 50f -> 2
            progressPercentage >= 25f -> 1
            else -> 0
        }

        if (targetStage <= reminderMilestoneStage) return

        reminderMilestoneStage = targetStage

        // Set progress directly to match stage (1..3)
        reminderCompletions = reminderMilestoneStage.coerceIn(0, 3)
        dataRepository.saveReminderCompletions(reminderCompletions)

        // Toast message referencing the 1st/2nd/3rd preset reminder
        val reminderName = when (reminderMilestoneStage) {
            1 -> presetReminders.getOrNull(0)?.title ?: "Reminder 1"
            2 -> presetReminders.getOrNull(1)?.title ?: "Reminder 2"
            3 -> presetReminders.getOrNull(2)?.title ?: "Reminder 3"
            else -> "Reminder"
        }
        reminderMilestoneToastMessage = "✅ Reminder completed: $reminderName"
        reminderMilestoneToastEvent++

        // Auto-award reward once when reaching 3/3
        if (reminderCompletions >= 3 && !reminderRewardClaimed) {
            awardReminderChallengeCoins()
        }
    }

    // (Manual reminder progress removed; driven by hydration milestones now)

    fun consumeGoalPopupAd() {
        showGoalPopupAd = false
    }
    
    fun dismissCelebration() {
        showCelebration = false
    }
    
    fun dismissProgressFeedback() {
        showProgressFeedback = false
    }
    
    fun setSelectedAvatar(avatarIcon: String) {
        userData = userData.copy(selectedAvatar = avatarIcon)
        dataRepository.updateSelectedAvatar(avatarIcon)
    }
    
    fun setSelectedBackground(backgroundId: String) {
        userData = userData.copy(selectedBackground = backgroundId)
        dataRepository.updateSelectedBackground(backgroundId)
    }
    
    fun setSelectedEffect(effectIcon: String) {
        userData = userData.copy(selectedEffect = effectIcon)
        dataRepository.updateSelectedEffect(effectIcon)
    }
    
    fun upgradeToPremium(premiumType: String) {
        val isPremium = premiumType != "none"
        userData = userData.copy(
            isPremium = isPremium,
            premiumType = premiumType
        )
        dataRepository.updatePremiumStatus(isPremium, premiumType)
    }
    
    fun purchaseShopItem(item: ShopItem): Boolean {
        // Check if already owned
        if (item.isOwned) {
            // If it's an avatar, select it
            if (item.category == ShopCategory.AVATAR) {
                setSelectedAvatar(item.icon)
            }
            // If it's a background, select it
            if (item.category == ShopCategory.BACKGROUND) {
                setSelectedBackground(item.id)
            }
            // If it's an effect, select it
            if (item.category == ShopCategory.EFFECT) {
                setSelectedEffect(item.icon)
            }
            return true
        }
        
        // Premium items require premium subscription - block purchase if not premium
        if (item.isPremium && !userData.isPremium) {
            return false // Cannot purchase premium items without premium subscription
        }
        
        // Free items (price 0) can be selected without purchase
        if (item.price == 0) {
            if (item.category == ShopCategory.AVATAR) {
                setSelectedAvatar(item.icon)
            }
            if (item.category == ShopCategory.BACKGROUND) {
                setSelectedBackground(item.id)
            }
            if (item.category == ShopCategory.EFFECT) {
                setSelectedEffect(item.icon)
            }
            return true
        }
        
        // Check if user has enough coins
        if (userData.coins >= item.price) {
            val newCoins = userData.coins - item.price
            userData = userData.copy(coins = newCoins)
            dataRepository.updateCoins(newCoins)
            
            // Mark item as owned
            shopItems = shopItems.map { shopItem ->
                if (shopItem.id == item.id) {
                    shopItem.copy(isOwned = true)
                } else {
                    shopItem
                }
            }
            
            // Save owned items to persistence
            saveOwnedShopItems()
            
            // If it's an avatar, select it immediately
            if (item.category == ShopCategory.AVATAR) {
                setSelectedAvatar(item.icon)
            }
            // If it's a background, select it immediately
            if (item.category == ShopCategory.BACKGROUND) {
                setSelectedBackground(item.id)
            }
            // If it's an effect, select it immediately
            if (item.category == ShopCategory.EFFECT) {
                setSelectedEffect(item.icon)
            }
            
            return true
        }
        
        return false // Not enough coins
    }
    
    private fun loadOwnedShopItems() {
        // Load owned items from persistence (simplified - you could use a Set<String> in SharedPreferences)
        // For now, we'll keep the default owned items
        val ownedItemIds = dataRepository.getOwnedItemIds()
        shopItems = shopItems.map { item ->
            item.copy(isOwned = ownedItemIds.contains(item.id))
        }
    }
    
    private fun saveOwnedShopItems() {
        val ownedItemIds = shopItems.filter { it.isOwned }.map { it.id }
        dataRepository.saveOwnedItemIds(ownedItemIds)
    }

    fun toggleReminder(id: String) {
        presetReminders = presetReminders.map { reminder ->
            if (reminder.id == id) {
                val newEnabled = !reminder.isEnabled
                val updated = reminder.copy(isEnabled = newEnabled)

                if (newEnabled) {
                    // If enabling a reminder, increment completion count (for demo/prototype)
                    incrementReminderCompletion()
                    if (notificationsEnabled) reminderScheduler.scheduleDaily(updated)
                } else {
                    reminderScheduler.cancel(reminder.id)
                }
                updated
            } else {
                reminder
            }
        }
        customReminders = customReminders.map { reminder ->
            if (reminder.id == id) {
                val newEnabled = !reminder.isEnabled
                val updated = reminder.copy(isEnabled = newEnabled)

                if (newEnabled) {
                    incrementReminderCompletion()
                    if (notificationsEnabled) reminderScheduler.scheduleDaily(updated)
                } else {
                    reminderScheduler.cancel(reminder.id)
                }
                updated
            } else {
                reminder
            }
        }
    }
    
    private fun incrementReminderCompletion() {
        if (reminderCompletions < 3) {
            reminderCompletions++
            dataRepository.saveReminderCompletions(reminderCompletions)
            if (reminderCompletions >= 3 && !reminderRewardClaimed) {
                // Auto-award reward once when reaching 3/3
                awardReminderChallengeCoins()
            }
        }
    }
    
    fun claimReminderReward() {
        if (reminderCompletions >= 3 && !reminderRewardClaimed) {
            awardReminderChallengeCoins()
        }
    }

    private fun awardReminderChallengeCoins() {
        val coinsEarned = 50
        val newCoins = userData.coins + coinsEarned
        userData = userData.copy(coins = newCoins)
        dataRepository.updateCoins(newCoins)

        reminderRewardClaimed = true
        dataRepository.saveReminderRewardClaimed(true)

        coinsEarnedAmount = coinsEarned
        coinsEarnedSubtitle = "3 reminders completed!"
        showCoinsEarned = true
    }
    
    fun addCustomReminder(title: String, description: String, time: String): Boolean {
        // Check limit: free users can only have 2 custom reminders, premium users have unlimited
        val maxCustomReminders = if (userData.isPremium) Int.MAX_VALUE else 2
        
        if (customReminders.size >= maxCustomReminders) {
            return false // Limit reached
        }
        
        val newId = "custom_${System.currentTimeMillis()}"
        val newReminder = Reminder(
            id = newId,
            title = title,
            description = description,
            time = time,
            isEnabled = true,
            isPreset = false
        )
        customReminders = customReminders + newReminder
        if (notificationsEnabled && newReminder.isEnabled) {
            reminderScheduler.scheduleDaily(newReminder)
        }
        return true
    }
    
    fun canAddCustomReminder(): Boolean {
        val maxCustomReminders = if (userData.isPremium) Int.MAX_VALUE else 2
        return customReminders.size < maxCustomReminders
    }
    
    fun deleteCustomReminder(id: String) {
        reminderScheduler.cancel(id)
        customReminders = customReminders.filter { it.id != id }
    }

    var selectedCategory by mutableStateOf("All")
        private set

    fun selectCategory(category: String) {
        selectedCategory = category
    }

    var notificationsEnabled by mutableStateOf(true)
        private set
    var soundEnabled by mutableStateOf(true)
        private set
    var vibrationEnabled by mutableStateOf(false)
        private set
    var quietHoursEnabled by mutableStateOf(false)
        private set
    var syncEnabled by mutableStateOf(true)
        private set

    init {
        // Load user data from persistence
        userData = dataRepository.getUserData()
        // Load reminder completion data
        reminderCompletions = dataRepository.getReminderCompletions()
        reminderRewardClaimed = dataRepository.getReminderRewardClaimed()
        // Load owned shop items from persistence
        loadOwnedShopItems()

        // Now that reminders + notification toggles are initialized, we can sync schedules safely
        syncReminderSchedules()
    }

    fun toggleNotifications() {
        updateNotificationsEnabled(!notificationsEnabled)
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        notificationsEnabled = enabled
        syncReminderSchedules()
    }

    private fun syncReminderSchedules() {
        val all = presetReminders + customReminders
        if (notificationsEnabled) {
            all.filter { it.isEnabled }.forEach { reminderScheduler.scheduleDaily(it) }
        } else {
            all.forEach { reminderScheduler.cancel(it.id) }
        }
    }

    fun toggleSound() {
        soundEnabled = !soundEnabled
    }

    fun toggleVibration() {
        vibrationEnabled = !vibrationEnabled
    }

    fun toggleQuietHours() {
        quietHoursEnabled = !quietHoursEnabled
    }

    fun toggleSync() {
        syncEnabled = !syncEnabled
    }
}
