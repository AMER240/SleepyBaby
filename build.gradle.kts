// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("org.sonarqube") version "4.4.1.3373"
    alias(libs.plugins.android.application) apply false
}
sonar {
  properties {
    property("sonar.projectKey", "AMER240_SleepyBaby")
    property("sonar.organization", "amer240")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}
