package com.michibaum.archunittests

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.freeze.FreezingArchRule.freeze

class NamingRules {
    
    companion object {

        @ArchTest
        val controllerNaming: ArchRule = freeze(classes().that().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .or().areAnnotatedWith("org.springframework.stereotype.Controller")
            .should().haveSimpleNameEndingWith("Controller")
            .allowEmptyShould(true))

        @ArchTest
        val repositoryNaming: ArchRule =
            freeze(classes().that().areAssignableTo("org.springframework.data.repository.Repository")
            .or().areAnnotatedWith("org.springframework.stereotype.Repository")
            .should().haveSimpleNameEndingWith("Repository")
            .allowEmptyShould(true))

        @ArchTest
        val repositoryNaming2: ArchRule = freeze(classes().that().haveSimpleNameEndingWith("Repository")
            .should().beAssignableTo("org.springframework.data.repository.Repository")
            .andShould().beAnnotatedWith("org.springframework.stereotype.Repository")
            .allowEmptyShould(true))
    }
    
}