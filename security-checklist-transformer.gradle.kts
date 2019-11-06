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
    distributionSha256Sum = "bc03088fc7ecb43181b48367bd7589684340f9ae4ffd108fa717e49f4b0d3544"
}
