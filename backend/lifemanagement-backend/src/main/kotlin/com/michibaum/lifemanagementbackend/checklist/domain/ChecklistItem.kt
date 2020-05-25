package com.michibaum.lifemanagementbackend.checklist.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.michibaum.lifemanagementbackend.user.domain.User
import java.util.*
import javax.persistence.*
import kotlin.collections.ArrayList

@Entity(name="CHECK_LIST_ITEM")
class ChecklistItem(

    @Column(nullable = false, name = "name")
    var name: String,

    @Column(nullable = false, name = "description")
    var description: String,

    @ManyToOne
    var parentChecklistItem: ChecklistItem? = null

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0L

    @Column(nullable = false, name = "creation_date")
    var creationDate: Long = Date().time

    @ManyToMany(mappedBy = "checklistItems", cascade = [CascadeType.MERGE])
    var users: MutableList<User> = arrayListOf()

}
