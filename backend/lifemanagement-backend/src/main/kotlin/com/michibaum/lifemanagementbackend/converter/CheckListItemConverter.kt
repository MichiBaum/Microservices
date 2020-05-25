package com.michibaum.lifemanagementbackend.converter

import com.michibaum.lifemanagementbackend.domain.CheckListItem
import com.michibaum.lifemanagementbackend.dtos.ReturnCheckListItem

fun CheckListItem.toDto() = ReturnCheckListItem(
    id = id,
    name = name,
    description = description,
    creationDate = creationDate,
    checkListItems = mutableListOf() //TODO not correct but why not
)
