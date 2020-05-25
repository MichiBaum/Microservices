package com.michibaum.lifemanagementbackend.dtos

data class ReturnCheckListItem(
    var id: Long,
    var name: String,
    var description: String,
    var creationDate: Long,
    var checkListItems: MutableList<ReturnCheckListItem>
)
