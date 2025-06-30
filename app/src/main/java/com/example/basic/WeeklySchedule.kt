package com.example.basic

// Basic data classes shared across the planner feature

data class ClassEntry(
    val course: String,
    val faculty: String,
    val start: String,
    val end: String,
    val room: String
)

// Timetable returned from the API maps a day to a list of classes
typealias Timetable = Map<String, List<ClassEntry>>
