package com.example.snookerscore.fragments.game

import androidx.lifecycle.MutableLiveData

class Player(
    var frameScore: MutableLiveData<Int> = MutableLiveData<Int>(0),
    var matchScore: MutableLiveData<Int> = MutableLiveData<Int>(0)
)