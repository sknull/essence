package de.visualdigits.essence.formatters

import com.fleeksoft.ksoup.Ksoup
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HtmlFormatterTest {

    @Test
    fun testPartition1() {
        val html = """
            <div>
                <span>000</span>
                <p>aaa</p>
                <p>bbb</p>
                <span>111</span>
                <p>ccc</p>
                <p>ddd</p>
                <p>eee</p>
                <p>eee</p>
                <span>222</span>
                <p>fff</p>
                <span>333</span>
            </div>
        """.trimIndent()

        val div = Ksoup.parse(html = html).select("div").firstOrNull()
        val children = div?.children()
        val partitions = children?.partitionByTagName("span")

        assertEquals(7, partitions!!.size)
    }

    @Test
    fun testPartition2() {
        val html = """
            <div>
                <p>aaa</p>
                <p>bbb</p>
                <p>ccc</p>
                <p>ddd</p>
                <p>eee</p>
                <p>eee</p>
                <p>fff</p>
            </div>
        """.trimIndent()

        val div = Ksoup.parse(html = html).select("div").firstOrNull()
        val children = div?.children()
        val partitions = children?.partitionByTagName("span")

        assertEquals(1, partitions!!.size)
    }

    @Test
    fun testPartition3() {
        val html = """
            <div>
                <span>000</span>
                <p>aaa</p>
                <p>bbb</p>
            </div>
        """.trimIndent()

        val div = Ksoup.parse(html = html).select("div").firstOrNull()
        val children = div?.children()
        val partitions = children?.partitionByTagName("span")

        assertEquals(2, partitions!!.size)
    }

    @Test
    fun testPartition4() {
        val html = """
            <div>
                <p>fff</p>
                <span>333</span>
            </div>
        """.trimIndent()

        val div = Ksoup.parse(html = html).select("div").firstOrNull()
        val children = div?.children()
        val partitions = children?.partitionByTagName("span")

        assertEquals(2, partitions!!.size)
    }
}
