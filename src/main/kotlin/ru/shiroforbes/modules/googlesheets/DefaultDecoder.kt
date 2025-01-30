package ru.shiroforbes.modules.googlesheets

import kotlin.reflect.KType
import kotlin.reflect.jvm.jvmErasure

class DefaultDecoder : Decoder {
    private val conversionClass = Class.forName("kotlin.text.StringsKt")
    override fun supports(type: KType): Boolean {
        if (type == String::class) {
            return true
        }

        val typeName = type.jvmErasure.simpleName
        val conversionFuncName = "to${typeName}OrNull"
        try {
            conversionClass.getMethod(conversionFuncName, String::class.java)
        } catch (e: NoSuchMethodException) {
            return false
        }
        return true
    }

    override fun convert(string: String, type: KType): Any? {
        if (type == String::class) {
            return string
        }
        val typeName = type.jvmErasure.simpleName
        val conversionFuncName = "to${typeName}OrNull"
        val conversionFunc =
            try {
                conversionClass.getMethod(conversionFuncName, String::class.java)
            } catch (e: NoSuchMethodException) {
                return null
            }
        conversionFunc.isAccessible = true
        val res = conversionFunc.invoke(null, string)
        return res
    }
}