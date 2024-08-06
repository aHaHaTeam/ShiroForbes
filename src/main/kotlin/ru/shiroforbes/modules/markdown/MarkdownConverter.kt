package ru.shiroforbes.modules.markdown

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class MarkdownConverter {
    private val parser = Parser.builder().build()
    private val renderer =
        HtmlRenderer
            .builder()
            .build()

    fun convert(text: String): String = renderer.render(parser.parse(text))
}

fun main() {
    println(MarkdownConverter().convert("![text](/url.png)"))
}
