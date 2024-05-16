package com.example.cypher_vault.view.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val capturedImages = MutableLiveData<List<Bitmap>>()
}