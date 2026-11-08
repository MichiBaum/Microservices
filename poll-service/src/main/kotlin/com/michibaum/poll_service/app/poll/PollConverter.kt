package com.michibaum.poll_service.app.poll

import com.michibaum.poll_service.app.poll.dto.*
import com.michibaum.poll_service.database.*
import org.springframework.stereotype.Component

@Component
class PollConverter {

    fun toSummaryDto(poll: Poll, submissionCount: Long = 0): PollSummaryDto {
        return PollSummaryDto(
            id = poll.idOrThrow(),
            title = poll.title,
            slug = poll.slug,
            description = poll.description,
            status = poll.status,
            accessPolicy = poll.accessPolicy,
            isAnonymous = poll.isAnonymous,
            expiresAt = poll.expiresAt,
            createdAt = poll.createdAt,
            updatedAt = poll.updatedAt,
            submissionCount = submissionCount
        )
    }

    fun toDetailDto(poll: Poll, submissionCount: Long = 0): PollDetailDto {
        val secret = (poll as? SecretPoll)?.secret
        val invitees = (poll as? InvitePoll)?.invitees?.map { it.userId } ?: emptyList()

        return PollDetailDto(
            id = poll.idOrThrow(),
            title = poll.title,
            slug = poll.slug,
            description = poll.description,
            status = poll.status,
            accessPolicy = poll.accessPolicy,
            isAnonymous = poll.isAnonymous,
            expiresAt = poll.expiresAt,
            createdAt = poll.createdAt,
            updatedAt = poll.updatedAt,
            secret = secret,
            inviteeUserIds = invitees,
            sections = poll.sections.map { toSectionDto(it) },
            submissionCount = submissionCount
        )
    }

    fun toSectionDto(section: Section): SectionDto {
        return SectionDto(
            id = section.idOrThrow(),
            title = section.title,
            description = section.description,
            orderIndex = section.orderIndex,
            questions = section.questions.map { toQuestionDto(it) }
        )
    }

    fun toQuestionDto(question: Question): QuestionDto {
        return QuestionDto(
            id = question.idOrThrow(),
            title = question.title,
            description = question.description,
            type = question.type,
            isRequired = question.isRequired,
            orderIndex = question.orderIndex,
            validator = toValidatorDto(question.validator),
            options = question.options.map { toQuestionOptionDto(it) }
        )
    }

    fun toValidatorDto(validator: QuestionValidator?): ValidatorDto? {
        if (validator == null) return null
        return when (validator) {
            is TextValidator -> ValidatorDto(
                minLength = validator.minLength,
                maxLength = validator.maxLength,
                pattern = validator.pattern,
                formatPreset = validator.formatPreset
            )
            is NumericalValidator -> ValidatorDto(
                minValue = validator.minValue,
                maxValue = validator.maxValue,
                integerOnly = validator.integerOnly
            )
            is DateValidator -> ValidatorDto(
                minDate = validator.minDate,
                maxDate = validator.maxDate,
                disablePast = validator.disablePast
            )
            is MultiSelectValidator -> ValidatorDto(
                minSelected = validator.minSelected,
                maxSelected = validator.maxSelected
            )
            is MultiEntryValidator -> ValidatorDto(
                minItems = validator.minItems,
                maxItems = validator.maxItems
            )
            else -> ValidatorDto()
        }
    }

    fun toQuestionOptionDto(option: QuestionOption): QuestionOptionDto {
        return QuestionOptionDto(
            id = option.idOrThrow(),
            title = option.title,
            description = option.description,
            orderIndex = option.orderIndex
        )
    }
}
