package com.michibaum

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

class NamingRules {
    
    companion object {

        @ArchTest
        val controllerNaming: ArchRule = classes().that().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .or().areAnnotatedWith("org.springframework.stereotype.Controller")
            .should().haveSimpleNameEndingWith("Controller")
            .allowEmptyShould(true)

        @ArchTest
        val repositoryNaming: ArchRule = 
            classes().that().areAssignableTo("org.springframework.data.repository.Repository")
            .or().areAnnotatedWith("org.springframework.stereotype.Repository")
            .should().haveSimpleNameEndingWith("Repository")
            .allowEmptyShould(true)

        @ArchTest
        val repositoryNaming2: ArchRule = classes().that().haveSimpleNameEndingWith("Repository")
            .should().beAssignableTo("org.springframework.data.repository.Repository")
            .andShould().beAnnotatedWith("org.springframework.stereotype.Repository")
            .allowEmptyShould(true)
    }
    
}