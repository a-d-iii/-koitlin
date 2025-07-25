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
        ClassEntry("ENG1004", "Ms. James", "11:00", "11:50", "305"),
        ClassEntry("CHE1005", "Dr. Verma", "12:00", "12:50", "202"),
        ClassEntry("CSE2001", "Prof. Gupta", "13:00", "13:50", "102"),
        ClassEntry("MAT2002", "Dr. Mehta", "14:00", "14:50", "203"),
        ClassEntry("PHY2003", "Dr. Shah", "15:00", "15:50", "Lab 2"),
        ClassEntry("ENG2004", "Ms. Clark", "16:00", "16:50", "306")
    ),
    "Tuesday" to listOf(
        ClassEntry("CHE1005", "Dr. Verma", "08:00", "08:50", "202"),
        ClassEntry("CSE1001", "Prof. Rao", "09:00", "09:50", "101"),
        ClassEntry("MAT1002", "Dr. Singh", "10:00", "10:50", "201"),
        ClassEntry("PHY1003", "Dr. Patel", "11:00", "11:50", "Lab 1"),
        ClassEntry("CHE2005", "Dr. Jain", "12:00", "12:50", "204"),
        ClassEntry("CSE2001", "Prof. Gupta", "13:00", "13:50", "102"),
        ClassEntry("MAT2002", "Dr. Mehta", "14:00", "14:50", "203"),
        ClassEntry("PHY2003", "Dr. Shah", "15:00", "15:50", "Lab 2"),
        ClassEntry("ENG2004", "Ms. Clark", "16:00", "16:50", "306")
    ),
    "Wednesday" to listOf(
        ClassEntry("ENG1004", "Ms. James", "08:00", "08:50", "305"),
        ClassEntry("CSE1001", "Prof. Rao", "09:00", "09:50", "101"),
        ClassEntry("CHE1005", "Dr. Verma", "10:00", "10:50", "202"),
        ClassEntry("MAT1002", "Dr. Singh", "11:00", "11:50", "201"),
        ClassEntry("ELE2001", "Dr. Nair", "12:00", "12:50", "401"),
        ClassEntry("CSE2001", "Prof. Gupta", "13:00", "13:50", "102"),
        ClassEntry("MAT2002", "Dr. Mehta", "14:00", "14:50", "203"),
        ClassEntry("PHY2003", "Dr. Shah", "15:00", "15:50", "Lab 2"),
        ClassEntry("ENG2004", "Ms. Clark", "16:00", "16:50", "306")
    ),
    "Thursday" to listOf(
        ClassEntry("PHY1003", "Dr. Patel", "08:00", "08:50", "Lab 1"),
        ClassEntry("ENG1004", "Ms. James", "09:00", "09:50", "305"),
        ClassEntry("CSE1001", "Prof. Rao", "10:00", "10:50", "101"),
        ClassEntry("CHE1005", "Dr. Verma", "11:00", "11:50", "202"),
        ClassEntry("PHY2003", "Dr. Shah", "12:00", "12:50", "Lab 2"),
        ClassEntry("CSE2001", "Prof. Gupta", "13:00", "13:50", "102"),
        ClassEntry("MAT2002", "Dr. Mehta", "14:00", "14:50", "203"),
        ClassEntry("ENG2004", "Ms. Clark", "15:00", "15:50", "306"),
        ClassEntry("CHE2005", "Dr. Jain", "16:00", "16:50", "204")
    ),
    "Friday" to listOf(
        ClassEntry("MAT1002", "Dr. Singh", "08:00", "08:50", "201"),
        ClassEntry("PHY1003", "Dr. Patel", "09:00", "09:50", "Lab 1"),
        ClassEntry("ENG1004", "Ms. James", "10:00", "10:50", "305"),
        ClassEntry("CHE1005", "Dr. Verma", "11:00", "11:50", "202"),
        ClassEntry("CSE2001", "Prof. Gupta", "12:00", "12:50", "102"),
        ClassEntry("MAT2002", "Dr. Mehta", "13:00", "13:50", "203"),
        ClassEntry("PHY2003", "Dr. Shah", "14:00", "14:50", "Lab 2"),
        ClassEntry("ENG2004", "Ms. Clark", "15:00", "15:50", "306"),
        ClassEntry("CHE2005", "Dr. Jain", "16:00", "16:50", "204")
    ),
    "Saturday" to listOf(
        ClassEntry("CSE1001", "Prof. Rao", "09:00", "10:30", "101"),
        ClassEntry("LAB Project", "Staff", "10:45", "12:15", "Innovation Lab"),
        ClassEntry("MAT1002", "Dr. Singh", "12:30", "13:20", "201"),
        ClassEntry("ELE2001", "Dr. Nair", "13:30", "14:20", "401"),
        ClassEntry("CSE2001", "Prof. Gupta", "14:30", "15:20", "102"),
        ClassEntry("MAT2002", "Dr. Mehta", "15:30", "16:20", "203"),
        ClassEntry("PHY2003", "Dr. Shah", "16:30", "17:20", "Lab 2"),
        ClassEntry("ENG2004", "Ms. Clark", "17:30", "18:20", "306"),
        ClassEntry("CHE2005", "Dr. Jain", "18:30", "19:20", "204")
    )
)
