package com.michibaum.poll_service.app.poll

import com.michibaum.poll_service.app.exception.InvalidPollException
import com.michibaum.poll_service.app.exception.PollForbiddenException
import com.michibaum.poll_service.app.exception.PollImmutableException
import com.michibaum.poll_service.app.exception.PollNotFoundException
import com.michibaum.poll_service.app.poll.dto.*
import com.michibaum.poll_service.database.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.Instant
import java.util.UUID

@Service
class PollService(
    private val pollRepository: PollRepository,
    private val pollSubmissionRepository: PollSubmissionRepository,
    private val pollConverter: PollConverter
) {

    private val random = SecureRandom()
    private val slugChars = "abcdefghijklmnopqrstuvwxyz0123456789"

    @Transactional
    fun create(dto: CreatePollDto, creatorId: String): PollDetailDto {
        val slug = generateUniqueSlug()
        val poll: Poll = when (dto.accessPolicy) {
            AccessPolicy.OPEN -> OpenPoll(
                title = dto.title,
                description = dto.description,
                slug = slug,
                creatorId = creatorId,
                status = dto.status,
                isAnonymous = dto.isAnonymous,
                expiresAt = dto.expiresAt
            )
            AccessPolicy.INVITED_ONLY -> {
                val invitePoll = InvitePoll(
                    title = dto.title,
                    description = dto.description,
                    slug = slug,
                    creatorId = creatorId,
                    status = dto.status,
                    isAnonymous = dto.isAnonymous,
                    expiresAt = dto.expiresAt
                )
                dto.inviteeUserIds.forEach { userId ->
                    invitePoll.invitees.add(PollInvitee(poll = invitePoll, userId = userId))
                }
                invitePoll
            }
            AccessPolicy.SECRET_PROTECTED -> {
                val secret = dto.secret?.takeIf { it.isNotBlank() }
                    ?: throw InvalidPollException("Secret is required for SECRET_PROTECTED polls")
                SecretPoll(
                    title = dto.title,
                    description = dto.description,
                    slug = slug,
                    creatorId = creatorId,
                    secret = secret,
                    status = dto.status,
                    isAnonymous = dto.isAnonymous,
                    expiresAt = dto.expiresAt
                )
            }
        }

        val sections = buildSections(dto.sections, poll)
        poll.sections.addAll(sections)

        val saved = pollRepository.save(poll)
        return pollConverter.toDetailDto(saved, 0)
    }

    @Transactional(readOnly = true)
    fun get(id: UUID, creatorId: String): PollDetailDto {
        val poll = pollRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw PollNotFoundException("Poll with id $id not found")
        if (poll.creatorId != creatorId) {
            throw PollForbiddenException()
        }
        val submissionCount = pollSubmissionRepository.countByPollId(id)
        return pollConverter.toDetailDto(poll, submissionCount)
    }

    @Transactional(readOnly = true)
    fun getAll(creatorId: String, status: PollStatus?, search: String?, pageable: Pageable): Page<PollSummaryDto> {
        val page = when {
            status != null && !search.isNullOrBlank() ->
                pollRepository.findAllByCreatorIdAndStatusAndTitleContainingIgnoreCaseAndDeletedAtIsNull(creatorId, status, search.trim(), pageable)
            status != null ->
                pollRepository.findAllByCreatorIdAndStatusAndDeletedAtIsNull(creatorId, status, pageable)
            !search.isNullOrBlank() ->
                pollRepository.findAllByCreatorIdAndTitleContainingIgnoreCaseAndDeletedAtIsNull(creatorId, search.trim(), pageable)
            else ->
                pollRepository.findAllByCreatorIdAndDeletedAtIsNull(creatorId, pageable)
        }

        return page.map { poll ->
            val submissionCount = pollSubmissionRepository.countByPollId(poll.idOrThrow())
            pollConverter.toSummaryDto(poll, submissionCount)
        }
    }

    @Transactional
    fun update(id: UUID, dto: UpdatePollDto, creatorId: String): PollDetailDto {
        val existing = pollRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw PollNotFoundException("Poll with id $id not found")
        if (existing.creatorId != creatorId) {
            throw PollForbiddenException()
        }

        val submissionCount = pollSubmissionRepository.countByPollId(id)
        if (submissionCount > 0 && dto.sections != null) {
            throw PollImmutableException("Cannot modify sections or questions for a poll with existing submissions")
        }

        existing.title = dto.title
        existing.description = dto.description
        existing.isAnonymous = dto.isAnonymous
        existing.expiresAt = dto.expiresAt
        existing.status = dto.status

        if (existing is SecretPoll && dto.secret != null) {
            existing.secret = dto.secret
        }

        if (existing is InvitePoll) {
            existing.invitees.clear()
            dto.inviteeUserIds.forEach { userId ->
                existing.invitees.add(PollInvitee(poll = existing, userId = userId))
            }
        }

        if (dto.sections != null) {
            existing.sections.clear()
            val newSections = buildSections(dto.sections, existing)
            existing.sections.addAll(newSections)
        }

        val saved = pollRepository.save(existing)
        return pollConverter.toDetailDto(saved, submissionCount)
    }

    @Transactional
    fun delete(id: UUID, creatorId: String) {
        val poll = pollRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw PollNotFoundException("Poll with id $id not found")
        if (poll.creatorId != creatorId) {
            throw PollForbiddenException()
        }
        poll.deletedAt = Instant.now()
        pollRepository.save(poll)
    }

    @Transactional
    fun duplicate(id: UUID, creatorId: String): PollDetailDto {
        val source = pollRepository.findByIdAndDeletedAtIsNull(id)
            ?: throw PollNotFoundException("Poll with id $id not found")
        if (source.creatorId != creatorId) {
            throw PollForbiddenException()
        }

        val newSlug = generateUniqueSlug()
        val newPoll: Poll = when (source) {
            is OpenPoll -> OpenPoll(
                title = "${source.title} (Copy)",
                description = source.description,
                slug = newSlug,
                creatorId = creatorId,
                status = PollStatus.DRAFT,
                isAnonymous = source.isAnonymous,
                expiresAt = source.expiresAt
            )
            is InvitePoll -> {
                val copy = InvitePoll(
                    title = "${source.title} (Copy)",
                    description = source.description,
                    slug = newSlug,
                    creatorId = creatorId,
                    status = PollStatus.DRAFT,
                    isAnonymous = source.isAnonymous,
                    expiresAt = source.expiresAt
                )
                source.invitees.forEach { copy.invitees.add(PollInvitee(poll = copy, userId = it.userId)) }
                copy
            }
            is SecretPoll -> SecretPoll(
                title = "${source.title} (Copy)",
                description = source.description,
                slug = newSlug,
                creatorId = creatorId,
                secret = source.secret,
                status = PollStatus.DRAFT,
                isAnonymous = source.isAnonymous,
                expiresAt = source.expiresAt
            )
            else -> error("Unknown poll type")
        }

        source.sections.forEach { srcSec ->
            val newSec = Section(
                poll = newPoll,
                title = srcSec.title,
                description = srcSec.description,
                orderIndex = srcSec.orderIndex
            )
            srcSec.questions.forEach { srcQ ->
                val newQ = Question(
                    section = newSec,
                    title = srcQ.title,
                    description = srcQ.description,
                    type = srcQ.type,
                    isRequired = srcQ.isRequired,
                    orderIndex = srcQ.orderIndex
                )
                val newValidator: QuestionValidator? = when (val v = srcQ.validator) {
                    is TextValidator -> TextValidator(
                        question = newQ,
                        minLength = v.minLength,
                        maxLength = v.maxLength,
                        pattern = v.pattern,
                        formatPreset = v.formatPreset
                    )
                    is NumericalValidator -> NumericalValidator(
                        question = newQ,
                        minValue = v.minValue,
                        maxValue = v.maxValue,
                        integerOnly = v.integerOnly
                    )
                    is DateValidator -> DateValidator(
                        question = newQ,
                        minDate = v.minDate,
                        maxDate = v.maxDate,
                        disablePast = v.disablePast
                    )
                    is MultiSelectValidator -> MultiSelectValidator(
                        question = newQ,
                        minSelected = v.minSelected,
                        maxSelected = v.maxSelected
                    )
                    is MultiEntryValidator -> MultiEntryValidator(
                        question = newQ,
                        minItems = v.minItems,
                        maxItems = v.maxItems
                    )
                    else -> null
                }
                newQ.validator = newValidator
                srcQ.options.forEach { srcOpt ->
                    newQ.options.add(
                        QuestionOption(
                            question = newQ,
                            title = srcOpt.title,
                            description = srcOpt.description,
                            orderIndex = srcOpt.orderIndex
                        )
                    )
                }
                newSec.questions.add(newQ)
            }
            newPoll.sections.add(newSec)
        }

        val saved = pollRepository.save(newPoll)
        return pollConverter.toDetailDto(saved, 0)
    }

    private fun generateUniqueSlug(): String {
        var slug: String
        do {
            slug = (1..8)
                .map { slugChars[random.nextInt(slugChars.length)] }
                .joinToString("")
        } while (pollRepository.existsBySlug(slug))
        return slug
    }

    private fun buildSections(sectionDtos: List<CreateSectionDto>, poll: Poll): MutableList<Section> {
        return sectionDtos.mapIndexed { sIndex, sDto ->
            val section = Section(
                poll = poll,
                title = sDto.title,
                description = sDto.description,
                orderIndex = if (sDto.orderIndex != 0) sDto.orderIndex else sIndex
            )
            val questions = sDto.questions.mapIndexed { qIndex, qDto ->
                val question = Question(
                    section = section,
                    title = qDto.title,
                    description = qDto.description,
                    type = qDto.type,
                    isRequired = qDto.isRequired,
                    orderIndex = if (qDto.orderIndex != 0) qDto.orderIndex else qIndex
                )
                question.validator = buildValidator(qDto.validator, qDto.type, question)

                val options = qDto.options.mapIndexed { oIndex, oDto ->
                    QuestionOption(
                        question = question,
                        title = oDto.title,
                        description = oDto.description,
                        orderIndex = if (oDto.orderIndex != 0) oDto.orderIndex else oIndex
                    )
                }
                question.options.addAll(options)
                question
            }
            section.questions.addAll(questions)
            section
        }.toMutableList()
    }

    private fun buildValidator(dto: ValidatorDto?, type: QuestionType, question: Question): QuestionValidator? {
        if (dto == null) return null
        return when (type) {
            QuestionType.TEXT -> TextValidator(
                question = question,
                minLength = dto.minLength,
                maxLength = dto.maxLength,
                pattern = dto.pattern,
                formatPreset = dto.formatPreset
            )
            QuestionType.NUMERICAL -> NumericalValidator(
                question = question,
                minValue = dto.minValue,
                maxValue = dto.maxValue,
                integerOnly = dto.integerOnly ?: false
            )
            QuestionType.DATE -> DateValidator(
                question = question,
                minDate = dto.minDate,
                maxDate = dto.maxDate,
                disablePast = dto.disablePast ?: false
            )
            QuestionType.MULTI_SELECT -> MultiSelectValidator(
                question = question,
                minSelected = dto.minSelected,
                maxSelected = dto.maxSelected
            )
            QuestionType.MULTI_ENTRY -> MultiEntryValidator(
                question = question,
                minItems = dto.minItems,
                maxItems = dto.maxItems
            )
            QuestionType.SINGLE_SELECT -> null
        }
    }
}
