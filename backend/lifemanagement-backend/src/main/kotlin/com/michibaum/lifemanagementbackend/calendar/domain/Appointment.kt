package com.michibaum.lifemanagementbackend.calendar.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity(name = "APPOINTMENT")
class Appointment(

    @Column(nullable = false, name = "name")
    var name: String,

    @Column(nullable = false, name = "description")
    var description: String,

    @Column(nullable = false, name = "location")
    var location: String = "",

    @Column(nullable = false, name = "beginDate")
    var beginDate: Long,

    @Column(nullable = false, name = "endDate")
    var endDate: Long,

    @ManyToOne
    @JoinColumn(name = "calendar_id", nullable = false)
    var calendar: Calendar

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

}
