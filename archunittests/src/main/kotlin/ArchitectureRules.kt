package com.michibaum

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.GeneralCodingRules

class ArchitectureRules {

    companion object {

        val databasePackage = "..database.."
        val appPackage = "..app.."
        val apiPackage = "..apis.."
        val configPackage = "..config.."

        @ArchTest
        val databaseNotDependingOnAnythingElse: ArchRule =
            noClasses().that().resideInAPackage(databasePackage)
                .should().dependOnClassesThat()
                .resideOutsideOfPackages(
                    "java..", 
                    "jakarta.persistence..", 
                    "org.jetbrains.annotations..", // TODO replace jetbrains annotations with jspecify (https://jspecify.dev/docs/user-guide/) when spring release Spring Framework 7 / Spring Boot 4.0
                    "org.springframework.data.jpa.repository..",
                    "org.springframework.data.repository..",
                    "org.springframework.stereotype..", // Repository annotation
                    databasePackage, 
                    "kotlin.."
                )
                .allowEmptyShould(true)

        @ArchTest
        val entityClassesLocation: ArchRule =
            classes().that().areAnnotatedWith("jakarta.persistence.Entity")
            .should().resideInAPackage(databasePackage)
            .allowEmptyShould(true)

        @ArchTest
        val repositoryLocation: ArchRule =
            classes().that().areAnnotatedWith("org.springframework.stereotype.Repository")
            .or().areAssignableTo("org.springframework.data.repository.Repository")
            .should().resideInAPackage(databasePackage)

        @ArchTest
        val testLocation = GeneralCodingRules.testClassesShouldResideInTheSamePackageAsImplementation()
        
    }

}