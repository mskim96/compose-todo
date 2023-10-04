plugins {
    id("mono.android.feature")
    id("mono.android.library.compose")
}

android {
    namespace = "com.example.mono.feature.tasks"
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.accompanist.permissions)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}