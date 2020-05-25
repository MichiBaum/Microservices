package com.michibaum.lifemanagementbackend.repository

import com.michibaum.lifemanagementbackend.domain.CheckListItem

interface CheckListItemRepository : CustomJpaRepository<CheckListItem, Long>{
    fun findByParentCheckListItem(checkListItem: CheckListItem): List<CheckListItem>
}
