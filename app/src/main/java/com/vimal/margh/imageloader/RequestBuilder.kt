package com.vimal.margh.imageloader

import android.content.Context
import android.widget.ImageView

class RequestBuilder(
    private val context: Context
) {
    internal var url: String? = null
    internal var thumbnailResId: Int? = null

    fun load(url: String): RequestBuilder {
        this.url = url
        return this
    }

    fun thumbnail(resId: Int): RequestBuilder {
        this.thumbnailResId = resId
        return this
    }

    fun into(imageView: ImageView) {
        ImageLoader.getInstance(context).doLoad(this, imageView)
    }
}