package com.michibaum.archunittests

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.GeneralCodingRules
import com.tngtech.archunit.library.freeze.FreezingArchRule.freeze

class TestRules {

    companion object {

        @ArchTest
        val testLocation = freeze(GeneralCodingRules.testClassesShouldResideInTheSamePackageAsImplementation())
        
    }

}