package de.visualdigits.essence

import java.io.File

fun readFileFull(path: String): String {
    return File(ClassLoader.getSystemResource(path).toURI()).readLines().joinToString(" ")
}
