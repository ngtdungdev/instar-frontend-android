pluginManagement {
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
        maven { url = uri("https://storage.zego.im/maven")}
        maven {
            url = uri("https://jitpack.io")
        }

        mavenCentral()
    }

}

rootProject.name = "instar-frontend-android"
include(":app")
