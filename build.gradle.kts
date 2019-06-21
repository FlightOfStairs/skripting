plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.21")
    id("io.gitlab.arturbosch.detekt").version("1.0.0-RC14")
}

repositories {
    jcenter()
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.apache.commons:commons-exec:1.3")

    testRuntime("org.slf4j:slf4j-simple:1.7.26")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")

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

tasks.withType<Wrapper> {
    gradleVersion = "5.4"
}
