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
    lateinit var openPollRepository: OpenPollRepository

    @Autowired
    lateinit var invitePollRepository: InvitePollRepository

    @Autowired
    lateinit var secretPollRepository: SecretPollRepository

    @Autowired
    lateinit var pollSubmissionRepository: PollSubmissionRepository

    @Autowired
    lateinit var questionAnswerRepository: QuestionAnswerRepository

    @Autowired
    lateinit var textAnswerRepository: TextAnswerRepository

    @Autowired
    lateinit var questionValidatorRepository: QuestionValidatorRepository

    @Autowired
    lateinit var textValidatorRepository: TextValidatorRepository

    @Autowired
    lateinit var dateValidatorRepository: DateValidatorRepository

    @Autowired
    lateinit var numericalValidatorRepository: NumericalValidatorRepository

    @Autowired
    lateinit var multiSelectValidatorRepository: MultiSelectValidatorRepository

    @Autowired
    lateinit var multiEntryValidatorRepository: MultiEntryValidatorRepository

    @Test
    fun testSaveAndRetrievePollHierarchy() {
        val invitePoll = InvitePoll(
            title = "Team Availability & Feedback",
            description = "Please fill out your availability and preferences.",
            slug = "avail2026",
            creatorId = "user-creator-123",
            status = PollStatus.PUBLISHED,
            isAnonymous = false,
            thankYouMessage = "Thank you for participating!",
            redirectUrl = "https://example.com/thanks",
            expiresAt = Instant.now().plusSeconds(86400)
        )

        val invitee = PollInvitee(
            poll = invitePoll,
            userId = "user-voter-456"
        )
        invitePoll.invitees.add(invitee)

        val section = Section(
            poll = invitePoll,
            title = "Section 1: General Info",
            description = "Basic information and scheduling",
            orderIndex = 0
        )
        invitePoll.sections.add(section)

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

        val dateValidator = DateValidator(
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

        val numValidator = NumericalValidator(
            question = questionNumeric,
            minValue = 1.0,
            maxValue = 10.0,
            integerOnly = true
        )
        questionNumeric.validator = numValidator

        val questionText = Question(
            section = section,
            title = "Additional comments",
            type = QuestionType.TEXT,
            isRequired = false,
            orderIndex = 3
        )
        section.questions.add(questionText)

        val textValidator = TextValidator(
            question = questionText,
            minLength = 5,
            maxLength = 500,
            formatPreset = FormatPreset.ALPHANUMERIC
        )
        questionText.validator = textValidator

        val questionMultiSelect = Question(
            section = section,
            title = "Select favorite tools",
            type = QuestionType.MULTI_SELECT,
            isRequired = false,
            orderIndex = 4
        )
        section.questions.add(questionMultiSelect)

        val multiSelectValidator = MultiSelectValidator(
            question = questionMultiSelect,
            minSelected = 1,
            maxSelected = 3
        )
        questionMultiSelect.validator = multiSelectValidator

        val questionMultiEntry = Question(
            section = section,
            title = "Suggest topics",
            type = QuestionType.MULTI_ENTRY,
            isRequired = false,
            orderIndex = 5
        )
        section.questions.add(questionMultiEntry)

        val multiEntryValidator = MultiEntryValidator(
            question = questionMultiEntry,
            minItems = 1,
            maxItems = 5
        )
        questionMultiEntry.validator = multiEntryValidator

        val savedPoll = invitePollRepository.save(invitePoll)
        assertNotNull(savedPoll.id)

        val retrievedPoll = pollRepository.findById(savedPoll.idOrThrow()).orElseThrow()
        assertTrue(retrievedPoll is InvitePoll)
        val retrievedInvitePoll = retrievedPoll as InvitePoll
        assertEquals("Team Availability & Feedback", retrievedInvitePoll.title)
        assertEquals("avail2026", retrievedInvitePoll.slug)
        assertEquals(PollStatus.PUBLISHED, retrievedInvitePoll.status)
        assertEquals(AccessPolicy.INVITED_ONLY, retrievedInvitePoll.accessPolicy)
        assertFalse(retrievedInvitePoll.isAnonymous)
        assertEquals(1, retrievedInvitePoll.invitees.size)
        assertEquals(1, retrievedInvitePoll.sections.size)

        val retrievedSection = retrievedInvitePoll.sections[0]
        assertEquals(6, retrievedSection.questions.size)

        val retrievedSingleQ = retrievedSection.questions[0]
        assertEquals(2, retrievedSingleQ.options.size)

        val retrievedDateQ = retrievedSection.questions[1]
        assertNotNull(retrievedDateQ.validator)
        assertTrue(retrievedDateQ.validator is DateValidator)
        assertTrue((retrievedDateQ.validator as DateValidator).disablePast)

        val retrievedNumQ = retrievedSection.questions[2]
        assertNotNull(retrievedNumQ.validator)
        assertTrue(retrievedNumQ.validator is NumericalValidator)
        assertEquals(1.0, (retrievedNumQ.validator as NumericalValidator).minValue)
        assertEquals(10.0, (retrievedNumQ.validator as NumericalValidator).maxValue)
        assertTrue((retrievedNumQ.validator as NumericalValidator).integerOnly)

        val retrievedTextQ = retrievedSection.questions[3]
        assertNotNull(retrievedTextQ.validator)
        assertTrue(retrievedTextQ.validator is TextValidator)
        assertEquals(5, (retrievedTextQ.validator as TextValidator).minLength)
        assertEquals(500, (retrievedTextQ.validator as TextValidator).maxLength)
        assertEquals(FormatPreset.ALPHANUMERIC, (retrievedTextQ.validator as TextValidator).formatPreset)

        val retrievedMultiSelectQ = retrievedSection.questions[4]
        assertNotNull(retrievedMultiSelectQ.validator)
        assertTrue(retrievedMultiSelectQ.validator is MultiSelectValidator)
        assertEquals(1, (retrievedMultiSelectQ.validator as MultiSelectValidator).minSelected)
        assertEquals(3, (retrievedMultiSelectQ.validator as MultiSelectValidator).maxSelected)

        val retrievedMultiEntryQ = retrievedSection.questions[5]
        assertNotNull(retrievedMultiEntryQ.validator)
        assertTrue(retrievedMultiEntryQ.validator is MultiEntryValidator)
        assertEquals(1, (retrievedMultiEntryQ.validator as MultiEntryValidator).minItems)
        assertEquals(5, (retrievedMultiEntryQ.validator as MultiEntryValidator).maxItems)

        // Test querying individual validator repositories
        val foundTextVal = textValidatorRepository.findByQuestionId(retrievedTextQ.idOrThrow())
        assertNotNull(foundTextVal)
        assertEquals(5, foundTextVal!!.minLength)

        val foundDateVal = dateValidatorRepository.findByQuestionId(retrievedDateQ.idOrThrow())
        assertNotNull(foundDateVal)
        assertTrue(foundDateVal!!.disablePast)

        val foundNumVal = numericalValidatorRepository.findByQuestionId(retrievedNumQ.idOrThrow())
        assertNotNull(foundNumVal)
        assertEquals(1.0, foundNumVal!!.minValue)

        val foundMultiSelectVal = multiSelectValidatorRepository.findByQuestionId(retrievedMultiSelectQ.idOrThrow())
        assertNotNull(foundMultiSelectVal)
        assertEquals(1, foundMultiSelectVal!!.minSelected)

        val foundMultiEntryVal = multiEntryValidatorRepository.findByQuestionId(retrievedMultiEntryQ.idOrThrow())
        assertNotNull(foundMultiEntryVal)
        assertEquals(1, foundMultiEntryVal!!.minItems)

        // Test polymorphic questionValidatorRepository
        val allValidators = questionValidatorRepository.findAll()
        assertEquals(5, allValidators.size)
    }

    @Test
    fun testOpenPollAndSecretPollPersistence() {
        val openPoll = OpenPoll(
            title = "Open Community Survey",
            slug = "open-survey",
            creatorId = "creator-open"
        )
        val savedOpenPoll = openPollRepository.save(openPoll)
        assertNotNull(savedOpenPoll.id)
        assertEquals(AccessPolicy.OPEN, savedOpenPoll.accessPolicy)

        val secretPoll = SecretPoll(
            title = "Board Election",
            slug = "board-secret",
            creatorId = "creator-secret",
            secret = "SuperSecretPin123!"
        )
        val savedSecretPoll = secretPollRepository.save(secretPoll)
        assertNotNull(savedSecretPoll.id)
        assertEquals(AccessPolicy.SECRET_PROTECTED, savedSecretPoll.accessPolicy)
        assertEquals("SuperSecretPin123!", savedSecretPoll.secret)

        val retrievedSecret = pollRepository.findById(savedSecretPoll.idOrThrow()).orElseThrow()
        assertTrue(retrievedSecret is SecretPoll)
        assertEquals("SuperSecretPin123!", (retrievedSecret as SecretPoll).secret)
    }

    @Test
    fun testSaveAndRetrievePollSubmissionAndPolymorphicAnswers() {
        val poll = OpenPoll(
            title = "Multi-Type Poll",
            slug = "multi-type",
            creatorId = "creator-1",
            status = PollStatus.PUBLISHED
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

        val questionNum = Question(
            section = section,
            title = "Your score?",
            type = QuestionType.NUMERICAL,
            isRequired = true,
            orderIndex = 1
        )
        section.questions.add(questionNum)

        val questionDate = Question(
            section = section,
            title = "Birthday?",
            type = QuestionType.DATE,
            isRequired = true,
            orderIndex = 2
        )
        section.questions.add(questionDate)

        val savedPoll = pollRepository.save(poll)

        val submission = PollSubmission(
            poll = savedPoll,
            userId = "user-voter-789"
        )

        val textAnswer = TextAnswer(
            submission = submission,
            question = savedPoll.sections[0].questions[0],
            textValue = "Looks great to me!"
        )
        val numAnswer = NumericalAnswer(
            submission = submission,
            question = savedPoll.sections[0].questions[1],
            numericValue = 9.5
        )
        val dateAnswer = DateAnswer(
            submission = submission,
            question = savedPoll.sections[0].questions[2],
            dateValue = LocalDate.of(2026, 11, 8)
        )

        submission.answers.add(textAnswer)
        submission.answers.add(numAnswer)
        submission.answers.add(dateAnswer)

        val savedSubmission = pollSubmissionRepository.save(submission)
        assertNotNull(savedSubmission.id)

        val retrievedSubmission = pollSubmissionRepository.findByPollIdAndUserId(savedPoll.idOrThrow(), "user-voter-789")
        assertNotNull(retrievedSubmission)
        assertEquals(3, retrievedSubmission!!.answers.size)

        val textAnswers = textAnswerRepository.findBySubmissionId(savedSubmission.idOrThrow())
        assertEquals(1, textAnswers.size)
        assertEquals("Looks great to me!", textAnswers[0].textValue)

        val allAnswers = questionAnswerRepository.findBySubmissionId(savedSubmission.idOrThrow())
        assertEquals(3, allAnswers.size)
        assertTrue(allAnswers.any { it is TextAnswer && it.textValue == "Looks great to me!" })
        assertTrue(allAnswers.any { it is NumericalAnswer && it.numericValue == 9.5 })
        assertTrue(allAnswers.any { it is DateAnswer && it.dateValue == LocalDate.of(2026, 11, 8) })
    }
}
