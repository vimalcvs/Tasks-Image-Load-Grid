package com.vimal.margh.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vimal.margh.R
import com.vimal.margh.databinding.ActivityDetailBinding
import com.vimal.margh.imageloader.ImageLoader
import com.vimal.margh.models.ModelWallpaper
import com.vimal.margh.util.Constant.EXTRA_KEY
import com.vimal.margh.util.Utils


class ActivityDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.ivBack.setOnClickListener { finish() }

        @Suppress("DEPRECATION")
        val modelList = intent.getSerializableExtra(EXTRA_KEY) as ModelWallpaper?
        if (modelList != null) {

            try {
                ImageLoader.with(this)
                    .load(modelList.webformatURL)
                    .thumbnail(R.drawable.ic_placeholder)
                    .into(binding.ivImage)
            } catch (e: Exception) {
                Utils.getErrors(e)
            }

        } else {
            Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show()
        }
    }
}