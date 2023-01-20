package com.quickpoint.snookerboard.domain.objects

import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.domain.objects.DomainPlayer.Player01
import com.quickpoint.snookerboard.domain.objects.DomainPlayer.Player02
import com.quickpoint.snookerboard.utils.K_PLAYER01_FIRST_NAME
import com.quickpoint.snookerboard.utils.K_PLAYER01_LAST_NAME
import com.quickpoint.snookerboard.utils.K_PLAYER02_FIRST_NAME

sealed class DomainPlayer(
    var firstName: String,
    var lastName: String
) {
    object Player01 : DomainPlayer("", "")
    object Player02 : DomainPlayer("", "")

    fun hasNoName() = firstName == "" || lastName == ""
    fun getPlayerText() = "First Name: $firstName, Last Name: $lastName"
}

fun setPlayerName(key: String, value: String) = when(key) {
    K_PLAYER01_FIRST_NAME -> Player01.firstName = value
    K_PLAYER01_LAST_NAME -> Player01.lastName = value
    K_PLAYER02_FIRST_NAME -> Player02.firstName = value
    else -> Player02.lastName = value // K_PLAYER02_LAST_NAME
}

fun getPlayerNameByKey(key: String): String = when(key) {
    K_PLAYER01_FIRST_NAME -> Player01.firstName
    K_PLAYER01_LAST_NAME -> Player01.lastName
    K_PLAYER02_FIRST_NAME -> Player02.firstName
    else -> Player02.lastName // K_PLAYER02_LAST_NAME
}

fun getPlaceholderStringIdByKey(key: String): Int = when(key) {
    K_PLAYER01_FIRST_NAME -> R.string.l_rules_main_hint_name_first
    K_PLAYER01_LAST_NAME -> R.string.l_rules_main_hint_name_last
    K_PLAYER02_FIRST_NAME -> R.string.l_rules_main_hint_name_first
    else -> R.string.l_rules_main_hint_name_last // K_PLAYER02_LAST_NAME
}