package com.michibaum.lifemanagementbackend.domain

import javax.persistence.*

@Entity(name = "LOGGING_EVENT_PROPERTY")
class LoggingEventProperty {

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "event_id")
    lateinit var eventId: LoggingEvent

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mapped_key")
    var mappedKey: String = ""

    @Lob
    @Column(name = "mapped_value")
    var mappedValue: String = ""

}
