package com.michibaum

import com.tngtech.archunit.library.GeneralCodingRules

class StandardRules {
    
    companion object{
        val forbiddenStandardStreamsAccess = GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
            .allowEmptyShould(true)
    }
    
}