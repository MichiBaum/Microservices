package com.michibaum.lifemanagementbackend.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity(name = "PRICE")
class Price(

    @Column(nullable = false, name = "product_id")
    var product_id: Long,

    @Column(nullable = false, name = "store_id")
    var store_id: Long,

    @Column(nullable = false, name = "price")
    var price: Double,

    @Column(nullable = false, name = "from_Date")
    var from_Date: Long,

    @Column(nullable = false, name = "to_Date")
    var to_Date: Long
) {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L

}
