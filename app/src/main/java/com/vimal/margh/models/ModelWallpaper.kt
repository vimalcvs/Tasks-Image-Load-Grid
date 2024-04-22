package com.vimal.margh.models

import java.io.Serializable


class ModelWallpaper(
    var id: Int,
    var previewURL: String,
    var largeImageURL: String,
    var webformatURL: String
) : Serializable