package com.michibaum.lifemanagementbackend.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity(name = "PURCHASEBILL")
class Purchasebill(

    @Column(nullable = false, name = "datetime")
    var datetime: Long,

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH], fetch = FetchType.LAZY)
    var prices: MutableList<Price>,

    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH])
    @JoinTable(name = "LOGIN_USER_PURCHASEBILL", joinColumns = [JoinColumn(name = "purchasebill_id")], inverseJoinColumns = [JoinColumn(name = "login_user_id")])
    var users: MutableList<User>

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

}
