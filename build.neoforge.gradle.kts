plugins {
    id("net.neoforged.moddev") version "2.0.148"
    kotlin("jvm")
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-neoforge"

val requiredJava = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    sc.current.parsed >= "1.20.5" -> JavaVersion.VERSION_21
    sc.current.parsed >= "1.18" -> JavaVersion.VERSION_17
    sc.current.parsed >= "1.17" -> JavaVersion.VERSION_16
    else -> JavaVersion.VERSION_1_8
}

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) =
        exclusiveContent {
            forRepository { maven(url) { name = alias } }
            filter { groups.forEach(::includeGroup) }
        }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    implementation("maven.modrinth:kotlin-for-forge:${property("deps.kotlin_for_forge")}")
    implementation("BosLib:${sc.current.version}-neoforge")
}

neoForge {
    version = property("deps.neo_loader") as String

    mods { register("bosui") { sourceSet(sourceSets.main.get()) } }

    runs {
        register("client") { gameDirectory = file("../../run/"); client() }
        register("server") { gameDirectory = file("../../run/"); server() }
    }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    val targetVersion = if (requiredJava.majorVersion.toInt() > 21) "21" else requiredJava.majorVersion
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(targetVersion))
    compilerOptions.freeCompilerArgs.add("-Xskip-metadata-version-check")
}

tasks {
    processResources {
        fun MutableMap<String, String>.register(key: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(key, value)
            set(key, value)
        }

        val props = buildMap {
            register("id", "mod.id")
            register("name", "mod.name")
            register("version", "mod.version")
            register("minecraft", "mod.mc_compat")
            register("neo_version", "deps.neo_loader")
        }

        filesMatching("META-INF/neoforge.mods.toml") { expand(props) }

        val mixinJava = "JAVA_${requiredJava.majorVersion}"
        filesMatching("*.mixins.json") { expand("java" to mixinJava) }

        exclude("fabric.mod.json", "*.accesswidener")
    }

    named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds mod jars and copies results to `build/libs/{minecraft_version}/`"

        from(jar.flatMap { it.archiveFile }, named<Jar>("sourcesJar").flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs/${sc.current.version}"))
    }
}

abstract class NeoForgeMutex : org.gradle.api.services.BuildService<org.gradle.api.services.BuildServiceParameters.None>
val scMutex = gradle.sharedServices.registerIfAbsent("createMinecraftArtifactsMutex", NeoForgeMutex::class.java) { maxParallelUsages.set(1) }
tasks.matching { it.name == "createMinecraftArtifacts" }.configureEach { usesService(scMutex) }
