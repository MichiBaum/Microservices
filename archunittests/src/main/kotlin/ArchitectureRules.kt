package com.michibaum

import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses

class ArchitectureRules {

    companion object {
        
        val databaseNotDependingOnAnythingElse: ArchRule =
            noClasses().that().resideInAPackage("..database..")
                .should().dependOnClassesThat()
                .resideOutsideOfPackages(
                    "java..", 
                    "jakarta.persistence..", 
                    "org.jetbrains.annotations..", // TODO replace jetbrains annotations with jspecify (https://jspecify.dev/docs/user-guide/) when spring release Spring Framework 7 / Spring Boot 4.0
                    "org.springframework.data.jpa.repository..",
                    "org.springframework.data.repository..",
                    "..database..", 
                    "kotlin.."
                )
                .allowEmptyShould(true)
        
    }

}