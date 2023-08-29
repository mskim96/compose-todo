plugins {
    id("mono.android.library")
    id("mono.android.hilt")
}

android {
    namespace = "com.example.mono.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}