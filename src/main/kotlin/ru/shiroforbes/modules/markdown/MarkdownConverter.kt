package ru.shiroforbes.modules.markdown

import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Heading
import org.commonmark.node.Image
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.AttributeProvider
import org.commonmark.renderer.html.HtmlRenderer

class MarkdownConverter {
    private val parser = Parser.builder().build()
    private val renderer =
        HtmlRenderer
            .builder()
            .attributeProviderFactory { ImageAttributeProvider() }
            .build()

    fun convert(text: String): String {
        val ast = parser.parse(text)
        ast.accept(HeadingVisitor())
        return renderer.render(ast)
    }
}

fun main() {
    println(MarkdownConverter().convert("![text](/url.png)"))
}

internal class HeadingVisitor : AbstractVisitor() {
    override fun visit(heading: Heading) {
        heading.level =
            when (heading.level) {
                1 -> 4
                2 -> 5
                else -> 6
            }
        visitChildren(heading)
    }
}

internal class ImageAttributeProvider : AttributeProvider {
    override fun setAttributes(
        node: Node?,
        tagName: String?,
        attributes: MutableMap<String?, String?>,
    ) {
        if (node is Image) {
            attributes["styles"] = "{width: auto; height: auto; max-width: 100%; max-height: auto;}"
        }
    }
}
