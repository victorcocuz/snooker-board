package com.quickpoint.snookerboard.domain

data class DomainBreak(
//    val breakId: Int,
    val player: Int,
    val frameId: Int,
    val pots: MutableList<DomainPot>,
    var breakSize: Int
)