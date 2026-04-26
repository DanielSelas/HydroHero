package com.example.hydrohero.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hydrohero.data.ShopItem
import com.example.hydrohero.data.UserData
import com.example.hydrohero.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Shop screen — soft & friendly, micro-animated.
 * Same callable signature as the original.
 *
 * Visual upgrades:
 *  • Hero coin wallet with animated count-up
 *  • Quest card: gradient ring + animated progress + claim micro-bounce
 *  • Avatar preview gets a soft halo + animated mint badge when owned
 *  • Pill category tabs with sliding active state
 *  • Cards: pill shape, soft shadow, shimmer ring on selected
 */
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
            .background(HydroBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(Modifier.width(44.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Reward yourself",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydroInk3,
                    letterSpacing = 1.sp,
                )
                Text(
                    "Shop",
                    fontFamily = HydroDisplayFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroInk,
                    letterSpacing = (-0.4).sp,
                )
            }
            CircleIconBtn("⚙", onSettingsClick)
        }

        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        LaunchedEffect(selectedCategory) {
            coroutineScope.launch { listState.animateScrollToItem(0) }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { CoinWallet(coins = userData.coins) }

            item {
                QuestCard(
                    completions = reminderCompletions,
                    target = 3,
                    rewardClaimed = reminderRewardClaimed,
                    onClaim = onClaimReward,
                )
            }

            item {
                AvatarShowcase(
                    avatar = userData.selectedAvatar,
                    label = shopItems.firstOrNull {
                        it.icon == userData.selectedAvatar &&
                            it.category == com.example.hydrohero.data.ShopCategory.AVATAR
                    }?.name ?: "Selected avatar",
                    isOwned = shopItems.firstOrNull {
                        it.icon == userData.selectedAvatar &&
                            it.category == com.example.hydrohero.data.ShopCategory.AVATAR
                    }?.isOwned == true,
                )
            }

            item {
                CategoryTabsRow(
                    selected = selectedCategory,
                    onSelect = onCategorySelected,
                )
            }

            when (selectedCategory) {
                "Avatars" -> item {
                    CategorySection("Avatars", shopItems.filter {
                        it.category == com.example.hydrohero.data.ShopCategory.AVATAR
                    }, userData, onItemClick)
                }
                "Effects" -> item {
                    CategorySection("Effects", shopItems.filter {
                        it.category == com.example.hydrohero.data.ShopCategory.EFFECT
                    }, userData, onItemClick)
                }
                "Backgrounds" -> item {
                    CategorySection("Backgrounds", shopItems.filter {
                        it.category == com.example.hydrohero.data.ShopCategory.BACKGROUND
                    }, userData, onItemClick)
                }
                else -> {
                    item {
                        CategorySection("Avatars", shopItems.filter {
                            it.category == com.example.hydrohero.data.ShopCategory.AVATAR
                        }, userData, onItemClick)
                    }
                    item {
                        CategorySection("Effects", shopItems.filter {
                            it.category == com.example.hydrohero.data.ShopCategory.EFFECT
                        }, userData, onItemClick)
                    }
                    item {
                        CategorySection("Backgrounds", shopItems.filter {
                            it.category == com.example.hydrohero.data.ShopCategory.BACKGROUND
                        }, userData, onItemClick)
                    }
                }
            }

            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Coin wallet hero

@Composable
private fun CoinWallet(coins: Int) {
    val animCoins by animateIntAsState(
        targetValue = coins,
        animationSpec = tween(700, easing = FastOutSlowInEasing),
        label = "coins"
    )
    val infinite = rememberInfiniteTransition(label = "coinShine")
    val shine by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shine"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    0f to HydroAccentSun,
                    1f to HydroAccentBlush,
                )
            )
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .scale(shine)
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center,
            ) { Text("🪙", fontSize = 32.sp) }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "YOUR COINS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydroInk2,
                    letterSpacing = 1.sp,
                )
                Text(
                    "$animCoins",
                    fontFamily = HydroDisplayFamily,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HydroInk,
                    letterSpacing = (-1).sp,
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(HydroSurface.copy(alpha = 0.7f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("Earn more", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HydroInk)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Quest card

@Composable
private fun QuestCard(
    completions: Int,
    target: Int,
    rewardClaimed: Boolean,
    onClaim: () -> Unit,
) {
    val progress = (completions / target.toFloat()).coerceIn(0f, 1f)
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "qprog"
    )
    val complete = completions >= target

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(HydroSurface)
            .border(1.dp, HydroLine, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(HydroPrimarySofter),
                    contentAlignment = Alignment.Center,
                ) { Text("🎯", fontSize = 18.sp) }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Daily quest",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HydroInk3,
                        letterSpacing = 0.8.sp,
                    )
                    Text(
                        "Complete 3 reminders",
                        fontFamily = HydroDisplayFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HydroInk,
                    )
                }
                AnimatedStatusPill(complete = complete, claimed = rewardClaimed)
            }
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(HydroPrimarySoft),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                0f to HydroPrimary,
                                1f to HydroPrimaryDeep,
                            )
                        ),
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "$completions / $target  •  +50 coins",
                    fontSize = 12.sp,
                    color = HydroInk2,
                )
                if (complete && !rewardClaimed) {
                    ClaimButton(onClaim)
                }
            }
        }
    }
}

