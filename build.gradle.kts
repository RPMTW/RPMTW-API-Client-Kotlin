plugins {
    application
    kotlin("multiplatform") version "1.6.10"
    jacoco
}
val jacocoVersion = "0.8.7"
group = "com.rpmtw"
version = "1.0.0"

repositories {
    mavenCentral()
}

jacoco {
    toolVersion = jacocoVersion
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()

            testLogging {
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
                events("passed", "failed", "skipped")
            }
        }
    }
    js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}


tasks.create<JacocoReport>("jacocoJvmTestReport") {
    dependsOn("jvmTest")
    reports {
        xml.required.set(true)
        csv.required.set(true)
        csv.outputLocation.set(file("build/coverage/coverage.csv"))
        html.required.set(true)
    }
    classDirectories.setFrom(file("${buildDir}/classes/kotlin/jvm/main"))
    sourceDirectories.setFrom(files("src/commonMain", "src/jvmMain"))
    executionData.setFrom(files("${buildDir}/jacoco/jvmTest.exec"))
}
