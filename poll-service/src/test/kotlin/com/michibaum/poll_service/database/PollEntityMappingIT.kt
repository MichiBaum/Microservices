package com.michibaum.poll_service.database

import com.michibaum.poll_service.TestcontainersConfiguration
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import java.time.Instant
import java.time.LocalDate

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestcontainersConfiguration
class PollEntityMappingIT {

    @Autowired
    lateinit var pollRepository: PollRepository

    @Autowired
    lateinit var pollSubmissionRepository: PollSubmissionRepository

    @Autowired
    lateinit var questionAnswerValueRepository: QuestionAnswerValueRepository

    @Test
    fun testSaveAndRetrievePollHierarchy() {
        val poll = Poll(
            title = "Team Availability & Feedback",
            description = "Please fill out your availability and preferences.",
            slug = "avail2026",
            creatorId = "user-creator-123",
            status = PollStatus.PUBLISHED,
            accessPolicy = AccessPolicy.INVITED_ONLY,
            secret = null,
            isAnonymous = false,
            thankYouMessage = "Thank you for participating!",
            redirectUrl = "https://example.com/thanks",
            expiresAt = Instant.now().plusSeconds(86400)
        )

        val invitee = PollInvitee(
            poll = poll,
            userId = "user-voter-456"
        )
        poll.invitees.add(invitee)

        val section = Section(
            poll = poll,
            title = "Section 1: General Info",
            description = "Basic information and scheduling",
            orderIndex = 0
        )
        poll.sections.add(section)

        val questionSingle = Question(
            section = section,
            title = "Which department are you in?",
            type = QuestionType.SINGLE_SELECT,
            isRequired = true,
            orderIndex = 0
        )
        section.questions.add(questionSingle)

        val option1 = QuestionOption(
            question = questionSingle,
            title = "Engineering",
            orderIndex = 0
        )
        val option2 = QuestionOption(
            question = questionSingle,
            title = "Product",
            orderIndex = 1
        )
        questionSingle.options.add(option1)
        questionSingle.options.add(option2)

        val questionDate = Question(
            section = section,
            title = "Select your preferred date",
            type = QuestionType.DATE,
            isRequired = true,
            orderIndex = 1
        )
        section.questions.add(questionDate)

        val dateValidator = QuestionValidator(
            question = questionDate,
            minDate = LocalDate.now(),
            disablePast = true
        )
        questionDate.validator = dateValidator

        val questionNumeric = Question(
            section = section,
            title = "Rate your satisfaction (1-10)",
            type = QuestionType.NUMERICAL,
            isRequired = false,
            orderIndex = 2
        )
        section.questions.add(questionNumeric)

        val numValidator = QuestionValidator(
            question = questionNumeric,
            minValue = 1.0,
            maxValue = 10.0,
            integerOnly = true
        )
        questionNumeric.validator = numValidator

        val savedPoll = pollRepository.save(poll)
        assertNotNull(savedPoll.id)

        val retrievedPoll = pollRepository.findById(savedPoll.idOrThrow()).orElseThrow()
        assertEquals("Team Availability & Feedback", retrievedPoll.title)
        assertEquals("avail2026", retrievedPoll.slug)
        assertEquals(PollStatus.PUBLISHED, retrievedPoll.status)
        assertEquals(AccessPolicy.INVITED_ONLY, retrievedPoll.accessPolicy)
        assertFalse(retrievedPoll.isAnonymous)
        assertEquals(1, retrievedPoll.invitees.size)
        assertEquals(1, retrievedPoll.sections.size)

        val retrievedSection = retrievedPoll.sections[0]
        assertEquals(3, retrievedSection.questions.size)

        val retrievedSingleQ = retrievedSection.questions[0]
        assertEquals(2, retrievedSingleQ.options.size)

        val retrievedDateQ = retrievedSection.questions[1]
        assertNotNull(retrievedDateQ.validator)
        assertTrue(retrievedDateQ.validator!!.disablePast == true)

        val retrievedNumQ = retrievedSection.questions[2]
        assertNotNull(retrievedNumQ.validator)
        assertEquals(1.0, retrievedNumQ.validator!!.minValue)
        assertEquals(10.0, retrievedNumQ.validator!!.maxValue)
    }

    @Test
    fun testSaveAndRetrievePollSubmissionAndAnswers() {
        val poll = Poll(
            title = "Quick Poll",
            slug = "quick123",
            creatorId = "creator-1",
            status = PollStatus.PUBLISHED,
            accessPolicy = AccessPolicy.OPEN
        )

        val section = Section(
            poll = poll,
            title = "Section A",
            orderIndex = 0
        )
        poll.sections.add(section)

        val questionText = Question(
            section = section,
            title = "Your thoughts?",
            type = QuestionType.TEXT,
            isRequired = true,
            orderIndex = 0
        )
        section.questions.add(questionText)

        val textValidator = QuestionValidator(
            question = questionText,
            minLength = 5,
            maxLength = 500,
            formatPreset = FormatPreset.ALPHANUMERIC
        )
        questionText.validator = textValidator

        val savedPoll = pollRepository.save(poll)

        val submission = PollSubmission(
            poll = savedPoll,
            userId = "user-voter-789"
        )

        val answerValue = QuestionAnswerValue(
            submission = submission,
            question = savedPoll.sections[0].questions[0],
            textValue = "Looks great to me!"
        )
        submission.answers.add(answerValue)

        val savedSubmission = pollSubmissionRepository.save(submission)
        assertNotNull(savedSubmission.id)

        val retrievedSubmission = pollSubmissionRepository.findByPollIdAndUserId(savedPoll.idOrThrow(), "user-voter-789")
        assertNotNull(retrievedSubmission)
        assertEquals(1, retrievedSubmission!!.answers.size)
        assertEquals("Looks great to me!", retrievedSubmission.answers[0].textValue)
    }
}
