# Quinine

![build status](https://github.com/Project5E/quinine/workflows/build/badge.svg)
![Kotlin 1.3.72](https://img.shields.io/badge/Kotlin-1.3.72-orange)

So far, it's a Kotlin cache library using [Caffeine](https://github.com/ben-manes/caffeine)
as backend which might be change in the future.

Still, working in progress.


## Example

```kotlin
val cache: QuinineCache<Int, Int> = Quinine
        .newBuilder()
        .maximumSize(500)
        .build()
```
```kotlin
val cache: QuinineLoadingCache<Int, Int> = Quinine
        .newBuilder()
        .maximumSize(500)
        .build { loadingFun(it) }
```