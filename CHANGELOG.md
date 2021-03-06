# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.1]
### Fixed
- SuspendCache did not handle the exception in Single

## [0.3.0]
### Added
- Add rxjava `Observable` for invalidate and update cache notify
  ```kotlin
    fun subscribeInvalidate(channel: Observable<K>)
    fun <T : Any> subscribeInvalidate(channel: Observable<T>, transformer: (T) -> K)
    fun <T : Any> subscribeUpdate(channel: Observable<T>, transformer: (T) -> Pair<K, V>)
    fun unsubscribe(channel: Observable<*>)
    // for loading cache
    fun subscribeUpdate(channel: Observable<K>)
  ```

### Changed
- `QuinineCache` changes to `Cache`
- `QuinineLoadingCache` changes to `LoadingCache`
- `QuinineCacheStats` changes to `CacheStats`

## [0.2.0] - 2020-06-09
### Changed
- the coroutine cache get method changes to suspend function 
- the mapping function of coroutine cache changes to suspend function

## [0.1.0] - 2020-06-05
### Added
- Copy and adapt the document from `Caffeine` for reactive part
- Reactive test case

### Changed
- gradle `rootProject.name` change from `Quinine` to `quinine`
- `K` of `cache<K, V>` now have an upper bounding to `Any`, make sure not null

### Fixed
- import name error of `rxQuinineLoadingCache` in `Quinine.kt`
- the logic of `getAllPresent` and `testGetAll` in test case

## [0.0.1] - 2020-06-05
### Added
- Coroutine style QuinineCache
- Reactive style QuinineCache
- Copy and adapt the document from `Caffeine` for coroutine part

[Unreleased]: https://github.com/Project5E/quinine/compare/v0.3.1...HEAD
[0.3.1]: https://github.com/Project5E/quinine/compare/v0.3.0...v0.3.1
[0.3.0]: https://github.com/Project5E/quinine/compare/v0.2.0...v0.3.0
[0.2.0]: https://github.com/Project5E/quinine/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/Project5E/quinine/compare/v0.0.1...v0.1.0
[0.0.1]: https://github.com/Project5E/quinine/releases/tag/v0.0.1