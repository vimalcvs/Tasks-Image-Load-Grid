package com.vimal.margh.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.os.HandlerCompat
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class InternalLoader(
    context: Context
) {
    private val memoryCache: MemoryCache
    private val diskCache: DiskCache
    private val validator: Validator

    private val executorService: ExecutorService
    private val mainThreadHandler: Handler

    init {
        val maxMemorySize = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        memoryCache = MemoryCache(maxMemorySize / 8)
        diskCache = DiskCache(context)
        validator = Validator()

        executorService = Executors.newFixedThreadPool(8)
        mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    }

    internal fun doLoad(requestBuilder: RequestBuilder, imageView: ImageView) {
        val url: String = requestBuilder.url!!
        val thumbnail: Int? = requestBuilder.thumbnailResId

        if (setImageFromMemoryCache(url, imageView)) {
            return
        }

        setImageFromDiskCacheAndRemote(url, imageView, thumbnail)
    }

    private fun setImageFromMemoryCache(url: String, imageView: ImageView): Boolean {
        val bitmap: Bitmap = memoryCache[url] ?: return false
        imageView.setImageBitmap(bitmap)
        return true
    }

    private fun setImageFromDiskCacheAndRemote(url: String, imageView: ImageView, thumbnail: Int?) {
        val shouldFetch: Boolean = validator.shouldFetch(url)

        validator.set(url, imageView)

        if (thumbnail != null) {
            imageView.setImageResource(thumbnail)
        }

        if (shouldFetch) {
            executorService.execute(LoadBitmap(url))
        }
    }

    private inner class LoadBitmap(
        private val url: String,
    ) : Runnable {
        private var targetWidth: Int = 200
        private var targetHeight: Int = 200
        override fun run() {
            if (!validator.checkBinding(url)) {
                return
            }

            var bitmap: Bitmap? = getBitmapFromDiskCache(url)
            if (displayBitmap(bitmap)) {
                return
            }

            bitmap = getBitmapFromRemote(url)
            displayBitmap(bitmap)
        }

        private fun getBitmapFromDiskCache(url: String): Bitmap? {
            val f: File = diskCache.getCacheFile(url)

            if (!f.exists()) {
                return null
            }

            return Util.decodeImageFile(f, targetWidth, targetHeight)
        }

        private fun getBitmapFromRemote(url: String): Bitmap? {
            try {
                val f: File = diskCache.getCacheFile(url)
                val imageUrl = URL(url)
                val conn: HttpURLConnection = imageUrl.openConnection() as HttpURLConnection

                conn.doInput = true
                conn.connect()

                val inputStream: InputStream = conn.inputStream
                inputStream.use {
                    diskCache.write(f, it)
                }

                conn.disconnect()

                return Util.decodeImageFile(f, targetWidth, targetHeight)
            } catch (e: Exception) {
                return null
            }
        }

        private fun displayBitmap(bitmap: Bitmap?): Boolean {
            if (bitmap != null && validator.checkBinding(url)) {
                memoryCache.put(url, bitmap)
                mainThreadHandler.post(DisplayBitmap(url, bitmap))
                return true
            }
            return false
        }
    }

    private inner class DisplayBitmap(
        private val url: String,
        private val bitmap: Bitmap
    ) : Runnable {
        override fun run() {
            validator.getImageViewList(url)?.forEach { imageView ->
                imageView.setImageBitmap(bitmap)
            }

            validator.clear(url)
        }
    }
}