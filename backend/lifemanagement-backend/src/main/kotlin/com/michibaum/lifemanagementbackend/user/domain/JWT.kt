package com.michibaum.lifemanagementbackend.user.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*


@Entity(name = "jwt")
class JWT(

    @Lob
    @Column(nullable = false, name = "jwt")
    var jwt: String,

    @Column(nullable = false, name = "active")
    var active: Boolean = true,

    @Column(nullable = false, name = "expiresAt")
    var expiresAt: Long

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

}
