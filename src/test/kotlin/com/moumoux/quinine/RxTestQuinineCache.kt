package com.moumoux.quinine

import com.moumoux.quinine.reactive.Cache
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.await
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors
import kotlin.random.Random


class RxTestQuinineCache {

    private val loaderExecutor = Executors.newSingleThreadExecutor()
    private val cache: Cache<Int, Int> = Quinine
        .newBuilder()
        .maximumSize(500)
        .rxBuild()

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
        runBlocking {
            assertEquals(Common.primes[index - 1], cache.get(index) {
                Single.create { emitter ->
                    loaderExecutor.execute { emitter.onSuccess(Common.nthPrime(it)) }
                }
            }.await())
        }
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
                        assertEquals(Common.primes[index - 1], cache.get(index) {
                            Single.create { emitter ->
                                loaderExecutor.execute { emitter.onSuccess(Common.nthPrime(it)) }
                            }
                        }.await())
                    }
                }
            }
            jobs.forEach { it.join() }
        }
    }

    @Test
    fun getAllPresent() {
        val indexes = List(100) { Random.nextInt(1, 1000) }
        List(500) { Random.nextInt(1, 1000) }
            .forEach { cache.put(it, Single.just(Common.primes[it - 1])) }
        runBlocking {
            cache.getAllPresent(indexes).forEach { (k, v) ->
                assertEquals(Common.primes[k - 1], v.await())
            }
        }
    }

}