package com.vimal.margh.imageloader

import android.widget.ImageView

class Validator {
    private val viewMap: HashMap<ImageView, String> = HashMap()
    private val urlMap: HashMap<String, HashSet<ImageView>> = HashMap()


    fun shouldFetch(url: String): Boolean {
        return !urlMap.containsKey(url)
    }


    fun set(url: String, imageView: ImageView) {
        val oldUrl = viewMap[imageView]

        if (urlMap.containsKey(oldUrl)) {
            urlMap[oldUrl]?.remove(imageView)

            if (urlMap[oldUrl]?.size == 0) {
                urlMap.remove(oldUrl)
            }
        }

        if (!urlMap.containsKey(url)) {
            urlMap[url] = HashSet()
        }
        urlMap[url]?.add(imageView)

        viewMap[imageView] = url
    }


    fun checkBinding(url: String): Boolean {
        return urlMap.containsKey(url)
    }


    fun getImageViewList(url: String): Set<ImageView>? {
        return urlMap[url]
    }


    fun clear(url: String) {
        urlMap[url]?.forEach { imageView ->
            viewMap.remove(imageView)
        }
        urlMap.remove(url)
    }
}