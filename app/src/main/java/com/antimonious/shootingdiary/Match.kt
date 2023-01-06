package com.antimonious.shootingdiary

data class Match(
    var Id: String?,
    var Date: String?,
    var StartTime: String?,
    var EndTime: String?,
    var Location: String?,
    var Result: Long?,
    var Inner10s: Long?,
    var Temperature: Long?,
    var Humidity: Long?,
    var AirPressure: Long?,
    var Mood: String?,
    var Notes: String?
)