package com.example.hydrohero.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydrohero.data.ShopItem
import com.example.hydrohero.data.UserData
import com.example.hydrohero.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ShopScreen(
    userData: UserData,
    shopItems: List<ShopItem>,
    selectedCategory: String = "All",
    onCategorySelected: (String) -> Unit,
    onItemClick: (ShopItem) -> Unit,
    onSettingsClick: () -> Unit,
    reminderCompletions: Int = 0,
    reminderRewardClaimed: Boolean = false,
    onClaimReward: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BackgroundWhite)
                .padding(16.dp, 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "💧",
                fontSize = 20.sp
            )
            Text(
                text = "Shop",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            IconButton(onClick = onSettingsClick) {
                Text("⚙️", fontSize = 20.sp)
            }
        }

        // Content - Scrollable with LazyColumn
        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        
        // Scroll to category when selected
        LaunchedEffect(selectedCategory) {
            // Scroll to top when category changes (filtering will show the right items)
            coroutineScope.launch {
                listState.animateScrollToItem(0)
            }
        }
        
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Complete 3 reminders",
                                fontSize = 14.sp,
                                color = TextDark
                            )
                            Surface(
                                color = if (reminderCompletions >= 3) AccentGreen else Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (reminderCompletions >= 3) "Completed" else "In Progress",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (reminderCompletions >= 3) BackgroundWhite else Color(0xFF92400E),
                                    modifier = Modifier.padding(4.dp, 8.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val progress = (reminderCompletions / 3f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = PrimaryBlue,
                            trackColor = BorderLight
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$reminderCompletions/3",
                                fontSize = 12.sp,
                                color = TextLight
                            )
                            if (reminderCompletions >= 3 && !reminderRewardClaimed) {
                                Button(
                                    onClick = onClaimReward,
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Text(
                                        "Claim Reward",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            } else if (reminderCompletions < 3) {
                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                                ) {
                                    Text(
                                        "Keep Going",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\$50 Coins",
                            fontSize = 14.sp,
                            color = TextDark
                        )
                    }
                }
            }

            // Coins Display
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your Coins:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                        Text(
                            text = "\$${userData.coins}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }
                }
            }
            
            // Character Preview - Show selected avatar
            item {
                val selectedAvatarItem = shopItems.find { it.icon == userData.selectedAvatar && it.category == com.example.hydrohero.data.ShopCategory.AVATAR }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(LightBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(userData.selectedAvatar, fontSize = 60.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = selectedAvatarItem?.name ?: "Selected Avatar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (selectedAvatarItem?.isOwned == true) "Owned" else "Select in Shop",
                            fontSize = 14.sp,
                            color = TextLight
                        )
                    }
                }
            }
            
            // Category Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryTab("All", selectedCategory == "All", onCategorySelected)
                    CategoryTab("Avatars", selectedCategory == "Avatars", onCategorySelected)
                    CategoryTab("Effects", selectedCategory == "Effects", onCategorySelected)
                    CategoryTab("Backgrounds", selectedCategory == "Backgrounds", onCategorySelected)
                }
            }
            
            // Category Section Headers with Items
            when (selectedCategory) {
                "Avatars" -> {
                    item {
                        CategorySection(
                            title = "Avatars",
                            items = shopItems.filter { it.category == com.example.hydrohero.data.ShopCategory.AVATAR },
                            userData = userData,
                            onItemClick = onItemClick
                        )
                    }
                }
                "Effects" -> {
                    item {
                        CategorySection(
                            title = "Effects",
                            items = shopItems.filter { it.category == com.example.hydrohero.data.ShopCategory.EFFECT },
                            userData = userData,
                            onItemClick = onItemClick
                        )
                    }
                }
                "Backgrounds" -> {
                    item {
                        CategorySection(
                            title = "Backgrounds",
                            items = shopItems.filter { it.category == com.example.hydrohero.data.ShopCategory.BACKGROUND },
                            userData = userData,
                            onItemClick = onItemClick
                        )
                    }
                }
                else -> {
                    // Show all categories
                    item {
                        CategorySection(
                            title = "Avatars",
                            items = shopItems.filter { it.category == com.example.hydrohero.data.ShopCategory.AVATAR },
                            userData = userData,
                            onItemClick = onItemClick
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    item {
                        CategorySection(
                            title = "Effects",
                            items = shopItems.filter { it.category == com.example.hydrohero.data.ShopCategory.EFFECT },
                            userData = userData,
                            onItemClick = onItemClick
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    item {
                        CategorySection(
                            title = "Backgrounds",
                            items = shopItems.filter { it.category == com.example.hydrohero.data.ShopCategory.BACKGROUND },
                            userData = userData,
                            onItemClick = onItemClick
                        )
                    }
                }
            }

            // (Banner ad is shown globally in MainActivity above the bottom nav)
        }
    }
}

@Composable
fun CategoryTab(
    text: String,
    isSelected: Boolean,
    onClick: (String) -> Unit
) {
    Surface(
        onClick = { onClick(text) },
        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
        color = if (isSelected) PrimaryBlue else BackgroundWhite,
        shape = RoundedCornerShape(20.dp),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, BorderLight) else null
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) BackgroundWhite else TextLight,
            modifier = Modifier.padding(8.dp, 16.dp)
        )
    }
}

@Composable
fun CategorySection(
    title: String,
    items: List<ShopItem>,
    userData: UserData,
    onItemClick: (ShopItem) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        // Use a simple grid layout with Row and Column
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        ShopItemCard(item = item, userData = userData, onClick = onItemClick)
                    }
                }
                // Add spacer if odd number of items
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun ShopItemCard(
    item: ShopItem,
    userData: UserData,
    onClick: (ShopItem) -> Unit
) {
    val isSelected = when (item.category) {
        com.example.hydrohero.data.ShopCategory.AVATAR -> item.icon == userData.selectedAvatar
        com.example.hydrohero.data.ShopCategory.BACKGROUND -> item.id == userData.selectedBackground
        com.example.hydrohero.data.ShopCategory.EFFECT -> item.icon == userData.selectedEffect
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, if (isSelected) PrimaryBlue else BorderLight, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        onClick = { onClick(item) }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF3F4F6))
            ) {
                // Item icon
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.icon, fontSize = 48.sp)
                }
                
                // Premium crown icon
                if (item.isPremium) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    ) {
                        Text(
                            text = "👑",
                            fontSize = 20.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (item.price == 0) "Free" else "\$${item.price}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.price == 0) AccentGreen else PrimaryBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onClick(item) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSelected,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isSelected -> AccentGreen
                        item.isPremium && !userData.isPremium && !item.isOwned -> Color(0xFFFFD700)
                        item.isOwned -> BorderLight
                        item.price == 0 -> AccentGreen
                        else -> PrimaryBlue
                    }
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = when {
                        isSelected -> "Selected"
                        item.isPremium && !userData.isPremium && !item.isOwned -> "Premium"
                        item.isOwned -> "Select"
                        item.price == 0 -> "Free"
                        else -> "Buy"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        isSelected -> BackgroundWhite
                        item.isPremium && !userData.isPremium && !item.isOwned -> TextDark
                        item.isOwned -> TextLight
                        item.price == 0 -> BackgroundWhite
                        else -> BackgroundWhite
                    }
                )
            }
        }
    }
}
