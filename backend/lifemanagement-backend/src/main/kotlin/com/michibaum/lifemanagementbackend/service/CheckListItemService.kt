package com.michibaum.lifemanagementbackend.service

import com.michibaum.lifemanagementbackend.converter.toDto
import com.michibaum.lifemanagementbackend.domain.CheckListItem
import com.michibaum.lifemanagementbackend.dtos.ReturnCheckListItem
import com.michibaum.lifemanagementbackend.repository.CheckListItemRepository
import org.springframework.stereotype.Service

@Service
class CheckListItemService(
    checkListItemRepository: CheckListItemRepository
) {

    private val findChildCheckListItems : (CheckListItem) -> List<CheckListItem> = checkListItemRepository::findByParentCheckListItem

    fun convertToDtoTree(checkListItems: List<CheckListItem>): List<ReturnCheckListItem> {
        val returnCheckListItems = mutableListOf<ReturnCheckListItem>()
        for(checkListItem in checkListItems){
            returnCheckListItems.add(convertToDto(checkListItem))
        }
        return returnCheckListItems
    }

    private fun convertToDto(checkListItem: CheckListItem): ReturnCheckListItem {
        val childCheckListItems: List<CheckListItem> = findChildCheckListItems(checkListItem)
        val returnCheckListItem = checkListItem.toDto()
        for(childCheckListItem in childCheckListItems){
            returnCheckListItem.checkListItems.add(convertToDto(childCheckListItem))
        }
        return returnCheckListItem
    }

}
