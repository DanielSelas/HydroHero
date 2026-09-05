package com.danielsela.hydrohero

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.danielsela.hydrohero.analytics.FirebaseAnalyticsLogger
import com.danielsela.hydrohero.billing.BillingManager
import com.danielsela.hydrohero.data.DataRepository
import com.danielsela.hydrohero.notifications.NotificationChannels
import com.danielsela.hydrohero.notifications.ReminderScheduler
import com.danielsela.hydrohero.ui.navigation.Screen
import com.danielsela.hydrohero.ui.components.CelebrationOverlay
import com.danielsela.hydrohero.ui.components.ProgressFeedbackOverlay
import com.danielsela.hydrohero.ui.components.CoinsEarnedOverlay
import com.danielsela.hydrohero.ui.components.BannerAd
import com.danielsela.hydrohero.ui.screens.AddWaterDialog
import com.danielsela.hydrohero.ui.screens.AddReminderDialog
import com.danielsela.hydrohero.ui.screens.DailyProgressScreen
import com.danielsela.hydrohero.ui.screens.HomeScreen
import com.danielsela.hydrohero.ui.screens.RemindersScreen
import com.danielsela.hydrohero.ui.screens.SettingsScreen
import com.danielsela.hydrohero.ui.screens.ShopScreen
import com.danielsela.hydrohero.ui.screens.SubscriptionDialog
import com.danielsela.hydrohero.ui.theme.*
import com.danielsela.hydrohero.ui.viewmodel.WaterViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Restore the saved hue + dark mode before the first composition so the
        // app never paints one frame in the wrong theme, then write every later
        // toggle straight back.
        val themeRepository = DataRepository(this)
        HydroThemeRuntime.restore(
            hueId = themeRepository.getThemeHue(),
            dark = themeRepository.getDarkMode()
        )
        HydroThemeRuntime.setPersistence { hueId, dark ->
            themeRepository.saveThemeHue(hueId)
            themeRepository.saveDarkMode(dark)
        }

        setContent {
            HydroHeroTheme {
                HydroHeroApp()
            }
        }
    }
}

