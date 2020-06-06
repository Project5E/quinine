# Quinine
![build status](https://github.com/Project5E/quinine/workflows/build/badge.svg)
[![Jitpack](https://jitpack.io/v/com.moumoux/quinine.svg)](https://jitpack.io/#com.moumoux/quinine)
![Kotlin 1.3.72](https://img.shields.io/badge/Kotlin-1.3.72-orange)
<a href="https://github.com/Project5E/quinine/wiki">
<img align="right" height="90px" src="https://upload.wikimedia.org/wikipedia/commons/e/e4/Quinine-2D-skeletal.png">
</a>

So far, it's a Kotlin cache library using [Caffeine](https://github.com/ben-manes/caffeine)
as backend which might be change in the future.

Still, working in progress.


## Example

Add this to `build.gradle`:
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
```gradle
	dependencies {
	        implementation 'com.moumoux:quinine:0.0.1'
	}
```

### Usage

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
