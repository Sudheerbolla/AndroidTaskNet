package com.imageloader.viewmodels

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.imageloader.models.ImageGroup
import com.imageloader.models.ImageModel
import com.imageloader.repository.ImagesRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ImagesViewModel() : ViewModel() {

    private lateinit var imageGroupsList: List<List<String>>
    val currentImageDetails = MutableLiveData<ImageModel>()
    var selectedImageGroupPosition: ObservableInt = ObservableInt(0)
    var selectedImageGroup = MutableLiveData<List<String>>()

    //    0 for group requests and 1 for individual image request
    val apiError = MutableLiveData<Int>()
    var errorMessage: String? = ""

    //  fields for maintaining Visibility
    var itemLoaded: ObservableBoolean? = null
    var loading: ObservableInt? = ObservableInt(View.VISIBLE)
    var showEmpty: ObservableInt? = ObservableInt(View.GONE)
    var showData: ObservableInt? = ObservableInt(View.GONE)
    var repository: ImagesRepository? = null

    //    This flag allows users to either go round and round in between image groups. If true, cyclic behaviour is enabled.
    val allowCyclicBehaviour: Boolean = true

    //    This flag allows users to allow swiping between images.
    val enableViewPagerSwipe: Boolean = true

    constructor(repository: ImagesRepository) : this() {
        this.repository = repository
        imageGroupsList = listOf()
        updateVisibilities(0)
        itemLoaded = ObservableBoolean(false)
        getAllImageGroups()
    }

    fun getImageGroups(): List<List<String>> {
        return imageGroupsList
    }

    /**
     * Clicked previous group button
     */
    fun goToPreviousGroup() {
        if (allowCyclicBehaviour) {
            updateCurrentImageGroup(
                if (selectedImageGroupPosition.get() != 0) selectedImageGroupPosition.get()
                    .minus(1) else getImageGroups().size - 1
            )
        } else {
            if (selectedImageGroupPosition.get() != 0)
                updateCurrentImageGroup(selectedImageGroupPosition.get().minus(1))
        }
    }

    /**
     * Clicked next group button
     */
    fun goToNextGroup() {
        if (allowCyclicBehaviour) {
            updateCurrentImageGroup(
                if (selectedImageGroupPosition.get() != getImageGroups().size - 1) selectedImageGroupPosition.get()
                    .plus(1)
                else 0
            )
        } else {
            if (selectedImageGroupPosition.get() != getImageGroups().size - 1)
                updateCurrentImageGroup(selectedImageGroupPosition.get().plus(1))
        }
    }

    private fun updateCurrentImageGroup(requestedImageGroupPosition: Int?) {
        try {
            selectedImageGroup.postValue(imageGroupsList.get(requestedImageGroupPosition!!))
            selectedImageGroupPosition.set(requestedImageGroupPosition)
            imageGroupsList[requestedImageGroupPosition].let {
                getImageDetails(it[0])
            }
        } catch (e: Exception) {
        }
    }

    /**
     * Api call for getting image groups
     */
    fun getAllImageGroups() {
        try {
            val response = repository?.getAllImageGroups()
            response?.enqueue(object : Callback<ImageGroup> {
                override fun onResponse(call: Call<ImageGroup>, response: Response<ImageGroup>) {
                    response.errorBody()?.let {
                        updateVisibilities(2)
                    } ?: response.body()?.let {
                        if (it.imageGroups.isNullOrEmpty()) {
                            updateVisibilities(2)
                        } else {
                            imageGroupsList = it.imageGroups!!
                            updateCurrentImageGroup(0)
                            updateVisibilities(1)
                        }
                    } ?: run {
                        updateVisibilities(2)
                    }
                }

                override fun onFailure(call: Call<ImageGroup>, t: Throwable) {
                    errorMessage = t.message
                    apiError.postValue(0)
                    updateVisibilities(2)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            apiError.postValue(0)
            updateVisibilities(2)
        }
    }

    /**
     * Api call for getting image details
     */
    fun getImageDetails(imageIdentifier: String) {
        try {
            itemLoaded?.set(false)
            val response = repository?.getImageDetails(imageIdentifier)
            response?.enqueue(object : Callback<ImageModel> {
                override fun onResponse(call: Call<ImageModel>, response: Response<ImageModel>) {
                    response.errorBody()?.let {
                        itemLoaded?.set(true)
                    } ?: response.body().let {
                        currentImageDetails.postValue(it)
                        itemLoaded?.set(true)
                    }
                }

                override fun onFailure(call: Call<ImageModel>, t: Throwable) {
                    errorMessage = t.message
                    apiError.postValue(1)
                    itemLoaded?.set(false)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            apiError.postValue(1)
            itemLoaded?.set(false)
        }
    }

    //Handling visibilities of the main screen
    private fun updateVisibilities(showDataViews: Int) {
        when (showDataViews) {
//            while loading
            0 -> {
                loading?.set(View.VISIBLE)
                showEmpty?.set(View.GONE)
                showData?.set(View.GONE)
            }
//            result successful
            1 -> {
                loading?.set(View.GONE)
                showEmpty?.set(View.GONE)
                showData?.set(View.VISIBLE)
            }
//            result failure
            2 -> {
                loading?.set(View.GONE)
                showEmpty?.set(View.VISIBLE)
                showData?.set(View.GONE)
            }
        }
    }

}