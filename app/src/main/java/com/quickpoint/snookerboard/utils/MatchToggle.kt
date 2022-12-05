package com.quickpoint.snookerboard.utils

sealed class MatchToggle (
    var toggleAdvancedRules: Int
){
   object TOGGLE: MatchToggle(0)

    fun toggleAdvancedRulesOn() = toggleAdvancedRules == 1

    fun switchToggleAdvancedRules(){
        toggleAdvancedRules = 1 - toggleAdvancedRules
    }
}

