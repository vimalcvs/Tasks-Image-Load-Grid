package com.vimal.margh.imageloader

import android.content.Context

class ImageLoader {
    companion object {
        @Volatile
        private var INSTANCE: InternalLoader? = null

        fun with(context: Context): RequestBuilder {
            return RequestBuilder(context)
        }

        internal fun getInstance(context: Context): InternalLoader {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildInternalLoader(context).also { INSTANCE = it }
            }
        }

        private fun buildInternalLoader(context: Context): InternalLoader {
            return InternalLoader(context)
        }
    }
}