plugins {
    java
    id("net.neoforged.moddev")
    id("com.modrinth.minotaur") version "2.9.0"
    id("net.darkhax.curseforgegradle") version "1.3.33"
    id("com.diffplug.spotless") version "7.0.1"
}

val modId = "appliedsmelting"
val minecraftVersion = providers.gradleProperty("minecraftVersion").get()
val modVersion = providers.gradleProperty("modVersion").get()

base.archivesName = "AppliedSmelting-$minecraftVersion"
version = modVersion
group = "dev.excal1bur.appliedsmelting"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
}

dependencies {
    api(core.ae2)
    compileOnly(integration.jei)
    compileOnly(integration.jade)
    testImplementation(testlibs.junit.jupiter)
    testRuntimeOnly(testlibs.junit.platform.launcher)
    testImplementation(testlibs.neoforge.test)
}

neoForge {
    version = core.versions.neoforge.get()

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }

    unitTest {
        enable()
        testedMod = mods.getByName(modId)
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
        from(rootProject.file("licenses/COPYING")) {
            rename { "${it}_$modId" }
        }
        from(rootProject.file("licenses/LICENSE-ASSETS")) {
            rename { "${it}_$modId" }
        }
        from(rootProject.file("licenses/NOTICE")) {
            rename { "${it}_$modId" }
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }

    processResources {
        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filesMatching("META-INF/neoforge.mods.toml") {
            expand(props)
        }
    }
}

// Publishes the release jar to Modrinth. Only runs when explicitly invoked
// (`./gradlew modrinth`), which the publish.yml workflow does on every
// published GitHub release; local builds are unaffected.
modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))
    projectId.set("WF9YE7g7")
    versionNumber.set(modVersion)
    versionName.set("Applied Smelting $modVersion")
    versionType.set("beta")
    uploadFile.set(tasks.jar.get())
    changelog.set(System.getenv("CHANGELOG") ?: "")
    gameVersions.addAll(minecraftVersion)
    loaders.addAll("neoforge")

    dependencies {
        required.project("ae2")
    }
}

// Publishes the release jar to CurseForge. Only runs when explicitly invoked
// (`./gradlew publishCurseForge`), which the publish.yml workflow does on
// every published GitHub release; local builds are unaffected.
tasks.register<net.darkhax.curseforgegradle.TaskPublishCurseForge>("publishCurseForge") {
    apiToken = System.getenv("CURSEFORGE_TOKEN") ?: ""

    val mainFile = upload("1619570", tasks.jar)
    mainFile.changelog = System.getenv("CHANGELOG") ?: ""
    mainFile.changelogType = "markdown"
    mainFile.releaseType = "beta"
    mainFile.addGameVersion(minecraftVersion)
    mainFile.addModLoader("NeoForge")
    mainFile.addRequirement("ae2")
}

spotless {
    kotlinGradle {
        target("*.kts")
        leadingTabsToSpaces(4)
        trimTrailingWhitespace()
        endWithNewline()
    }

    java {
        target("src/**/java/**/*.java")
        endWithNewline()
        leadingTabsToSpaces(4)
        removeUnusedImports()
        palantirJavaFormat("2.96.0")
        toggleOffOn()
        trimTrailingWhitespace()

        // courtesy of diffplug/spotless#240
        custom("noWildcardImports", object : java.io.Serializable, com.diffplug.spotless.FormatterFunc {
            override fun apply(input: String): String {
                if (input.contains("*;\n")) {
                    throw GradleException("No wildcard imports allowed.")
                }

                return input
            }
        })

        bumpThisNumberIfACustomStepChanges(1)
    }
}
