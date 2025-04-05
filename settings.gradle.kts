pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

include("flash-cards-api", "flash-cards-ui")

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "flash-cards-app"