plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.21")
    id("io.gitlab.arturbosch.detekt").version("1.0.0-RC14")
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.0.0-RC14")
}

detekt {
    toolVersion = "1.0.0-RC14"
    input = files("src/main/kotlin", "src/test/kotlin")
    filters = ".*/resources/.*,.*/build/.*"

    config = files(projectDir.resolve("detekt-config.yml"))
    buildUponDefaultConfig = true

    baseline = projectDir.resolve("detekt-baseline.xml")

    failFast = false
}
