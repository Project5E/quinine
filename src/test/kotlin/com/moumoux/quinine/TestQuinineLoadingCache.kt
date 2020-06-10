package com.moumoux.quinine

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

class TestQuinineLoadingCache {

    private val cache: QuinineLoadingCache<Int, Int> = Quinine
        .newBuilder()
        .maximumSize(500)
        .build { Common.nthPrime(it) }

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
        runBlocking { assertEquals(Common.primes[index - 1], cache.get(index)) }
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
                        assertEquals(Common.primes[index - 1], cache.get(index))
                    }
                }
            }
            jobs.forEach { it.join() }
        }
    }

    @Test
    fun testGetAll() {
        val indexes = List(100) { Random.nextInt(1, 100) }
        List(500) { Random.nextInt(1, 1000) }.forEach(cache::refresh)
        runBlocking {
            cache.getAll(indexes).forEach { (k, v) ->
                assertEquals(Common.primes[k - 1], v)
            }
        }
    }

    @Test
    fun getAllPresent() {
        val indexes = List(100) { Random.nextInt(1, 100) }
        List(500) { Random.nextInt(1, 1000) }.forEach(cache::refresh)
        runBlocking {
            cache.getAllPresent(indexes).forEach { (k, v) ->
                assertEquals(Common.primes[k - 1], v)
            }
        }
    }

}