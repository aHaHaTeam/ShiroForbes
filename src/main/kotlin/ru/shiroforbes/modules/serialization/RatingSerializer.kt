package ru.shiroforbes.modules.serialization

import com.itextpdf.html2pdf.HtmlConverter
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import ru.shiroforbes.model.Student
import java.io.OutputStream

class RatingSerializer {
    private val templateEngine = TemplateEngine()

    init {
        templateEngine.setTemplateResolver(
            ClassLoaderTemplateResolver().apply {
                prefix = "serialization/templates/thymeleaf/"
                suffix = ".html"
                characterEncoding = "utf-8"
            },
        )
    }

    fun serialize(
        outputStream: OutputStream,
        students: List<Student>,
    ) {
        val templateContext = Context()
        templateContext.setVariable("studentsSortedByRating", students.sortedByDescending { it.rating })
        templateContext.setVariable("studentsSortedByWealth", students.sortedByDescending { it.wealth })
        val fileContent = templateEngine.process("ratings.html", templateContext)

        HtmlConverter.convertToPdf(fileContent, outputStream)
    }
}
