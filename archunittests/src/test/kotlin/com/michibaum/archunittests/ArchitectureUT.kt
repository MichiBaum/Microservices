package com.michibaum.archunittests

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchTests

@AnalyzeClasses(packages = ["com.michibaum"], importOptions = [ImportOption.DoNotIncludeTests::class])
class ArchitectureUT {

    @ArchTest
    val standardRules = ArchTests.`in`(StandardRules::class.java)

    @ArchTest
    val namingRules = ArchTests.`in`(NamingRules::class.java)

    @ArchTest
    val architectureRules = ArchTests.`in`(ArchitectureRules::class.java)

}

@AnalyzeClasses(packages = ["com.michibaum"])
class TestUT {

    @ArchTest
    val testRules = ArchTests.`in`(TestRules::class.java)
}