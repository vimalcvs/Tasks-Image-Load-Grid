package com.vimal.margh.imageloader

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class DiskCache(context: Context) {
    private val cacheDir: File = context.cacheDir

    fun getCacheFile(url: String): File {
        val filename: String = url.hashCode().toString()
        return File(cacheDir, filename)
    }

    fun write(f: File, inputStream: InputStream) {
        val outputStream: OutputStream = FileOutputStream(f)

        outputStream.use {
            inputStream.copyTo(it)
        }
    }
}