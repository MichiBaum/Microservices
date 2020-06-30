package com.michibaum.lifemanagementbackend.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity(name = "PURCHASEBILL")
class Purchasebill(

    @Column(nullable = false, name = "datetime")
    var datetime: Long

//    var store: Store TODO

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

}
