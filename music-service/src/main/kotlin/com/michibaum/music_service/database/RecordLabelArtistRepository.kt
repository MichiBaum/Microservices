package com.michibaum.music_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RecordLabelArtistRepository: JpaRepository<RecordLabelArtistMapping, RecordLabelArtistMappingId> {
}