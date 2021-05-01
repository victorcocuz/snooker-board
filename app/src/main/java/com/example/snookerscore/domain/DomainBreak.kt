package com.example.snookerscore.domain

data class DomainBreak(
    val breakId: Int,
    val player: Int,
    val frameId: Int,
    val pots: MutableList<DomainPot>,
    var breakSize: Int
)