package com.michibaum.lifemanagementbackend.user.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*
import javax.persistence.*

@Entity(name = "LOGIN_LOG")
class LoginLog(

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Column(nullable = false, name = "ipAddress")
    var ipAddress: String,

    @Column(nullable = false, name = "reqMethod")
    var reqMethod: String,

    @Column(nullable = false, name = "successfullAuth")
    var successfullAuth: Boolean,

    @ManyToMany(cascade = [CascadeType.MERGE], fetch = FetchType.LAZY)
    var httpHeaders: MutableList<HttpHeader> = arrayListOf()

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    @Column(nullable = false, name = "creationDate")
    var creationDate: Long = Date().time

}

@Entity(name = "HttpHeader")
@Table(
    name = "HttpHeader",
    uniqueConstraints = [UniqueConstraint(columnNames = ["name", "value"])]
)
class HttpHeader(

    @Column(nullable = false, name = "name")
    var name: String,

    @Column(nullable = false, name = "value")
    var value: String

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

    @ManyToMany(mappedBy = "httpHeaders")
    var loginLogs: MutableList<LoginLog> = arrayListOf()
}
