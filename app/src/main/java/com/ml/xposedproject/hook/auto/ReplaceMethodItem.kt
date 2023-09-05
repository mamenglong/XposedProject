package com.ml.xposedproject.hook.auto

data class ReplaceMethodItem(
    val cls: String,
    val method: String,
    val value: Any?
)