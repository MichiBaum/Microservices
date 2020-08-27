package com.michibaum.lifemanagementbackend.user.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*


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
