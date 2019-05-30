package com.haejung.snapmark.presentation

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object GlideImageBindings {

    @BindingAdapter("app:glideImage")
    @JvmStatic
    fun loadImage(view: ImageView, source: Any?) {
        source?.let {
            Glide.with(view).load(it).into(view)
        }
    }

    @BindingAdapter("app:glideImageUrl")
    @JvmStatic
    fun loadImageByUrl(view: ImageView, url: String?) {
        url?.let {
            Glide.with(view).load(it).into(view)
        }
    }

    @BindingAdapter("app:glideImageBitmap")
    @JvmStatic
    fun loadImageByBitmap(view: ImageView, bitmap: Bitmap?) {
        bitmap?.let {
            Glide.with(view).load(it).into(view)
        }
    }
}