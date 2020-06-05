package com.moumoux.quinine

object Common {
    val primes = List(1000) { nthPrime(it + 1) }

    fun nthPrime(n: Int): Int {
        var num = 2
        val primes = ArrayList<Int>(n)
        primes.add(num)
        while (primes.size < n) {
            check@ while (true) {
                for (i in 0..primes.size) {
                    if (num % primes[i] == 0) {
                        num += 1
                        break
                    }
                    if (i == primes.size - 1) {
                        break@check
                    }
                }
            }
            primes.add(num)
            num += 1
        }
        return primes[n - 1]
    }
}