plugins {
    kotlin("jvm")
}

dependencies {
    api(kotlin("stdlib"))
    api(project(":subprojects:security-checklist-core"))
    implementation("org.jsoup:jsoup:1.12.1")
}
