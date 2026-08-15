package com.michibaum.admin_service.app.domain

object HosttechAuthContext {
    private val tokenHolder = ThreadLocal<String>()

    fun setToken(token: String) {
        tokenHolder.set(token)
    }

    fun getToken(): String? = tokenHolder.get()

    fun clear() {
        tokenHolder.remove()
    }

    fun <T> withToken(token: String, block: () -> T): T {
        val previous = getToken()
        setToken(token)
        try {
            return block()
        } finally {
            if (previous != null) {
                setToken(previous)
            } else {
                clear()
            }
        }
    }
}
