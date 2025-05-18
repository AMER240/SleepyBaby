// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.sonarqube") version "4.4.1.3373"
    alias(libs.plugins.android.application) apply false
}
sonar {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
    }
}