package com.michibaum.poll_service.app.poll

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.michibaum.authentication_library.JwsValidationSuccess
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.permission_library.Permissions
import com.michibaum.poll_service.TestcontainersConfiguration
import com.michibaum.poll_service.app.poll.dto.*
import com.michibaum.poll_service.database.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import tools.jackson.databind.ObjectMapper
import java.time.LocalDate

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class PollControllerIT {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var pollRepository: PollRepository

    @Autowired
    private lateinit var pollService: PollService

    @MockitoBean
    private lateinit var jwsValidator: JwsValidator

    @BeforeEach
    fun setup() {
        `when`(jwsValidator.validate(anyString())).thenReturn(JwsValidationSuccess())
    }

    private fun bearerToken(userId: String = "creator-1", username: String = "testUser"): String {
        val token = JWT.create()
            .withSubject(username)
            .withClaim("userId", userId)
            .withClaim("permissions", listOf(Permissions.POLL_SERVICE.name))
            .sign(Algorithm.none())
        return "Bearer $token"
    }

    @Test
    fun `create should create OpenPoll with sections, questions, and validators`() {
        val createDto = CreatePollDto(
            title = "Customer Feedback 2026",
            description = "Survey on product satisfaction",
            accessPolicy = AccessPolicy.OPEN,
            isAnonymous = false,
            sections = listOf(
                CreateSectionDto(
                    title = "General Questions",
                    description = "Basic info",
                    orderIndex = 0,
                    questions = listOf(
                        CreateQuestionDto(
                            title = "How satisfied are you?",
                            description = "Rating 1 to 10",
                            type = QuestionType.NUMERICAL,
                            isRequired = true,
                            orderIndex = 0,
                            validator = ValidatorDto(minValue = 1.0, maxValue = 10.0, integerOnly = true)
                        ),
                        CreateQuestionDto(
                            title = "Preferred features",
                            type = QuestionType.MULTI_SELECT,
                            isRequired = false,
                            orderIndex = 1,
                            validator = ValidatorDto(minSelected = 1, maxSelected = 3),
                            options = listOf(
                                CreateQuestionOptionDto(title = "Speed", orderIndex = 0),
                                CreateQuestionOptionDto(title = "Design", orderIndex = 1),
                                CreateQuestionOptionDto(title = "Reliability", orderIndex = 2)
                            )
                        ),
                        CreateQuestionDto(
                            title = "Birth Date",
                            type = QuestionType.DATE,
                            isRequired = false,
                            orderIndex = 2,
                            validator = ValidatorDto(minDate = LocalDate.of(1900, 1, 1), disablePast = false)
                        )
                    )
                )
            )
        )

        val json = objectMapper.writeValueAsString(createDto)

        val result = mockMvc.perform(
            post("/api/v1/creator/polls")
                .header("Authorization", bearerToken("creator-1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("Customer Feedback 2026"))
            .andExpect(jsonPath("$.status").value("DRAFT"))
            .andExpect(jsonPath("$.accessPolicy").value("OPEN"))
            .andExpect(jsonPath("$.sections.length()").value(1))
            .andExpect(jsonPath("$.sections[0].questions.length()").value(3))
            .andExpect(jsonPath("$.sections[0].questions[0].validator.minValue").value(1.0))
            .andExpect(jsonPath("$.sections[0].questions[1].options.length()").value(3))
            .andReturn()

        val responseBody = result.response.contentAsString
        val createdPoll = objectMapper.readValue(responseBody, PollDetailDto::class.java)
        assertNotNull(createdPoll.id)
        assertNotNull(createdPoll.slug)
    }

    @Test
    fun `create should create SecretPoll and return secret`() {
        val createDto = CreatePollDto(
            title = "Secret Board Voting",
            accessPolicy = AccessPolicy.SECRET_PROTECTED,
            secret = "SuperSecretPassphrase123"
        )

        mockMvc.perform(
            post("/api/v1/creator/polls")
                .header("Authorization", bearerToken("creator-secret"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.accessPolicy").value("SECRET_PROTECTED"))
            .andExpect(jsonPath("$.secret").value("SuperSecretPassphrase123"))
    }

    @Test
    fun `create should create InvitePoll and return invitee ids`() {
        val createDto = CreatePollDto(
            title = "Team Retro",
            accessPolicy = AccessPolicy.INVITED_ONLY,
            inviteeUserIds = listOf("user-alice", "user-bob")
        )

        mockMvc.perform(
            post("/api/v1/creator/polls")
                .header("Authorization", bearerToken("creator-invite"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.accessPolicy").value("INVITED_ONLY"))
            .andExpect(jsonPath("$.inviteeUserIds.length()").value(2))
    }

    @Test
    fun `getAll should list creator polls and filter by search and status`() {
        pollService.create(
            CreatePollDto(title = "Alpha Testing Poll", status = PollStatus.PUBLISHED),
            "creator-list-test"
        )
        pollService.create(
            CreatePollDto(title = "Beta Testing Poll", status = PollStatus.DRAFT),
            "creator-list-test"
        )

        // Find all
        mockMvc.perform(
            get("/api/v1/creator/polls")
                .header("Authorization", bearerToken("creator-list-test"))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(2))

        // Filter by status
        mockMvc.perform(
            get("/api/v1/creator/polls")
                .header("Authorization", bearerToken("creator-list-test"))
                .param("status", "PUBLISHED")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].title").value("Alpha Testing Poll"))

        // Search by title
        mockMvc.perform(
            get("/api/v1/creator/polls")
                .header("Authorization", bearerToken("creator-list-test"))
                .param("search", "Beta")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.content.length()").value(1))
            .andExpect(jsonPath("$.content[0].title").value("Beta Testing Poll"))
    }

    @Test
    fun `update duplicate and delete should work correctly`() {
        val created = pollService.create(
            CreatePollDto(
                title = "Original Poll",
                sections = listOf(
                    CreateSectionDto(
                        title = "Section 1",
                        questions = listOf(
                            CreateQuestionDto(
                                title = "Text Question",
                                type = QuestionType.TEXT,
                                validator = ValidatorDto(minLength = 2, maxLength = 100)
                            )
                        )
                    )
                )
            ),
            "creator-crud"
        )

        val pollId = created.id

        // Update
        val updateDto = UpdatePollDto(
            title = "Updated Poll Title",
            description = "Updated Description",
            status = PollStatus.PUBLISHED,
            sections = listOf(
                CreateSectionDto(
                    title = "Updated Section",
                    questions = listOf(
                        CreateQuestionDto(
                            title = "Updated Question",
                            type = QuestionType.TEXT,
                            validator = ValidatorDto(minLength = 5, maxLength = 200)
                        )
                    )
                )
            )
        )

        mockMvc.perform(
            put("/api/v1/creator/polls/$pollId")
                .header("Authorization", bearerToken("creator-crud"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Updated Poll Title"))
            .andExpect(jsonPath("$.status").value("PUBLISHED"))
            .andExpect(jsonPath("$.sections[0].title").value("Updated Section"))

        // Duplicate
        val dupResult = mockMvc.perform(
            post("/api/v1/creator/polls/$pollId/duplicate")
                .header("Authorization", bearerToken("creator-crud"))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("Updated Poll Title (Copy)"))
            .andExpect(jsonPath("$.status").value("DRAFT"))
            .andExpect(jsonPath("$.sections.length()").value(1))
            .andReturn()

        val dupPoll = objectMapper.readValue(dupResult.response.contentAsString, PollDetailDto::class.java)
        assertNotEquals(pollId, dupPoll.id)
        assertNotEquals(created.slug, dupPoll.slug)

        // Delete
        mockMvc.perform(
            delete("/api/v1/creator/polls/$pollId")
                .header("Authorization", bearerToken("creator-crud"))
        )
            .andExpect(status().isNoContent)

        // Get after delete should return 404
        mockMvc.perform(
            get("/api/v1/creator/polls/$pollId")
                .header("Authorization", bearerToken("creator-crud"))
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `get another user poll should return 403 Forbidden`() {
        val created = pollService.create(
            CreatePollDto(title = "Private Poll"),
            "owner-user"
        )

        mockMvc.perform(
            get("/api/v1/creator/polls/${created.id}")
                .header("Authorization", bearerToken("unauthorized-user"))
        )
            .andExpect(status().isForbidden)
    }
}
