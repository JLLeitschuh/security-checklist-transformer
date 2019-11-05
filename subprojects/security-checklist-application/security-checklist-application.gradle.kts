plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "org.jlleitschuh.security.checklist.app.ChecklistTransformerKt"
}

dependencies {
    implementation("com.github.ajalt:clikt:2.1.0")
    implementation(project(":subprojects:security-checklist-parser"))
}
