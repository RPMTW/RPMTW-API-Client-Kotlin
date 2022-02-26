import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    jacoco
    `maven-publish`
}
val jacocoVersion = "0.8.7"
val fuelVersion: String by project
val libraryVersion: String by project
group = "com.rpmtw"
version = libraryVersion
repositories {
    mavenCentral()
}

jacoco {
    toolVersion = jacocoVersion
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        xml.outputLocation.set(file("build/coverage/coverage.xml"))
        csv.required.set(true)
        html.required.set(true)
    }
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        events("passed", "failed", "skipped")
    }

    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("com.github.kittinunf.fuel:fuel:$fuelVersion")
    implementation("com.github.kittinunf.fuel:fuel-gson:$fuelVersion")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:$fuelVersion")
    implementation("com.google.code.gson:gson:2.9.0")
    @Suppress("GradlePackageUpdate")
    implementation("io.socket:socket.io-client:1.0.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.0")
}

application {
    mainClass.set("RPMTWApiClient.kt")
}

tasks {
    build {
        dependsOn(shadowJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = "rpmtw-api-client"
            version = libraryVersion

            from(components["java"])
        }
    }
}