// SPDX-FileCopyrightText: 2020 Lars Geyer-Blaumeiser <lars@lgblaumeiser.de>
// SPDX-License-Identifier: MIT

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("org.jetbrains.kotlin.jvm") version "1.3.72"
}

group = "de.lgblaumeiser.ptm"
version = "2.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.1.0.RC2") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.1.0.RC2") // for kotest core jvm assertions
    testImplementation("io.kotest:kotest-property-jvm:4.1.0.RC2") // for kotest property test
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
