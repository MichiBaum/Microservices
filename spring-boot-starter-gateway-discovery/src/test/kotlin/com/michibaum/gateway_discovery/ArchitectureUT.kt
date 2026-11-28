package com.michibaum.gateway_discovery

import com.michibaum.archunittests.ArchitectureRules
import com.michibaum.archunittests.NamingRules
import com.michibaum.archunittests.StandardRules
import com.michibaum.archunittests.TestRules
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.junit.ArchTests
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.freeze.ViolationStore
import java.util.Properties

class EmptyViolationStore : ViolationStore {
    override fun initialize(properties: Properties) = Unit
    override fun contains(rule: ArchRule): Boolean = true
    override fun save(rule: ArchRule, violations: List<String>) = Unit
    override fun getViolations(rule: ArchRule): List<String> = emptyList()
}

@AnalyzeClasses(packages = ["com.michibaum.gateway_discovery"], importOptions = [ImportOption.DoNotIncludeTests::class])
class ArchitectureUT {

    @ArchTest
    val standardRules = ArchTests.`in`(StandardRules::class.java)

    @ArchTest
    val namingRules = ArchTests.`in`(NamingRules::class.java)

    @ArchTest
    val architectureRules = ArchTests.`in`(ArchitectureRules::class.java)
}

@AnalyzeClasses(packages = ["com.michibaum.gateway_discovery"])
class GatewayDiscoveryTestArchitectureUT {

    @ArchTest
    val testRules = ArchTests.`in`(TestRules::class.java)
}
