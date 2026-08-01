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
        maven("https://api.modrinth.com/maven") { name = "Modrinth" }
    }
    versionCatalogs {
        val neoforgeVersion = "26.1.2.80"

        // Minecraft/NeoForge/AE2 - the mod can't build or run without these.
        create("core") {
            version("neoforge", neoforgeVersion)
            version("ae2", "26.1.10-beta")
            library("ae2", "org.appliedenergistics", "appliedenergistics2").versionRef("ae2")
        }

        // Optional compat targets (compileOnly) - the mod builds and runs fine without them.
        create("integration") {
            version("jei", "29.16.0.47")
            version("jade", "26.1.8+neoforge")
            library("jei", "mezz.jei", "jei-26.1.2-neoforge").versionRef("jei")
            library("jade", "maven.modrinth", "jade").versionRef("jade")
        }

        // Test-only dependencies.
        create("testlibs") {
            version("junit", "5.14.4")
            version("junit-platform", "1.14.4")
            library("junit-jupiter", "org.junit.jupiter", "junit-jupiter").versionRef("junit")
            library("junit-platform-launcher", "org.junit.platform", "junit-platform-launcher")
                .versionRef("junit-platform")
            library("neoforge-test", "net.neoforged", "testframework").version(neoforgeVersion)
        }
    }
}

rootProject.name = "AppliedSmelting"
