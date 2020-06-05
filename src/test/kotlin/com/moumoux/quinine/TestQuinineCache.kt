package com.moumoux.quinine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

class TestQuinineCache {

    private val cache: QuinineCache<Int, Int> = Quinine
        .newBuilder()
        .maximumSize(500)
        .build()

    @BeforeEach
    fun before() {
        println("\n--------------------------------------")
        cache.invalidateAll()
        cache.cleanUp()
    }

    @AfterEach
    fun after() {
        println("Estimated size: ${cache.estimatedSize}")
        println("Hit rate: ${cache.stats.hitRate * 100}%")
        println("Miss rate: ${cache.stats.missRate * 100}%")
        println("--------------------------------------")
    }

    @Test
    fun testGetOne() {
        val index = Random.nextInt(1, 1000)
        runBlocking { assertEquals(Common.primes[index - 1], cache.get(index) { Common.nthPrime(it) }) }
        cache.invalidate(index)
    }

    @Test
    fun concurrentTest() {
        runBlocking {
            val cores = Runtime.getRuntime().availableProcessors()
            println("number of cores: $cores")
            val jobs = List(cores - 1) {
                async(Dispatchers.Default) {
                    val random = Random(it)
                    for (i in 1..2000) {
                        val index = random.nextInt(1, 1000)
                        assertEquals(Common.primes[index - 1], cache.get(index) { Common.nthPrime(it) })
                    }
                }
            }
            jobs.forEach { it.join() }
        }
    }

    @Test
    fun getAllPresent() {
        val indexes = List(1000) { Random.nextInt(1, 1000) }
        runBlocking {
            cache.getAllPresent(indexes).forEach { (k, v) ->
                assertEquals(Common.primes[k], v)
            }
        }
    }

}