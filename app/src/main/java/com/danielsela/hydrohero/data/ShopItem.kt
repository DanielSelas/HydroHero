package com.danielsela.hydrohero.data

data class ShopItem(
    val id: String,
    val name: String,
    val price: Int,
    val icon: String,
    val isOwned: Boolean = false,
    val category: ShopCategory,
    val isPremium: Boolean = false,
    val mascotId: String? = null
)

enum class ShopCategory {
    AVATAR,
    EFFECT,
    BACKGROUND
}
