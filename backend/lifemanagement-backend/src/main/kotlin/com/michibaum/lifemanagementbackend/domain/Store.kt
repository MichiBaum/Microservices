package com.michibaum.lifemanagementbackend.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity(name = "STORE")
class Store(

    @Column(nullable = false, name = "name")
    var name: String,

    @Column(nullable = false, name = "description")
    var description: String,

    @ManyToMany(cascade = [CascadeType.MERGE], fetch = FetchType.LAZY)
    var products: MutableList<Product> = arrayListOf()

//    var purchasebills: MutableList<Purchasebill> TODO

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

}
