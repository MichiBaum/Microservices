package com.michibaum.poll_service

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class PollServiceApplicationTest {

    @Test
    fun applicationClassInstantiates() {
        val app = PollServiceApplication()
        assertNotNull(app)
    }

}
