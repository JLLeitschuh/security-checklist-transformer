plugins {
    application
    kotlin("jvm")
}

application {
    mainClassName = "org.jlleitschuh.security.checklist.app.ChecklistTransformerKt"
}

dependencies {
    implementation("com.github.ajalt:clikt:2.2.0")
    implementation(project(":subprojects:security-checklist-parser"))
    implementation(project(":subprojects:security-checklist-uploader-github"))
}
