pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/c0c41n/tangem-sdk-android")
            credentials {
                username = System.getenv("GIT_TANGEM_USER")
                password = System.getenv("GIT_TANGEM_TOKEN")
            }
        }
        google()
        mavenCentral()
    }
}

rootProject.name = "PocTangemAndroid"
include(":app")
