// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.sonarqube") version "4.4.1.3373"
    alias(libs.plugins.android.application) apply false
}
sonarqube {
    properties {
        property("sonar.projectKey", "SleepyBaby")
        property("sonar.organization", "amer240")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.login", System.getenv("SONAR_TOKEN"))
    }
}