// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.21"
}

group = "de.lgblaumeiser.ptm"
version = "2.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("at.favre.lib:bcrypt:0.9.0")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.2.0.RC2") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.2.0.RC2") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property-jvm:4.2.0.RC2") // for kotest property test
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.4.21")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
