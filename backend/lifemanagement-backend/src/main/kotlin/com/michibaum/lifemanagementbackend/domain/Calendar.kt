package com.michibaum.lifemanagementbackend.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity(name = "CALENDAR")
class Calendar(

    @Column(nullable = false, name = "name")
    var name: String,

    @Column(nullable = false, name = "description")
    var description: String

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    @ManyToMany(mappedBy = "calendars")
    var users: MutableList<User> = arrayListOf()

}
