package com.messaging.kakchotask.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Download (
    var progress: Int = 0,
    var currentFileSize: Int = 0,
    var totalFileSize: Int = 0
) : Parcelable