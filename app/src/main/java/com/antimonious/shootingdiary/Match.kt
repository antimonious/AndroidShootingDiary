package com.antimonious.shootingdiary

data class Match(
    val Id: String,
    val Date: String,
    val TimeSpan: String,
    val Location: String,
    val Result: String,
    val Inner10s: String,
    val Temperature: String,
    val Humidity: String,
    val AirPressure: String,
    val Mood: String,
    val Notes: String
)
