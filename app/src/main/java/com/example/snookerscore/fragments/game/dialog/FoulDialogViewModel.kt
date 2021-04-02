package com.example.snookerscore.fragments.game.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.snookerscore.fragments.game.Ball

class FoulDialogViewModel: ViewModel() {
    val ballClicked = MutableLiveData<Ball>()

}