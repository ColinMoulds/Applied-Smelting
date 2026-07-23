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
    repositories {
        maven("https://maven.blamejared.com/") { name = "JEI" }
    }
    versionCatalogs {
        create("libs") {
            version("neoforge", "26.1.2.80")
            version("ae2", "26.1.10-beta")
            version("jei", "29.16.0.47")
            library("ae2", "org.appliedenergistics", "appliedenergistics2").versionRef("ae2")
            library("jei", "mezz.jei", "jei-26.1.2-neoforge").versionRef("jei")
        }
    }
}

rootProject.name = "AppliedSmelting"
