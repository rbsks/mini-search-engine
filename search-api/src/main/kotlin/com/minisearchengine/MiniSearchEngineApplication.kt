package com.minisearchengine

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MiniSearchEngineApplication

fun main(args: Array<String>) {
    runApplication<MiniSearchEngineApplication>(*args)
}
