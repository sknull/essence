package de.visualdigits.essence.model

data class ImageEntry(
    val src: String,
    val alt: String? = null,
    val title: String? = null,
    val imageType: ImageType
)
