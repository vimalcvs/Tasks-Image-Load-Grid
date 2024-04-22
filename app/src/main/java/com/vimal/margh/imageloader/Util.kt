package com.vimal.margh.imageloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream

class Util {
    companion object {
        fun decodeImageFile(f: File, targetWidth: Int, targetHeight: Int): Bitmap? {
            var inputStream = FileInputStream(f)
            val options: BitmapFactory.Options = BitmapFactory.Options()

            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            // scale
            options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight)
            options.inJustDecodeBounds = false
            inputStream = FileInputStream(f)
            val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            return bitmap
        }

        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val width: Int = options.outWidth
            val height: Int = options.outHeight

            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val halfHeight: Int = height / 2
                val halfWidth: Int = width / 2

                while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }
    }
}