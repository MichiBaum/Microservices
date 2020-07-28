package com.michibaum.lifemanagementbackend.user.domain

import com.fasterxml.jackson.annotation.JsonIgnore
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
    var successfullAuth: Boolean

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L


}
