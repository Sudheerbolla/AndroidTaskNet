package com.imageloader.repository

import com.imageloader.utils.wsutils.RetrofitService

class ImagesRepository constructor(private val retrofitService: RetrofitService) {

    fun getAllImageGroups() = retrofitService.getImageGroups()

    fun getImageDetails(imageIdentifier:String) = retrofitService.getImageDetails(imageIdentifier)

}