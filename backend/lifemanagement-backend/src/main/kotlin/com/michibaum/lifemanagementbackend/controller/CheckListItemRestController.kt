package com.michibaum.lifemanagementbackend.controller

import com.michibaum.lifemanagementbackend.argumentresolver.ArgumentResolver
import com.michibaum.lifemanagementbackend.domain.CheckListItem
import com.michibaum.lifemanagementbackend.domain.User
import com.michibaum.lifemanagementbackend.dtos.ReturnCheckListItem
import com.michibaum.lifemanagementbackend.service.CheckListItemService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class CheckListItemRestController(
    checkListItemService: CheckListItemService
){

    private val convertToDtoTree : (checkListItems: MutableList<CheckListItem>) -> List<ReturnCheckListItem> = checkListItemService::convertToDtoTree

    @RequestMapping(value = ["/lifemanagement/api/checkListItems"], method = [RequestMethod.GET])
    fun getCheckListItems(@ArgumentResolver currentUser: User): List<ReturnCheckListItem> = convertToDtoTree(currentUser.checkListItems)

}
