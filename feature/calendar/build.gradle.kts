plugins {
    id("mono.android.feature")
    id("mono.android.library.compose")
}

android {
    namespace = "com.example.mono.feature.calendar"
}

dependencies {
    implementation(libs.androidx.activity.compose)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}