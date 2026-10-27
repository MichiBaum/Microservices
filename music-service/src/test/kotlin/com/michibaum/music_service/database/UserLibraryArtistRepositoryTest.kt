package com.michibaum.music_service.database

import com.michibaum.music_service.TestcontainersTestConfiguration
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(TestcontainersTestConfiguration::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserLibraryArtistRepositoryTest {

    @Autowired
    private lateinit var userLibraryArtistRepository: UserLibraryArtistRepository

    @Autowired
    private lateinit var artistRepository: ArtistRepository

    @Test
    fun `save and findByUserId returns correct entries`() {
        val artist = artistRepository.save(Artist(name = "Pink Floyd", isni = null))
        val userId = "user-abc"

        val userLibraryArtist = userLibraryArtistRepository.save(
            UserLibraryArtist(userId = userId, artist = artist)
        )

        assertNotNull(userLibraryArtist.id)

        val results = userLibraryArtistRepository.findByUserId(userId)
        assertEquals(1, results.size)
        assertEquals("Pink Floyd", results[0].artist.name)

        val exists = userLibraryArtistRepository.existsByUserIdAndArtistId(userId, artist.id!!)
        assertTrue(exists)
    }
}
