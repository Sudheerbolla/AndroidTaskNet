package com.imageloader.models

import com.google.gson.annotations.SerializedName

data class ImageGroup(@SerializedName("manifest") var imageGroups: List<List<String>>? = null)
