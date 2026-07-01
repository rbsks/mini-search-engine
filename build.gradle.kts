plugins {
    kotlin("jvm") version "2.1.21" apply false
    kotlin("plugin.spring") version "2.1.21" apply false
    id("org.springframework.boot") version "3.4.13" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "com.minisearchengine"
version = "0.0.1-SNAPSHOT"
description = "mini-search-engine"

allprojects {
    group = "com.minisearchengine"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
