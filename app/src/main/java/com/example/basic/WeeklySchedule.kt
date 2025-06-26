package com.example.basic

// Data classes and sample schedule used for PlannerScreen

data class ClassEntry(
    val course: String,
    val faculty: String,
    val start: String,
    val end: String,
    val room: String
)

val WEEKLY_SCHEDULE: Map<String, List<ClassEntry>> = mapOf(
    "Monday" to listOf(
        ClassEntry("CSE1001", "Prof. Rao", "08:00", "08:50", "101"),
        ClassEntry("MAT1002", "Dr. Singh", "09:00", "09:50", "201"),
        ClassEntry("PHY1003", "Dr. Patel", "10:00", "10:50", "Lab 1"),
        ClassEntry("ENG1004", "Ms. James", "11:00", "11:50", "305")
    ),
    "Tuesday" to listOf(
        ClassEntry("CHE1005", "Dr. Verma", "08:00", "08:50", "202"),
        ClassEntry("CSE1001", "Prof. Rao", "09:00", "09:50", "101"),
        ClassEntry("MAT1002", "Dr. Singh", "10:00", "10:50", "201"),
        ClassEntry("PHY1003", "Dr. Patel", "11:00", "11:50", "Lab 1")
    ),
    "Wednesday" to listOf(
        ClassEntry("ENG1004", "Ms. James", "08:00", "08:50", "305"),
        ClassEntry("CSE1001", "Prof. Rao", "09:00", "09:50", "101"),
        ClassEntry("CHE1005", "Dr. Verma", "10:00", "10:50", "202"),
        ClassEntry("MAT1002", "Dr. Singh", "11:00", "11:50", "201")
    ),
    "Thursday" to listOf(
        ClassEntry("PHY1003", "Dr. Patel", "08:00", "08:50", "Lab 1"),
        ClassEntry("ENG1004", "Ms. James", "09:00", "09:50", "305"),
        ClassEntry("CSE1001", "Prof. Rao", "10:00", "10:50", "101"),
        ClassEntry("CHE1005", "Dr. Verma", "11:00", "11:50", "202")
    ),
    "Friday" to listOf(
        ClassEntry("MAT1002", "Dr. Singh", "08:00", "08:50", "201"),
        ClassEntry("PHY1003", "Dr. Patel", "09:00", "09:50", "Lab 1"),
        ClassEntry("ENG1004", "Ms. James", "10:00", "10:50", "305"),
        ClassEntry("CHE1005", "Dr. Verma", "11:00", "11:50", "202")
    ),
    "Saturday" to listOf(
        ClassEntry("CSE1001", "Prof. Rao", "09:00", "10:30", "101"),
        ClassEntry("LAB Project", "Staff", "10:45", "12:15", "Innovation Lab"),
        ClassEntry("MAT1002", "Dr. Singh", "12:30", "13:20", "201")
    )
)
