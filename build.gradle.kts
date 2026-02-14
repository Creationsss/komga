import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
  run {
    val kotlinVersion = "2.2.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("kapt") version kotlinVersion
  }
  id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
  id("com.github.ben-manes.versions") version "0.52.0"
}

fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
  val unstableKeyword = listOf("ALPHA", "RC").any { version.uppercase().contains(it) }
  val regex = "^[0-9,.v-]+(-r)?$".toRegex()
  val isStable = stableKeyword || regex.matches(version)
  return unstableKeyword || !isStable
}

group = "org.gotson"

allprojects {
  repositories {
    mavenCentral()
  }
  apply(plugin = "org.jlleitschuh.gradle.ktlint")
  apply(plugin = "com.github.ben-manes.versions")

  tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    // disallow release candidates as upgradable versions from stable versions
    rejectVersionIf {
      isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
    gradleReleaseChannel = "current"
    checkConstraints = true
  }

  configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version = "1.7.1"
    filter {
      exclude("**/generated-src/**")
      exclude("**/generated/**")
    }
  }
}

tasks.wrapper {
  gradleVersion = "8.14.3"
  distributionType = Wrapper.DistributionType.ALL
}

