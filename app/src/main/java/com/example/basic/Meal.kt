package com.example.basic

data class Meal(
    val name: String,
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val items: List<String>
)
