package com.michibaum

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.library.GeneralCodingRules

class TestRules {

    companion object {

        @ArchTest
        val testLocation = GeneralCodingRules.testClassesShouldResideInTheSamePackageAsImplementation()
        
    }

}