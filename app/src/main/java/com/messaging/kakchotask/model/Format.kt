package com.messaging.kakchotask.model

import com.google.gson.annotations.SerializedName

class Format (
    val format: String,

    @SerializedName("download_url")
    val downloadUrl: String,

    @SerializedName("preview_url")
    val previewUrl: String
)