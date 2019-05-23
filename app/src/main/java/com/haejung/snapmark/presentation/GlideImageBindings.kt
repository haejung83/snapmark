package com.haejung.snapmark.presentation

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object GlideImageBindings {

    @BindingAdapter("app:glideImageUrl")
    @JvmStatic
    fun loadImageByUrl(view: ImageView, url: String) {
        Glide.with(view).load(url).into(view)
    }

    @BindingAdapter("app:glideImageBitmap")
    @JvmStatic
    fun loadImageByBitmap(view: ImageView, bitmap: Bitmap) {
        Glide.with(view).load(bitmap).into(view)
    }
}