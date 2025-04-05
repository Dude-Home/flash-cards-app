plugins {
    java
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

apply("tests.gradle.kts")