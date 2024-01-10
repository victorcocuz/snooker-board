package com.quickpoint.snookerboard.domain.utils

import com.quickpoint.snookerboard.R
import com.quickpoint.snookerboard.core.utils.Constants
import com.quickpoint.snookerboard.data.*
import com.quickpoint.snookerboard.domain.repository.DataStoreRepository
import com.quickpoint.snookerboard.domain.utils.DomainPlayer.Player01
import com.quickpoint.snookerboard.domain.utils.DomainPlayer.Player02

sealed class DomainPlayer(
    var dataStoreRepository: DataStoreRepository?,
    var firstName: String,
    var lastName: String,
) {
    object Player01 : DomainPlayer(null, Constants.EMPTY_STRING, Constants.EMPTY_STRING)
    object Player02 : DomainPlayer(null, Constants.EMPTY_STRING, Constants.EMPTY_STRING)

    fun assignDataStore(dataStoreRepository: DataStoreRepository) {
        this.dataStoreRepository = dataStoreRepository
    }

    fun loadPreferences(firstName: String, lastName: String) {
        this.firstName = firstName
        this.lastName = lastName
    }

    fun hasNoName() = firstName == Constants.EMPTY_STRING || lastName == Constants.EMPTY_STRING
}

fun Player01.setPlayerName(key: String, value: String) {
    when (key) {
        K_PLAYER01_FIRST_NAME -> firstName = value
        K_PLAYER01_LAST_NAME -> lastName = value
    }
    dataStoreRepository?.savePrefs(key, value)
}

fun Player02.setPlayerName(key: String, value: String) {
    when (key) {
        K_PLAYER02_FIRST_NAME -> Player02.firstName = value
        K_PLAYER02_LAST_NAME -> Player02.lastName = value
    }
    dataStoreRepository?.savePrefs(key, value)
}

fun getPlayerNameByKey(key: String): String = when (key) {
    K_PLAYER01_FIRST_NAME -> Player01.firstName
    K_PLAYER01_LAST_NAME -> Player01.lastName
    K_PLAYER02_FIRST_NAME -> Player02.firstName
    else -> Player02.lastName // K_PLAYER02_LAST_NAME
}

fun getPlaceholderStringIdByKey(key: String): Int = when (key) {
    K_PLAYER01_FIRST_NAME -> R.string.l_rules_main_hint_name_first
    K_PLAYER01_LAST_NAME -> R.string.l_rules_main_hint_name_last
    K_PLAYER02_FIRST_NAME -> R.string.l_rules_main_hint_name_first
    else -> R.string.l_rules_main_hint_name_last // K_PLAYER02_LAST_NAME
}