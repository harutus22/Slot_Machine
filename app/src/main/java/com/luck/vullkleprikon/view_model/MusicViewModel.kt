package com.luck.vullkleprikon.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicViewModel: ViewModel() {
    val isMusicPlaying: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val isSoundPlaying: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
}