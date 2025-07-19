package com.michibaum.authentication_service

import com.michibaum.ArchitectureRules
import com.michibaum.NamingRules
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.michibaum.StandardRules
import com.tngtech.archunit.junit.ArchTests

@AnalyzeClasses(packages = ["com.michibaum"])
class ArchitectureTests {

    @ArchTest
    val standardRules = ArchTests.`in`(StandardRules::class.java)

    @ArchTest
    val namingRules = ArchTests.`in`(NamingRules::class.java)

    @ArchTest
    val architectureRules = ArchTests.`in`(ArchitectureRules::class.java)

}