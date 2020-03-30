import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.diffplug.gradle.spotless") version "3.24.3"
    id("org.jetbrains.kotlin.jvm").version("1.3.21").apply(false)
}

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

allprojects {
    apply(plugin = "com.diffplug.gradle.spotless")

    spotless {
        kotlinGradle {
            ktlint()
        }
    }
}

allprojects {
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.named("wrapper", Wrapper::class) {
    gradleVersion = "5.6.2"
    distributionType = Wrapper.DistributionType.ALL
    distributionSha256Sum = "027fdd265d277bae65a0d349b6b8da02135b0b8e14ba891e26281fa877fe37a2"
}
