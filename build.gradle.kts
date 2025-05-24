plugins {
  id("org.sonarqube") version "6.2.0.5505"
}

sonar {
  properties {
    property("sonar.projectKey", "AMER240_SleepyBaby")
    property("sonar.organization", "amer240")
    property("sonar.host.url", "https://sonarcloud.io")
  }
}
