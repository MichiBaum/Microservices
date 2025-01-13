package com.michibaum.chess_service.app.person

import com.michibaum.chess_service.TestcontainersConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class PersonControllerIT {

    @Autowired
    lateinit var mockMvc: MockMvc



}
