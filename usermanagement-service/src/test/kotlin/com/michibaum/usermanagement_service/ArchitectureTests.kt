package com.michibaum.usermanagement_service

import com.michibaum.ArchitectureRules
import com.michibaum.NamingRules
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.michibaum.StandardRules

@AnalyzeClasses(packages = ["com.michibaum"])
class ArchitectureTests {

    @ArchTest
    val noAccessToStandardStreams = StandardRules.forbiddenStandardStreamsAccess
    
    @ArchTest
    val architectureRules = ArchitectureRules.databaseNotDependingOnAnythingElse
    
    @ArchTest
    val namingRules = NamingRules.controllerNaming
    
    @ArchTest
    val repositoryNaming = NamingRules.repositoryNaming

    @ArchTest
    val repositoryNaming2 = NamingRules.repositoryNaming2
    
}