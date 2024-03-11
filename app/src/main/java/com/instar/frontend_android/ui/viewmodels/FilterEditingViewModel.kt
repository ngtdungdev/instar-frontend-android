package com.instar.frontend_android.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.instar.frontend_android.ui.DTO.ImageAndVideo

class FilterEditingViewModel : ViewModel() {
    var data: MutableList<ImageAndVideo>? = mutableListOf()
}