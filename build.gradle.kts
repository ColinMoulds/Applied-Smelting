plugins {
    java
    id("net.neoforged.moddev")
}

val modId = "ae2smelter"

base.archivesName = modId
version = providers.gradleProperty("modVersion").get()
group = "dev.excal1bur.appliedsmelting"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
}

dependencies {
    api(libs.ae2)
}

neoForge {
    version = libs.versions.neoforge.get()

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        configureEach {
            logLevel = org.slf4j.event.Level.INFO
            sourceSet = sourceSets.main.get()
        }

        create("client") {
            client()
            gameDirectory = file("run/client")
        }

        create("server") {
            server()
            gameDirectory = file("run/server")
        }
    }
}

tasks {
    jar {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_$modId" }
        }
        from(rootProject.file("COPYING")) {
            rename { "${it}_$modId" }
        }
        from(rootProject.file("LICENSE-ASSETS")) {
            rename { "${it}_$modId" }
        }
        from(rootProject.file("NOTICE")) {
            rename { "${it}_$modId" }
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    processResources {
        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(props)
        }
    }
}
