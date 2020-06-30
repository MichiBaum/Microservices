package com.michibaum.lifemanagementbackend.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity(name = "PRODUCT")
class Product(

    @Column(nullable = false, name = "name")
    var name: String,

    @Column(nullable = false, name = "description")
    var description: String,

    @ManyToMany(mappedBy = "products")
    var stores: MutableList<Store>

) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

}
