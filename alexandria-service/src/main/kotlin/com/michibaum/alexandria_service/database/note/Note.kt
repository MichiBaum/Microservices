package com.michibaum.alexandria_service.database.note

import com.michibaum.alexandria_service.database.IdNullException
import jakarta.persistence.*
import org.hibernate.envers.Audited
import org.hibernate.proxy.HibernateProxy
import java.util.*

@Entity
@Table(name="note")
@Audited
data class Note (

    @Column(name="title")
    val title: String,
    
    @Column(name="text")
    val content: String,

    @Column(name="encrypted")
    val encryped: Boolean,

    @Column(name="belongs_to")
    val belongsTo: String,
    
    @Column(name="deleted")
    val deleted: Boolean = false,
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as Note

        return id != null && id == other.id
    }

    override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id )"
    }
}