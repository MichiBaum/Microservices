package com.michibaum.lifemanagementbackend.service

import com.michibaum.lifemanagementbackend.converter.toDto
import com.michibaum.lifemanagementbackend.domain.CheckListItem
import com.michibaum.lifemanagementbackend.dtos.ReturnCheckListItem
import com.michibaum.lifemanagementbackend.repository.CheckListItemRepository
import org.springframework.stereotype.Service

@Service
class CheckListItemService(
    private val checkListItemRepository: CheckListItemRepository
) {

    fun convertToDtoTree(checkListItems: List<CheckListItem>): List<ReturnCheckListItem> =
        checkListItems.map { convertToDto(it) }

    private fun convertToDto(checkListItem: CheckListItem): ReturnCheckListItem =
        checkListItem.toDto().also { _checkListItem ->
            checkListItemRepository.findByParentCheckListItem(checkListItem)
                .forEach { _childCheckListItem ->
                    _checkListItem.checkListItems.add(
                        convertToDto(_childCheckListItem)
                    )
                }

        }
}
