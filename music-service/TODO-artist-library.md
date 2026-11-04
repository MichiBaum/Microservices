# Music Service - Suggested Improvements & Technical Debt

This document tracks identified architectural, performance, and code quality improvements for `music-service` (specifically arising from PR #310 / User-Saved Library Artists feature) for future implementation.

---

## 1. Isolate External Network Calls Outside `@Transactional` Boundary

- **Location:** `com.michibaum.music_service.app.library.MusicLibraryService` (`syncLibraryArtists`)
- **Category:** Performance & Resource Management
- **Problem:** `spotifyApi.myFollowedArtists(credentials)` executes an external HTTP network request within a `@Transactional` method scope. Holding open a database transaction during network I/O consumes connection pool slots unnecessarily and can lead to connection pool exhaustion during latency spikes or high request volume.
- **Proposed Solution:**
  1. Fetch all followed artist data from the Spotify API prior to opening a database transaction.
  2. Extract the database persistence logic (upserting artists and creating library joins) into a separate `@Transactional` helper method or service.

---

## 2. Replace Non-Null Assertions (`!!`) with Null-Safe Operators

- **Location:** `com.michibaum.music_service.app.library.MusicLibraryService` (`createNewSpotifyArtists`)
- **Category:** Code Quality & Null Safety
- **Problem:** Direct non-null assertions (`it.name!!` and `it.id!!`) are used when creating new artist entities. Although preceding validation filters out null elements, non-null assertions violate Kotlin safety best practices and risk `NullPointerException` if preconditions change.
- **Proposed Solution:**
  1. Replace `!!` calls with null-safe fallbacks (e.g. `it.name.orEmpty()`, `requireNotNull(it.id)` with explicit messages, or idiomatic smart casts).

---

## 4. Refactor JPA Entity Data Classes with Lazy Associations

- **Location:** `com.michibaum.music_service.database.UserLibraryArtist`
- **Category:** Database Integrity & Hibernate Best Practices
- **Problem:** `UserLibraryArtist` is declared as a Kotlin `data class` with a `FetchType.LAZY` association to `Artist`. Kotlin auto-generated `equals()` and `hashCode()` evaluate all constructor properties, including lazy associations. Invoking `equals()` or `hashCode()` outside an active transaction can trigger `LazyInitializationException` or unnecessary N+1 queries.
- **Proposed Solution:**
  1. Change entity declaration from `data class` to standard `class`.
  2. Implement explicit `equals()` and `hashCode()` based strictly on entity identity (`id` surrogate key or business key).
