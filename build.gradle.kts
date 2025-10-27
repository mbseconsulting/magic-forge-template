plugins {
    `maven-publish`
    id("com.diffplug.spotless") version "7.2.1" // https://github.com/diffplug/spotless
    id("jacoco") // https://docs.gradle.org/current/userguide/jacoco_plugin.html
    id("com.mbseconsulting.magicforge") version "1.0.0-SNAPSHOT" // https://github.com/mbseconsulting/magic-forge
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(
            JavaLanguageVersion.of(magicForge.javaToolChain.orNull ?: 11),
        )
    }
}

dependencies {
}