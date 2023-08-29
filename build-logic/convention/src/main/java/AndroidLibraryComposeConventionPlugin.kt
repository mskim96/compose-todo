import com.android.build.api.dsl.LibraryExtension
import com.example.mono.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidLibraryComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")
            val extensions = extensions.getByType<LibraryExtension>()
            configureAndroidCompose(extensions)
        }
    }
}