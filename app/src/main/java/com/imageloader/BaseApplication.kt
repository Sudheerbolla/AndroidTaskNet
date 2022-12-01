package com.imageloader

import android.app.Application
import com.imageloader.utils.StaticUtils
import com.imageloading.ImageLoader

class BaseApplication : Application() {

    companion object {
        private var mInstance: BaseApplication? = null
        var imageLoader: ImageLoader? = null

        //        to use Glide or our local pattern
        var useGlide: Boolean? = true

        @Synchronized
        fun getInstance(): BaseApplication {
            if (mInstance == null) mInstance = BaseApplication()
            return mInstance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
//        To gracefully handle any crashes/exception and show error, instead of crashing
        Thread.setDefaultUncaughtExceptionHandler { thread, exc ->
            StaticUtils.showSimpleToast(this, exc.localizedMessage!!)
        }
        if (useGlide!!.not()) imageLoader = ImageLoader(this)
    }
}