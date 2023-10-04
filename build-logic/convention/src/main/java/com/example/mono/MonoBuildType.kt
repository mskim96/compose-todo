package com.example.mono

/**
 * This is provide configurations type safety.
 */
enum class MonoBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE
}