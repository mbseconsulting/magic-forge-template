/*
 * macrodata-renaming-license
 * Copyright © 2025 Mark S
 * Contact information: mark.s@lumon.com / https://www.lumon.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
plugins {
    `maven-publish`
    id("com.diffplug.spotless") version "7.2.1" // https://github.com/diffplug/spotless
    id("jacoco") // https://docs.gradle.org/current/userguide/jacoco_plugin.html
    id("com.mbseconsulting.magicforge") version "1.0.0" // https://github.com/mbseconsulting/magic-forge
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
    // Declare Resources dependencies
    catiaMagic("com.3ds.resources:product-line-engineering:2024.2.0")
    // Declare Plugin dependencies
    catiaMagic("com.3ds.plugins:sysml:2024.2.0")
    // Additional Java/Kotlin code dependencies
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")

    // Testing dependencies
    testImplementation(enforcedPlatform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.platform:junit-platform-suite")
}
