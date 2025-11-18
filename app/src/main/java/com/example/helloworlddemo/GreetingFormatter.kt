package com.example.helloworlddemo

import java.util.Locale

/**
 * 提供可单元测试的问候语格式化逻辑。
 */
class GreetingFormatter {

    /**
     * 对按钮展示的默认问候语做简单的规范化处理：
     * 1. 去除前后空白
     * 2. 可选地将首字母大写，便于保持一致的展示效果
     *
     * @throws IllegalArgumentException 当传入的内容为空或仅包含空白时抛出
     */
    fun formatDefaultGreeting(rawMessage: String, uppercaseFirstLetter: Boolean = true): String {
        val normalized = rawMessage.trim()
        require(normalized.isNotEmpty()) { "Greeting message cannot be blank" }

        if (!uppercaseFirstLetter) {
            return normalized
        }

        return normalized.replaceFirstChar { char ->
            if (char.isLowerCase()) {
                char.titlecase(Locale.getDefault())
            } else {
                char.toString()
            }
        }
    }

    /**
     * 根据用户输入生成个性化问候语，供后续功能扩展使用。
     */
    fun buildPersonalGreeting(name: String?, fallbackName: String = "there"): String {
        val cleanedName = name?.trim().orEmpty()
        val finalName = if (cleanedName.isEmpty()) fallbackName else cleanedName
        return "Hello, $finalName!"
    }
}

