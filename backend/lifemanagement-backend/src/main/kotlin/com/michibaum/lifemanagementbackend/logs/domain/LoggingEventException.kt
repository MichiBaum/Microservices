package com.michibaum.lifemanagementbackend.logs.domain

import javax.persistence.*

@Entity(name = "LOGGING_EVENT_EXCEPTION")
class LoggingEventException {

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    lateinit var eventId: LoggingEvent

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "i")
    var i: Short? = null

    @Column(name = "trace_line")
    var traceLine: String = ""

}
