plugins {
    application
    id("com.gradleup.shadow") version "8.3.6"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.javalin:javalin:7.2.2")
    implementation("org.slf4j:slf4j-simple:2.0.17")

    implementation("gg.jte:jte:3.2.1")
    implementation("io.javalin:javalin-rendering-jte:7.2.2")

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("com.h2database:h2:2.2.224")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:42.7.7")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "hexlet.code.App"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