@Composable
fun HydroHeroApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val dataRepository = remember { DataRepository(context) }
    val reminderScheduler = remember { ReminderScheduler(context) }
    val analytics = remember { FirebaseAnalyticsLogger(context.applicationContext) }
    val viewModel = remember { WaterViewModel(dataRepository, reminderScheduler, analytics) }
    val billingManager = remember {
        BillingManager(
            context = context,
            onEntitlementChanged = { entitlement ->
                viewModel.applyEntitlement(entitlement.isPremium, entitlement.premiumType)
            },
            onError = { message -> viewModel.reportBillingError(message) },
        )
    }
    var showAddWaterDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var showSubscriptionDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }
    var selectedRating by remember { mutableStateOf(5) }
    var hasShownRateForReminderReward by remember { mutableStateOf(false) }

    val activity = context as? Activity

    // TODO: replace with the PUBLISHED (public) Google Sites URLs before release.
    // These are private "/edit" links: Play review and real users get a login prompt
    // or a 404. Publish the site, then paste the public https://sites.google.com/view/... URLs here.
    val privacyPolicyUrl =
        "https://sites.google.com/d/1n-Sg5VBSLgKZtTXVFLVuaQVSxW90SoPv/p/1Pu21Hb0X4nGiycH7PnGe3hGKmd38DBOZ/edit"
    val termsOfServiceUrl =
        "https://sites.google.com/d/1RK9-bYQa3DolFboLLIT7t5UE88ryS9PE/p/1fw2mlA_WVUxomHGnc2Gx835bBgCYAc83/edit"

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    /**
     * Settings > Export data: writes today's drink log to a CSV in the app's
     * cache and hands it to the system share sheet. The user picks the
     * destination, so nothing leaves the device without an explicit choice.
     */
    fun exportData() {
        val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())
        val entries = viewModel.waterEntries.sortedBy { it.timestamp }

        val csv = buildString {
            appendLine("time,amount_ml,daily_goal_ml")
            entries.forEach { entry ->
                appendLine(
                    "${timeFormatter.format(Instant.ofEpochMilli(entry.timestamp))}," +
                        "${entry.amount},${viewModel.userData.dailyGoal}"
                )
            }
        }

        val exportsDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportsDir, "hydro-hero-${LocalDate.now()}.csv")
        file.writeText(csv)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Hydro Hero — water log")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Export water log"))
    }

    /**
     * Opens the system screen for our reminders channel, where Android puts the
     * sound and vibration controls from API 26 on.
     */
    fun openSystemNotificationSettings() {
        val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            putExtra(Settings.EXTRA_CHANNEL_ID, NotificationChannels.REMINDERS_CHANNEL_ID)
        }
        // Fall back to the app's notification screen if the channel deep link is
        // unavailable on this device.
        val fallback = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(fallback)
        }
    }

    fun shareApp() {
        // Prototype-friendly: share a short message (replace link with Play Store URL when published)
        val shareText =
            "Hydro Hero 💧\n" +
                "A fun hydration tracker with goals, reminders, coins, and customization.\n\n" +
                "Try it out!"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Hydro Hero")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Share Hydro Hero"))
    }

    // Interstitial (Google's public TEST unit id)
    // TODO: replace with production AdMob IDs before release
    val interstitialUnitId = "ca-app-pub-3940256099942544/1033173712"
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    // Rewarded (Google's public TEST unit id)
    // TODO: replace with production AdMob IDs before release
    val rewardedUnitId = "ca-app-pub-3940256099942544/5224354917"
    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    var pendingDeleteReminderId by remember { mutableStateOf<String?>(null) }

    // Initialize notification channel + AdMob
    LaunchedEffect(Unit) {
        NotificationChannels.ensureCreated(context)
        MobileAds.initialize(context)
        analytics.setUserProperty("build_type", "debug")
    }

    // Tie the Play connection to the screen, and re-check entitlement on every
    // resume so a purchase, refund or cancellation made outside the app is
    // picked up when the user comes back.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        billingManager.start()
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) billingManager.queryPurchases()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            billingManager.stop()
        }
    }

    LaunchedEffect(viewModel.billingErrorEvent) {
        if (viewModel.billingErrorEvent > 0) {
            android.widget.Toast.makeText(
                context,
                viewModel.billingErrorMessage,
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Log screen views based on navigation destination
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        val screenName = when (currentRoute) {
            Screen.Home.route -> "Home"
            Screen.Reminders.route -> "Reminders"
            Screen.Shop.route -> "Shop"
            Screen.DailyProgress.route -> "DailyProgress"
            Screen.Settings.route -> "Settings"
            else -> currentRoute ?: "Unknown"
        }
        analytics.logScreen(screenName)
    }

    // Log key modal opens
    LaunchedEffect(showSubscriptionDialog) {
        if (showSubscriptionDialog) {
            analytics.logEvent("subscription_dialog_open")
        }
    }

    // Load ads (only needed for free users)
    LaunchedEffect(viewModel.userData.isPremium) {
        if (viewModel.userData.isPremium) {
            interstitialAd = null
            rewardedAd = null
            return@LaunchedEffect
        }

        InterstitialAd.load(
            context,
            interstitialUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )

        RewardedAd.load(
            context,
            rewardedUnitId,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                }
            }
        )
    }

    // Show interstitial when daily goal completed (free users only)
    LaunchedEffect(viewModel.showGoalPopupAd, viewModel.userData.isPremium, interstitialAd) {
        if (viewModel.showGoalPopupAd && !viewModel.userData.isPremium && activity != null && interstitialAd != null) {
            interstitialAd?.show(activity)
            // Consume event and reload for next time
            viewModel.consumeGoalPopupAd()
            interstitialAd = null
            InterstitialAd.load(
                context,
                interstitialUnitId,
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                    }
                }
            )
        }
    }

    // Android 13+ notification permission
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.updateNotificationsEnabled(granted)
        if (!granted) {
            android.widget.Toast.makeText(
                context,
                "Notifications permission denied. Reminders won't notify.",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Keep notifications disabled if permission is missing on Android 13+
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!granted) {
                viewModel.updateNotificationsEnabled(false)
            }
        }
    }


    // Mock reminder completion toasts (driven by hydration milestones 25/50/75%)
    LaunchedEffect(viewModel.reminderMilestoneToastEvent) {
        if (viewModel.reminderMilestoneToastEvent > 0) {
            android.widget.Toast.makeText(
                context,
                viewModel.reminderMilestoneToastMessage,
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Auto-show Rate dialog when the "3 reminders" reward is completed (once per reset)
    LaunchedEffect(viewModel.reminderRewardClaimed) {
        if (!viewModel.reminderRewardClaimed) {
            hasShownRateForReminderReward = false
        } else if (!hasShownRateForReminderReward) {
            showRateDialog = true
            hasShownRateForReminderReward = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                Column {
                    // Global banner ad (hidden for premium)
                    if (!viewModel.userData.isPremium) {
                        BannerAd(
                            // TODO: replace with production AdMob IDs before release
                            // (Google's public TEST banner unit id)
                            adUnitId = "ca-app-pub-3940256099942544/6300978111",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    BottomNavigationBar(navController = navController)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        userData = viewModel.userData,
                        onAddWaterClick = {
                            showAddWaterDialog = true
                        },
                        onSettingsClick = {
                            navController.navigate(Screen.Settings.route)
                        },
                        onSubscriptionClick = {
                            showSubscriptionDialog = true
                        },
                        onDailyProgressClick = {
                            navController.navigate(Screen.DailyProgress.route)
                        },
                        onQuickAdd = { ml ->
                            viewModel.addWater(ml)
                        }
                    )
                }
                composable(Screen.DailyProgress.route) {
                    DailyProgressScreen(
                        dailyGoal = viewModel.userData.dailyGoal,
                        currentIntake = viewModel.userData.currentIntake,
                        entries = viewModel.waterEntries,
                        onBackClick = { navController.popBackStack() },
                        onSettingsClick = { navController.navigate(Screen.Settings.route) }
                    )
                }
                composable(Screen.Reminders.route) {
                    RemindersScreen(
                        presetReminders = viewModel.presetReminders,
                        customReminders = viewModel.customReminders,
                        onToggleReminder = { id -> viewModel.toggleReminder(id) },
                        completedReminderIds = viewModel.completedReminderIds,
                        onToggleDone = { id -> viewModel.toggleReminderDone(id) },
                        onAddCustomReminder = {
                            if (viewModel.canAddCustomReminder()) {
                                showAddReminderDialog = true
                            } else {
                                // Show upgrade prompt if limit reached
                                showSubscriptionDialog = true
                            }
                        },
                        onDeleteReminder = { id ->
                            if (viewModel.userData.isPremium) {
                                viewModel.deleteCustomReminder(id)
                            } else {
                                // Free users: watch rewarded video to delete
                                if (activity == null) return@RemindersScreen
                                if (rewardedAd == null) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Ad not ready yet. Try again in a moment.",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    // Try to reload
                                    RewardedAd.load(
                                        context,
                                        rewardedUnitId,
                                        AdRequest.Builder().build(),
                                        object : RewardedAdLoadCallback() {
                                            override fun onAdLoaded(ad: RewardedAd) {
                                                rewardedAd = ad
                                            }

                                            override fun onAdFailedToLoad(error: LoadAdError) {
                                                rewardedAd = null
                                            }
                                        }
                                    )
                                } else {
                                    pendingDeleteReminderId = id
                                    val adToShow = rewardedAd
                                    rewardedAd = null
                                    adToShow?.show(activity) { _: RewardItem ->
                                        pendingDeleteReminderId?.let { viewModel.deleteCustomReminder(it) }
                                        pendingDeleteReminderId = null
                                    }
                                    // Reload for next time
                                    RewardedAd.load(
                                        context,
                                        rewardedUnitId,
                                        AdRequest.Builder().build(),
                                        object : RewardedAdLoadCallback() {
                                            override fun onAdLoaded(ad: RewardedAd) {
                                                rewardedAd = ad
                                            }

                                            override fun onAdFailedToLoad(error: LoadAdError) {
                                                rewardedAd = null
                                            }
                                        }
                                    )
                                }
                            }
                        },
                        onSettingsClick = { navController.navigate(Screen.Settings.route) },
                        isPremium = viewModel.userData.isPremium
                    )
                }
                composable(Screen.Shop.route) {
                    ShopScreen(
                        userData = viewModel.userData,
                        shopItems = viewModel.shopItems,
                        selectedCategory = viewModel.selectedCategory,
                        onCategorySelected = { category -> viewModel.selectCategory(category) },
                        onItemClick = { item ->
                            val wasOwned = item.isOwned
                            val success = viewModel.purchaseShopItem(item)
                            if (success) {
                                val actionText = if (wasOwned) "Selected" else "Purchased"
                                val categoryText = when (item.category) {
                                    com.danielsela.hydrohero.data.ShopCategory.AVATAR -> "avatar"
                                    com.danielsela.hydrohero.data.ShopCategory.BACKGROUND -> "background"
                                    com.danielsela.hydrohero.data.ShopCategory.EFFECT -> "effect"
                                }
                                android.widget.Toast.makeText(
                                    context,
                                    "$actionText $categoryText: ${item.name}",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            } else if (!item.isOwned) {
                                if (item.isPremium) {
                                    // Show toast for premium items - suggest upgrade
                                    android.widget.Toast.makeText(
                                        context,
                                        "Premium item! Upgrade to unlock 👑",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    showSubscriptionDialog = true
                                } else {
                                    // Show toast for insufficient coins
                                    android.widget.Toast.makeText(
                                        context,
                                        "Not enough coins! You need ${item.price} coins.",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        onSettingsClick = { navController.navigate(Screen.Settings.route) },
                        reminderCompletions = viewModel.reminderCompletions,
                        reminderRewardClaimed = viewModel.reminderRewardClaimed,
                        onClaimReward = { viewModel.claimReminderReward() }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        userData = viewModel.userData,
                        notificationsEnabled = viewModel.notificationsEnabled,
                        quietHoursEnabled = viewModel.quietHoursEnabled,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onGoalChange = { newGoal ->
                            viewModel.updateDailyGoal(newGoal)
                        },
                        onToggleNotifications = {
                            val enabling = !viewModel.notificationsEnabled
                            if (enabling && Build.VERSION.SDK_INT >= 33) {
                                val granted = ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.POST_NOTIFICATIONS
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                                if (!granted) {
                                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.updateNotificationsEnabled(true)
                                }
                            } else {
                                viewModel.toggleNotifications()
                            }
                        },
                        onToggleQuietHours = { viewModel.toggleQuietHours() },
                        onOpenSystemNotificationSettings = { openSystemNotificationSettings() },
                        onExportData = {
                            exportData()
                        },
                        onResetProgress = {
                            showResetDialog = true
                        },
                        onRateApp = {
                            showRateDialog = true
                        },
                        onShareApp = {
                            shareApp()
                        },
                        onOpenPrivacyPolicy = {
                            openUrl(privacyPolicyUrl)
                        },
                        onOpenTermsOfService = {
                            openUrl(termsOfServiceUrl)
                        }
                    )
                }
            }
        }
        
        // Overlays on top of everything
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reset Progress?") },
                text = { Text("This will reset your demo progress (intake, coins, streak, shop, reminders).") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetProgress()
                            showResetDialog = false
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                            android.widget.Toast.makeText(context, "Progress reset ✅", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    ) { Text("Reset") }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
                }
            )
        }

        if (showRateDialog) {
            AlertDialog(
                onDismissRequest = { showRateDialog = false },
                title = { Text("Rate Hydro Hero") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("If you enjoy the app, please rate us!")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (1..5).forEach { star ->
                                val filled = star <= selectedRating
                                Text(
                                    text = if (filled) "★" else "☆",
                                    fontSize = 34.sp,
                                    color = if (filled) Color(0xFFFFD700) else Color(0xFF9CA3AF),
                                    modifier = Modifier
                                        .clickable { selectedRating = star }
                                        .padding(horizontal = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Your rating: $selectedRating/5",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = {
                                android.widget.Toast.makeText(
                                    context,
                                    "Thanks! You rated us $selectedRating/5 ⭐",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                showRateDialog = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Rate us now")
                        }
                        TextButton(
                            onClick = { showRateDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
    Text(
                                "Maybe later",
                                color = PrimaryBlue
                            )
                        }
                    }
                },
                dismissButton = {}
            )
        }

        if (showAddWaterDialog) {
            AddWaterDialog(
                onDismiss = { showAddWaterDialog = false },
                onAddWater = { amount ->
                    viewModel.addWater(amount)
                }
            )
        }
        
        if (showAddReminderDialog) {
            AddReminderDialog(
                onDismiss = { showAddReminderDialog = false },
                onAddReminder = { title, description, time ->
                    val success = viewModel.addCustomReminder(title, description, time)
                    if (!success) {
                        // Limit reached, show upgrade prompt
                        showAddReminderDialog = false
                        showSubscriptionDialog = true
                        android.widget.Toast.makeText(
                            context,
                            "Custom reminder limit reached! Upgrade to Premium for unlimited reminders 👑",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
}
                }
            )
        }
        
        if (showSubscriptionDialog) {
            SubscriptionDialog(
                userData = viewModel.userData,
                monthlyPrice = billingManager.monthlyPrice,
                lifetimePrice = billingManager.lifetimePrice,
                onDismiss = { showSubscriptionDialog = false },
                onUpgrade = { premiumType ->
                    // No optimistic unlock: premium turns on only when Play
                    // reports the completed purchase back through the listener.
                    if (activity != null) billingManager.launchPurchase(activity, premiumType)
                    showSubscriptionDialog = false
                },
                onCancelMonthly = {
                    // Only Play can cancel a subscription; open the Play screen.
                    if (activity != null) billingManager.openSubscriptionManagement(activity)
                    showSubscriptionDialog = false
                }
            )
        }
        
        CelebrationOverlay(
            show = viewModel.showCelebration,
            effectIcon = viewModel.userData.selectedEffect, // Use effect icon
            onDismiss = { viewModel.dismissCelebration() }
        )
        
        ProgressFeedbackOverlay(
            show = viewModel.showProgressFeedback,
            message = viewModel.progressMessage,
            effectIcon = viewModel.userData.selectedEffect, // Use effect icon
            onDismiss = { viewModel.dismissProgressFeedback() }
        )
        
        CoinsEarnedOverlay(
            show = viewModel.showCoinsEarned,
            amount = viewModel.coinsEarnedAmount,
            subtitle = viewModel.coinsEarnedSubtitle.ifBlank { "Nice!" },
            onDismiss = { viewModel.dismissCoinsEarned() }
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Reminders,
        Screen.Shop,
        Screen.DailyProgress
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Floating pill, to match the card language of the rest of the app instead
    // of an edge-to-edge M3 NavigationBar.
    val pillShape = RoundedCornerShape(22.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(pillShape)
            .background(HydroSurface)
            .border(1.dp, HydroLine, pillShape)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route
            val itemShape = RoundedCornerShape(16.dp)
            val label = when (screen) {
                is Screen.Home -> "Home"
                is Screen.DailyProgress -> "Progress"
                is Screen.Reminders -> "Reminders"
                is Screen.Shop -> "Shop"
                is Screen.Settings -> "Settings"
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(itemShape)
                    .background(if (selected) HydroPrimarySoft else Color.Transparent)
                    .clickable {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                    .padding(vertical = 7.dp)
                    // One spoken item ("Home, selected") instead of an emoji
                    // read aloud followed by a separate label.
                    .semantics(mergeDescendants = true) {
                        contentDescription = label
                        this.selected = selected
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    when (screen) {
                        is Screen.Home -> "🏠"
                        is Screen.DailyProgress -> "📈"
                        is Screen.Reminders -> "🔔"
                        is Screen.Shop -> "🛍️"
                        is Screen.Settings -> "⚙️"
                    },
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    label,
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) HydroPrimaryDeep else HydroInk3
                )
            }
        }
    }
}