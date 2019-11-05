buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("gradle.plugin.org.gradleweaver.plugins:better-build-file-names:0.0.1")
    }
}
apply(plugin = "org.gradleweaver.plugins.better-build-file-names")

rootProject.name = "security-checklist-transformer"

include(":subprojects")
include(":subprojects:security-checklist-core")
include(":subprojects:security-checklist-parser")
include(":subprojects:security-checklist-application")
