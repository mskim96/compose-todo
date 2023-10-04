plugins {
    id("mono.android.library")
    id("mono.android.library.compose")
    id("mono.android.hilt")
}

android {
    namespace = "com.example.mono.core.notifications"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.core.ktx)
}