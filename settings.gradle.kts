pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Mono"
include(":app")

include(":core:common")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:designsystem")
include(":core:model")
include(":core:notifications")
include(":core:network")
include(":core:ui")

include(":feature:tasks")
include(":feature:calendar")
include(":feature:bookmarks")
include(":feature:tasklist")
include(":feature:reminders")
include(":feature:detail")
include(":feature:settings")
