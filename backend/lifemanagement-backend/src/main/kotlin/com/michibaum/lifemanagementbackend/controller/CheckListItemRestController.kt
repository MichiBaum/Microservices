package com.michibaum.lifemanagementbackend.controller

import com.michibaum.lifemanagementbackend.argumentresolver.ArgumentResolver
import com.michibaum.lifemanagementbackend.domain.User
import com.michibaum.lifemanagementbackend.dtos.ReturnCheckListItem
import com.michibaum.lifemanagementbackend.service.CheckListItemService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class CheckListItemRestController(
    private val checkListItemService: CheckListItemService
) {

    @RequestMapping(value = ["/lifemanagement/api/checkListItems"], method = [RequestMethod.GET])
    fun getCheckListItems(@ArgumentResolver currentUser: User): List<ReturnCheckListItem> =
        checkListItemService.convertToDtoTree(
            currentUser.checkListItems
        )

}
