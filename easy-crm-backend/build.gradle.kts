plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.spring") version "2.1.21"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.1.21"

    id("com.diffplug.spotless") version "7.0.4"

    id("com.google.devtools.ksp") version "2.1.21-2.0.1"
}

group = "de.nstdspace"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        ktfmt().kotlinlangStyle()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}

repositories {
    mavenCentral()
}

object Versions {
    const val FLYWAY = "11.9.0"
    const val KONVERT = "4.1.0"
    const val KOTLIN_LOGGING = "7.0.7"
    const val APACHE_POI = "5.4.1"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.flywaydb:flyway-core:${Versions.FLYWAY}")

    implementation("io.mcarle:konvert-api:${Versions.KONVERT}")

    implementation("org.apache.poi:poi:${Versions.APACHE_POI}")
    implementation("org.apache.poi:poi-ooxml:${Versions.APACHE_POI}")
    implementation("io.github.oshai:kotlin-logging-jvm:${Versions.KOTLIN_LOGGING}")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:${Versions.FLYWAY}")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    ksp("io.mcarle:konvert:${Versions.KONVERT}")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
