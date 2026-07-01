plugins {
    application
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":search-core"))

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass = "com.minisearchengine.benchmark.BenchmarkRunner"
}

tasks.withType<Test> {
    useJUnitPlatform()
}
