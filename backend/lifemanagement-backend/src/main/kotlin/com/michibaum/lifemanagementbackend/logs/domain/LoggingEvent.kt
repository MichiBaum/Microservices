package com.michibaum.lifemanagementbackend.logs.domain

import javax.persistence.*

@Entity(name = "LOGGING_EVENT")
class LoggingEvent(

    @Column(nullable = false, name = "timestmp")
    var timestmp: Long = 0,

    @Lob
    @Column(nullable = false, name = "formatted_message")
    var formattedMessage: String = "",

    @Column(nullable = false, name = "logger_name")
    val loggerName: String = "",

    @Column(nullable = false, name = "level_string")
    val levelString: String = "",

    @Column(name = "thread_name")
    val threadName: String? = "",

    @Column(name = "reference_flag")
    val referenceFlag: Short? = null,

    @Column(name = "arg0")
    val arg0: String? = "",

    @Column(name = "arg1")
    val arg1: String? = "",

    @Column(name = "arg2")
    val arg2: String? = "",

    @Column(name = "arg3")
    val arg3: String? = "",

    @Column(nullable = false, name = "caller_filename")
    val callerFilename: String = "",

    @Column(nullable = false, name = "caller_class")
    val callerClass: String = "",

    @Column(nullable = false, name = "caller_method")
    val callerMethod: String = "",

    @Column(nullable = false, name = "caller_line")
    val callerLine: String = "",

    @Column(nullable = false, name = "seen", columnDefinition = "boolean default false")
    var seen: Boolean = false

) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    val eventId: Long = 0L

}
