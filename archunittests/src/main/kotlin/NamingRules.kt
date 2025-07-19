package com.michibaum

import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes

class NamingRules {
    
    companion object {
        val controllerNaming: ArchRule = classes().that().areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
            .or().areAnnotatedWith("org.springframework.stereotype.Controller")
            .should().haveSimpleNameEndingWith("Controller")
            .allowEmptyShould(true)
        
        val repositoryNaming: ArchRule = classes().that().areAssignableTo("org.springframework.data.repository.Repository")
            .should().haveSimpleNameEndingWith("Repository")
            .allowEmptyShould(true)
        
        val repositoryNaming2: ArchRule = classes().that().haveSimpleNameEndingWith("Repository")
            .should().beAssignableTo("org.springframework.data.repository.Repository")
            .allowEmptyShould(true)
    }
    
}