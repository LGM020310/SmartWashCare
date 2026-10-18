package com.smartwashcare.app.ui.screens



import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LabelViewModel : ViewModel() {
    // 촬영하거나 갤러리에서 고른 사진 (나중에 서버 업로드에 사용)
    var photoUri by mutableStateOf<Uri?>(null)
}