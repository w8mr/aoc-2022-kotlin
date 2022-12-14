plugins {
    kotlin("jvm") version "1.7.22"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src/main/kotlin")
        }
    }

    wrapper {
        gradleVersion = "7.6"
    }

    test {
        useJUnitPlatform()
    }
}
