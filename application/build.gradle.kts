import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.diffplug.spotless") version "6.12.0"
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

repositories {
    mavenCentral()
}

java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")
    implementation("org.valiktor:valiktor-core:0.12.0")
    implementation("org.postgresql:r2dbc-postgresql:1.0.0.RELEASE")
    implementation("org.flywaydb:flyway-core:9.11.0")
    implementation(project(":domain"))
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
    testImplementation("io.kotest:kotest-assertions-core:5.5.4")
    testImplementation("io.kotest:kotest-property:5.5.4")
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")
    testImplementation("org.testcontainers:postgresql:1.17.6")
    testImplementation(testFixtures(project(":domain")))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    kotlin {
        ktfmt("0.30").dropboxStyle()
    }
}