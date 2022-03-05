package com.rpmtw.rpmtw_api_client.utilities

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object Utilities {
    @JvmStatic
    fun <T> jsonDeserialize(json: String, className: Class<T>, gson: Gson = Gson()): T {
        val jsonObject: JsonObject = JsonParser.parseString(json).asJsonObject
        jsonObject["data"].let {
            return gson.fromJson(it, className)
        }
    }

    @JvmStatic
    @Suppress("DuplicatedCode")
    fun markdownToMinecraftFormatting(source: String): String {
        val result = StringBuilder()
        var bold = false
        var italic = false
        var strikethrough = false
        var underline = false

        val resetCode = "§r"
        val boldCode = "§l"
        val strikethroughCode = "§m"
        val underlineCode = "§n"
        val italicCode = "§o"

        var i = 0
        while (i < source.length) {
            val nextChar: Char? = source.getOrNull(i + 1)
            when (source[i]) {
                '*' -> {
                    if (nextChar == '*') {
                        if (bold) {
                            result.append(resetCode)
                            result.append(if (italic) italicCode else "")
                            result.append(if (strikethrough) strikethroughCode else "")
                            result.append(if (underline) underlineCode else "")
                        } else {
                            result.append(boldCode)
                        }
                        bold = !bold
                        i += 1
                        continue
                    }
                    if (italic) {
                        result.append(resetCode)
                        result.append(if (bold) boldCode else "")
                        result.append(if (strikethrough) strikethroughCode else "")
                        result.append(if (underline) underlineCode else "")
                    } else {
                        result.append(italicCode)
                    }
                    italic = !italic
                }
                '~' -> {
                    if (nextChar == '~') {
                        if (strikethrough) {
                            result.append(resetCode)
                            result.append(if (bold) boldCode else "")
                            result.append(if (italic) italicCode else "")
                            result.append(if (underline) underlineCode else "")
                        } else {
                            result.append(strikethroughCode)
                        }
                        strikethrough = !strikethrough
                        i += 1
                        continue
                    }
                    result.append("~")
                }
                '_' -> {
                    if (nextChar == '_') {
                        if (underline) {
                            result.append(resetCode)
                            result.append(if (bold) boldCode else "")
                            result.append(if (italic) italicCode else "")
                            result.append(if (strikethrough) strikethroughCode else "")
                        } else {
                            result.append(underlineCode)
                        }
                        underline = !underline
                        i += 1
                        continue
                    } else {
                        if (italic) {
                            result.append(resetCode)
                            result.append(if (bold) boldCode else "")
                            result.append(if (strikethrough) strikethroughCode else "")
                            result.append(if (underline) underlineCode else "")
                        } else {
                            result.append(italicCode)
                        }
                        italic = !italic
                    }
                }
                '\\' -> when (source[++i]) {
                    '*' -> result.append("*")
                    '~' -> result.append("~")
                    '_' -> result.append("_")
                    '\\' -> result.append("\\")
                    else -> result.append("\\").append(source[i])
                }
                else -> result.append(source[i])
            }
            i += 1
        }

        if (bold) {
            val index = result.lastIndexOf(boldCode)
            result.delete(index, index + 2)
            result.insert(index, "**")
        }
        if (italic) {
            val index = result.lastIndexOf(italicCode)
            result.delete(index, index + 2)
            result.insert(index, "*")
        }
        if (strikethrough) {
            val index = result.lastIndexOf(strikethroughCode)
            result.delete(index, index + 2)
            result.insert(index, "~~")
        }
        if (underline) {
            val index = result.lastIndexOf(underlineCode)
            result.delete(index, index + 2)
            result.insert(index, "_")
        }

        return result.toString()
    }
}