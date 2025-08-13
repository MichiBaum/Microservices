package com.michibaum.babymetal_service.app

import org.springframework.stereotype.Controller
import org.springframework.ui.Model

@Controller
class IndexController {

    fun showIndexHtml(model: Model): String {
        return "index"
    }
    
}