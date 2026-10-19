plugins {
    id("dev.kikugie.loom-back-compat")
    kotlin("jvm")
    idea
}

version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = "${property("mod.id") as String}-fabric"

val requiredJava: JavaVersion = when {
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
    fun fapi(vararg modules: String) =
        modules.forEach { modImplementation(fabricApi.module(it, sc.properties["deps.fabric_api"])) }

    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.fabric_language_kotlin")}")
    modImplementation("BosLib:${sc.current.version}-fabric")
    fapi(
        "fabric-lifecycle-events-v1",
        "fabric-resource-loader-v0",
        "fabric-content-registries-v0",
        "fabric-registry-sync-v0",
        "fabric-key-mapping-api-v1"
    )
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
    val awFile = rootProject.file("src/main/resources/bosui.accesswidener")
    if (awFile.exists()) accessWidenerPath = awFile

    runs.all { runDirectory.set(rootProject.file("run")) }
    runs {
        named("server") {
            generateRunConfig.set(false)
        }
    }

    decompilerOptions.named("vineflower") { options.put("mark-corresponding-synthetics", "1") }
}

tasks.processResources {
    val currentVersion = project.version.toString()
    val mcCompat = sc.properties["mod.mc_compat"] as String
    val fabricLoader = project.property("deps.fabric_loader") as String
    val modId = project.property("mod.id") as String
    val modName = project.property("mod.name") as String

    inputs.property("version", currentVersion)
    inputs.property("mc_compat", mcCompat)
    inputs.property("fabric_loader", fabricLoader)

    filesMatching("fabric.mod.json") {
        expand(
            "version" to currentVersion,
            "mc_compat" to mcCompat,
            "fabric_loader" to fabricLoader,
            "mod_id" to modId,
            "mod_name" to modName
        )
    }

    exclude("META-INF/neoforge.mods.toml")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = requiredJava.majorVersion.toInt()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    val targetVersion = if (requiredJava.majorVersion.toInt() > 21) "21" else requiredJava.majorVersion
    compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(targetVersion))
    compilerOptions.freeCompilerArgs.add("-Xskip-metadata-version-check")
}

java {
    toolchain { languageVersion = JavaLanguageVersion.of(requiredJava.majorVersion) }
    withSourcesJar()
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    description = "Builds mod jars and copies results to `build/libs/{minecraft_version}/`"

    val jarTask = if (tasks.names.contains("remapJar")) tasks.named("remapJar") else tasks.named("jar")
    val sourcesTask = if (tasks.names.contains("remapSourcesJar")) tasks.named("remapSourcesJar") else tasks.named("sourcesJar")
    from(jarTask, sourcesTask)
    into(rootProject.layout.buildDirectory.dir("libs/${sc.current.version}"))
}

val scVersion = sc.current.version
val scProject = sc.current.project
val ideaRunConfigsDir = rootProject.layout.projectDirectory.dir(".idea/runConfigurations").asFile

tasks.register("genIdeaRuns") {
    group = "ide"
    description = "Generates IntelliJ IDEA run configuration for Fabric Client"
    notCompatibleWithConfigurationCache("Generates IDE files")
    doLast {
        ideaRunConfigsDir.mkdirs()
        val runFile = File(ideaRunConfigsDir, "$scVersion-fabric_-_Client.xml")
        runFile.writeText(
            """
            <component name="ProjectRunConfigurationManager">
              <configuration default="false" name="$scVersion-fabric - Client" type="GradleRunConfiguration" factoryName="Gradle">
                <ExternalSystemSettings>
                  <option name="executionName" />
                  <option name="externalProjectPath" value="${'$'}PROJECT_DIR$" />
                  <option name="externalSystemIdString" value="GRADLE" />
                  <option name="scriptParameters" value="" />
                  <option name="taskDescriptions">
                    <list />
                  </option>
                  <option name="taskNames">
                    <list>
                      <option value=":$scProject:runClient" />
                    </list>
                  </option>
                  <option name="vmOptions" />
                </ExternalSystemSettings>
                <ExternalSystemDebugServerProcess>false</ExternalSystemDebugServerProcess>
                <ExternalSystemReattachDebugProcess>true</ExternalSystemReattachDebugProcess>
                <DebugAllEnabled>false</DebugAllEnabled>
                <RunAsTest>false</RunAsTest>
                <method v="2" />
              </configuration>
            </component>
            """.trimIndent()
        )
    }
}
