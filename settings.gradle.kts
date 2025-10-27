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
rootProject.name = "macrodata-renaming-example"

pluginManagement {
    repositories {
        maven {
            url = uri("https://nexus.lumon.com/repository/maven-gradle-plugin-snapshots/")
            credentials {
                username = providers.gradleProperty("com.mbseconsulting.magicforge.credentials.username").orNull
                password = providers.gradleProperty("com.mbseconsulting.magicforge.credentials.password").orNull
            }
        }
        maven {
            url = uri("https://nexus.lumon.com/repository/maven-gradle-plugin-releases/")
            credentials {
                username = providers.gradleProperty("com.mbseconsulting.magicforge.credentials.username").orNull
                password = providers.gradleProperty("com.mbseconsulting.magicforge.credentials.password").orNull
            }
        }
        gradlePluginPortal()
        mavenCentral()
    }
}
