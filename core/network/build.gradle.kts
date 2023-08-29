plugins {
    id("mono.android.library")
    id("mono.android.hilt")
    id("kotlinx-serialization")
}

android {
    namespace = "com.example.mono.core.network"
}

dependencies {
    implementation(libs.coil.kt)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}