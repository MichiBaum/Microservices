package com.michibaum.lifemanagementbackend.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity(name = "APPOINTMENT_CATEGORY")
class AppointmentCategory(

    @Column(nullable = false, name = "name")
    var name: String,

    @Column(nullable = false, name = "color")
    var color: String

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

}
