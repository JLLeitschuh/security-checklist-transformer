plugins {
    kotlin("jvm")
}

dependencies {
    api(kotlin("stdlib"))
    implementation(project(":subprojects:security-checklist-core"))
    implementation(group = "org.kohsuke", name = "github-api", version = "1.99")
}
