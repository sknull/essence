package io.github.cdimascio.essence

import org.junit.jupiter.api.Test
import java.io.File

class ReeperbahnTest {

    @Test
    fun readHtml() {
        val file = File(ClassLoader.getSystemResource("html/Reeperbahn-Festival-Blog-Tag-2-Von-einem-Konzert-zum-naechsten,reeperbahnfestival2458.html").toURI())
        val html = file.readText()

        val data = Essence.extract(html)

        println("text: ${data.text}")
        println("videos: ${data.videos}")
        println("language: ${data.language}")
        println("authors: ${data.authors}")
        println("title: ${data.title}")
        println("softTitle: ${data.softTitle}")
        println("copyright: ${data.copyright}")
        println("date: ${data.date}")
        println("publisher: ${data.publisher}")
        println("description: ${data.description}")
        println("favicon: ${data.favicon}")
        println("image: ${data.image}")
        println("links: ${data.links}")
        println("canonicalLink: ${data.canonicalLink}")
        println("keywords: ${data.keywords}")
        println("tags: ${data.tags}")
    }
}
