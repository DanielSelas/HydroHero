package com.example.hydrohero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hydrohero.data.DataRepository
import com.example.hydrohero.ui.navigation.Screen
import com.example.hydrohero.ui.components.CelebrationOverlay
import com.example.hydrohero.ui.components.ProgressFeedbackOverlay
import com.example.hydrohero.ui.components.CoinsEarnedOverlay
import com.example.hydrohero.ui.screens.AddWaterDialog
import com.example.hydrohero.ui.screens.AddReminderDialog
import com.example.hydrohero.ui.screens.HomeScreen
import com.example.hydrohero.ui.screens.RemindersScreen
import com.example.hydrohero.ui.screens.SettingsScreen
import com.example.hydrohero.ui.screens.ShopScreen
import com.example.hydrohero.ui.screens.SubscriptionDialog
import com.example.hydrohero.ui.theme.*
import com.example.hydrohero.ui.viewmodel.WaterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val viewModel = remember { WaterViewModel(dataRepository) }
    var showAddWaterDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }
    var showSubscriptionDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController)
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
                        }
                    )
                }
                composable(Screen.Reminders.route) {
                    RemindersScreen(
                        presetReminders = viewModel.presetReminders,
                        customReminders = viewModel.customReminders,
                        onToggleReminder = { id -> viewModel.toggleReminder(id) },
                        onAddCustomReminder = {
                            showAddReminderDialog = true
                        },
                        onDeleteReminder = { id -> viewModel.deleteCustomReminder(id) }
                    )
                }
                composable(Screen.Shop.route) {
                    ShopScreen(
                        userData = viewModel.userData,
                        shopItems = viewModel.shopItems,
                        selectedCategory = viewModel.selectedCategory,
                        onCategorySelected = { category -> viewModel.selectCategory(category) },
                        onItemClick = { item ->
                            val success = viewModel.purchaseShopItem(item)
                            if (!success && !item.isOwned) {
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
                        reminderCompletions = viewModel.reminderCompletions,
                        reminderRewardClaimed = viewModel.reminderRewardClaimed,
                        onClaimReward = { viewModel.claimReminderReward() }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        userData = viewModel.userData,
                        notificationsEnabled = viewModel.notificationsEnabled,
                        soundEnabled = viewModel.soundEnabled,
                        vibrationEnabled = viewModel.vibrationEnabled,
                        quietHoursEnabled = viewModel.quietHoursEnabled,
                        syncEnabled = viewModel.syncEnabled,
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onGoalChange = { newGoal ->
                            viewModel.updateDailyGoal(newGoal)
                        },
                        onToggleNotifications = { viewModel.toggleNotifications() },
                        onToggleSound = { viewModel.toggleSound() },
                        onToggleVibration = { viewModel.toggleVibration() },
                        onToggleQuietHours = { viewModel.toggleQuietHours() },
                        onToggleSync = { viewModel.toggleSync() },
                        onExportData = {
                            // TODO: Export data
                        },
                        onResetProgress = {
                            // TODO: Show confirmation and reset
                        }
                    )
                }
            }
        }
        
        // Overlays on top of everything
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
                    viewModel.addCustomReminder(title, description, time)
                }
            )
        }
        
        if (showSubscriptionDialog) {
            SubscriptionDialog(
                userData = viewModel.userData,
                onDismiss = { showSubscriptionDialog = false },
                onUpgrade = { premiumType ->
                    viewModel.upgradeToPremium(premiumType)
                    showSubscriptionDialog = false
                    android.widget.Toast.makeText(
                        context,
                        "Premium activated! 🎉",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
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
        Screen.Settings
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar(
        containerColor = BackgroundWhite,
        modifier = Modifier.height(72.dp)
    ) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            when (screen) {
                                is Screen.Home -> "🏠"
                                is Screen.Reminders -> "🔔"
                                is Screen.Shop -> "🛍️"
                                is Screen.Settings -> "⚙️"
                            },
                            fontSize = 22.sp
                        )
                    }
                },
                label = {
                    Text(
                        when (screen) {
                            is Screen.Home -> "Home"
                            is Screen.Reminders -> "Reminders"
                            is Screen.Shop -> "Shop"
                            is Screen.Settings -> "Settings"
                        },
                        fontSize = 10.sp
                    )
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    unselectedIconColor = TextLight,
                    unselectedTextColor = TextLight,
                    indicatorColor = BackgroundWhite
                )
            )
        }
    }
}