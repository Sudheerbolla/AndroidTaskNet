package com.imageloader.utils.views

import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.imageloader.BaseApplication
import com.imageloader.R
import com.imageloader.models.ImageModel
import com.imageloader.utils.StaticUtils

object BindingAdapterUtils {

    /**
     *  For loading image into imageview from URL using Glide
     */
    @JvmStatic
    @BindingAdapter("app:imageUrlItem")
    fun bindImage(imageView: AspectRatioImageView, imageModel: ImageModel?) {
//        Check and load image from Url into our imageview
        imageModel?.imageUrl?.let {
            if (BaseApplication.useGlide!!) {
//        This is added because of cache issue. Before loading new image in viewpager, old image is being shown for a small time.
                Glide.with(imageView).clear(imageView)

                Glide.with(imageView.context).load(StaticUtils.getGlideImageUrl(it)).centerCrop()
                    .transform(RoundedCorners(15))
                    .error(R.drawable.ic_image_error).placeholder(R.drawable.ic_image_loading)
                    .dontAnimate()
                    .into(imageView)
            } else {
                BaseApplication.imageLoader?.displayImage(it, true, imageView)
            }
        }
    }

}