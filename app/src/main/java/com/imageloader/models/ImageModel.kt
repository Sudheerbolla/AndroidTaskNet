package com.imageloader.models

import com.google.gson.annotations.SerializedName

data class ImageModel(
    @SerializedName("name") var name: String? = null,
    @SerializedName("width") var width: Int? = null,
    @SerializedName("height") var height: Int? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("url") var imageUrl: String? = null
) {
    var identifier: String = ""
}