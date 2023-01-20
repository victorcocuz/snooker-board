package com.quickpoint.snookerboard.domain

sealed class DomainPlayer(
    var firstName: String,
    var lastName: String
) {
    object PLAYER01 : DomainPlayer("", "")
    object PLAYER02 : DomainPlayer("", "")

    fun hasNoName() = firstName == "" || lastName == ""
    fun getPlayerText() = "First Name: $firstName, Last Name: $lastName"
}