package com.example.hydrohero.data

data class Reminder(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val isEnabled: Boolean,
    val isPreset: Boolean = false
)
