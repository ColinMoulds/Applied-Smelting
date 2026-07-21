pluginManagement {
    plugins {
        id("net.neoforged.moddev") version "2.0.141"
        id("net.neoforged.moddev.repositories") version "2.0.141"
    }
}

plugins {
    id("net.neoforged.moddev.repositories")
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.PREFER_SETTINGS
    versionCatalogs {
        create("libs") {
            version("neoforge", "26.1.2.80")
            version("ae2", "26.1.10-beta")
            library("ae2", "org.appliedenergistics", "appliedenergistics2").versionRef("ae2")
        }
    }
}

rootProject.name = "AE2Smelter"
