import org.zaproxy.gradle.addon.AddOnPlugin
import org.zaproxy.gradle.addon.AddOnStatus



plugins {
	id("java")
    id("com.diffplug.spotless") version "6.21.0"
    id("com.github.ben-manes.versions") version "0.38.0"
    `java-library`
    id("org.zaproxy.add-on") version "0.5.0"
}

repositories {
    mavenCentral()
}

spotless {
    java {
      //  licenseHeaderFile("./gradle/spotless/License.java")
        googleJavaFormat().aosp()
    }
}

tasks.compileJava {
    dependsOn("spotlessApply")
}

tasks.withType<JavaCompile>().configureEach { options.encoding = "utf-8"}


dependencies {
    // This dependency is used by the application.
    // implementation(libs.guava)
    zap("org.zaproxy:zap:2.12.0")
}


// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}


version = "0.0.1"
description = "Connecting frida and zap"

zapAddOn {
    addOnName.set("Friza")

    manifest {
        author.set("Aryan kashyaparyan565@gmail.com")
        author.set("Ganesh ganeshavp2003@gmail.com")
    }
}
