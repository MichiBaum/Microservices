package com.michibaum.lifemanagementbackend.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable

@NoRepositoryBean
interface CustomJpaRepository<T, ID : Serializable> : JpaRepository<T, ID>