@Composable
private fun AnimatedStatusPill(complete: Boolean, claimed: Boolean) {
    val bg by animateColorAsState(
        targetValue = when {
            claimed -> HydroLine
            complete -> HydroAccentMint
            else -> HydroPrimarySofter
        },
        animationSpec = tween(300),
        label = "statusBg"
    )
    val label = when {
        claimed -> "Claimed"
        complete -> "Ready!"
        else -> "In progress"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HydroInk)
    }
}

@Composable
private fun ClaimButton(onClaim: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "claim")
    val pulse by infinite.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse"
    )
    Surface(
        onClick = onClaim,
        shape = RoundedCornerShape(14.dp),
        color = HydroPrimaryDeep,
        modifier = Modifier.scale(pulse),
    ) {
        Text(
            "Claim ✨",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────
// Avatar showcase

@Composable
private fun AvatarShowcase(avatar: String, label: String, isOwned: Boolean) {
    val infinite = rememberInfiniteTransition(label = "halo")
    val haloScale by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "haloScale"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(HydroSurface)
            .border(1.dp, HydroLine, RoundedCornerShape(20.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .scale(haloScale)
                        .size(120.dp)
                        .clip(RoundedCornerShape(60.dp))
                        .background(HydroPrimarySofter),
                )
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(48.dp))
                        .background(HydroPrimarySoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(avatar, fontSize = 56.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                label,
                fontFamily = HydroDisplayFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = HydroInk,
            )
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isOwned) HydroAccentMint else HydroPrimarySofter)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = if (isOwned) "✓ Owned" else "Equipped",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = HydroInk,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────
// Category tabs

@Composable
private fun CategoryTabsRow(selected: String, onSelect: (String) -> Unit) {
    val tabs = listOf("All", "Avatars", "Effects", "Backgrounds")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        tabs.forEach { CategoryTab(it, selected == it, onSelect) }
    }
}

@Composable
fun CategoryTab(text: String, isSelected: Boolean, onClick: (String) -> Unit) {
    val bg by animateColorAsState(
        targetValue = if (isSelected) HydroPrimaryDeep else HydroSurface,
        animationSpec = tween(250),
        label = "tabBg"
    )
    val fg by animateColorAsState(
        targetValue = if (isSelected) Color.White else HydroInk2,
        animationSpec = tween(250),
        label = "tabFg"
    )
    Surface(
        onClick = { onClick(text) },
        color = bg,
        shape = RoundedCornerShape(14.dp),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, HydroLine) else null,
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = fg,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        )
    }
}

// ─────────────────────────────────────────────────────────────────────
// Category section + item card

@Composable
fun CategorySection(
    title: String,
    items: List<ShopItem>,
    userData: UserData,
    onItemClick: (ShopItem) -> Unit,
) {
    Column {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = HydroInk3,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp),
        )
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f)) {
                        ShopItemCard(item, userData, onItemClick)
                    }
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ShopItemCard(item: ShopItem, userData: UserData, onClick: (ShopItem) -> Unit) {
    val isSelected = when (item.category) {
        com.example.hydrohero.data.ShopCategory.AVATAR -> item.icon == userData.selectedAvatar
        com.example.hydrohero.data.ShopCategory.BACKGROUND -> item.id == userData.selectedBackground
        com.example.hydrohero.data.ShopCategory.EFFECT -> item.icon == userData.selectedEffect
    }
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) HydroPrimaryDeep else HydroLine,
        animationSpec = tween(300),
        label = "cardBorder"
    )
    val borderWidth by animateFloatAsState(
        targetValue = if (isSelected) 2f else 1f,
        animationSpec = tween(300),
        label = "borderW"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(HydroSurface)
            .border(borderWidth.dp, borderColor, RoundedCornerShape(18.dp))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(HydroPrimarySofter),
                contentAlignment = Alignment.Center,
            ) {
                if (item.mascotId != null) {
                    MascotById(item.mascotId, size = 80.dp)
                } else {
                    Text(item.icon, fontSize = 48.sp)
                }
                if (item.isPremium) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(HydroAccentSun)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("👑", fontSize = 12.sp)
                    }
                }
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(HydroAccentMint)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("✓", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HydroInk)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = HydroInk,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = if (item.price == 0) "Free" else "🪙 ${item.price}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (item.price == 0) HydroInk else HydroPrimaryDeep,
            )
            Spacer(Modifier.height(8.dp))
            ShopActionButton(item = item, isSelected = isSelected, isPremiumUser = userData.isPremium, onClick = { onClick(item) })
        }
    }
}

@Composable
private fun ShopActionButton(
    item: ShopItem,
    isSelected: Boolean,
    isPremiumUser: Boolean,
    onClick: () -> Unit,
) {
    val (label, bg, fg) = when {
        isSelected -> Triple("Selected", HydroAccentMint, HydroInk)
        item.isPremium && !isPremiumUser && !item.isOwned -> Triple("Premium 👑", HydroAccentSun, HydroInk)
        item.isOwned -> Triple("Select", HydroPrimarySofter, HydroPrimaryDeep)
        item.price == 0 -> Triple("Get free", HydroPrimaryDeep, Color.White)
        else -> Triple("Buy", HydroPrimaryDeep, Color.White)
    }
    Surface(
        onClick = onClick,
        enabled = !isSelected,
        shape = RoundedCornerShape(12.dp),
        color = bg,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = fg)
        }
    }
}

@Composable
private fun CircleIconBtn(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        color = HydroSurface,
        modifier = Modifier.size(44.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(label, fontSize = 18.sp, color = HydroInk)
        }
    }
}
