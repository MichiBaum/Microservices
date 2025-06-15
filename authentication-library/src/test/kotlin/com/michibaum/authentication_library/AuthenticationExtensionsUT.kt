package com.michibaum.authentication_library

import com.michibaum.permission_library.Permissions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class AuthenticationExtensionsUT {

    @Test
    fun `without input should return true`(){
        //  GIVEN
        val authorities = mutableListOf(GrantedAuthority { Permissions.ADMIN_SERVICE.name })
        val auth = TestAuthentication(authorities)

        // WHEN
        val result = auth.anyOf()

        // THEN
        Assertions.assertTrue(result)
    }

    @Test
    fun `without input and empty authentication should return true`(){
        //  GIVEN
        val authorities = mutableListOf<GrantedAuthority>()
        val auth = TestAuthentication(authorities)

        // WHEN
        val result = auth.anyOf()

        // THEN
        Assertions.assertTrue(result)
    }

    @Test
    fun `with input and empty authentication should return false`(){
        //  GIVEN
        val authorities = mutableListOf<GrantedAuthority>()
        val auth = TestAuthentication(authorities)

        // WHEN
        val result = auth.anyOf(Permissions.ADMIN_SERVICE)

        // THEN
        Assertions.assertFalse(result)
    }

    @Test
    fun `has exact permission should return true`(){
        //  GIVEN
        val authorities = mutableListOf(GrantedAuthority { Permissions.ADMIN_SERVICE.name })
        val auth = TestAuthentication(authorities)

        // WHEN
        val result = auth.anyOf(Permissions.ADMIN_SERVICE)

        // THEN
        Assertions.assertTrue(result)
    }

    @Test
    fun `is one of permission should return true`(){
        //  GIVEN
        val authorities = mutableListOf(
            GrantedAuthority { Permissions.CHESS_SERVICE_ADMIN.name },
            GrantedAuthority { Permissions.ADMIN_SERVICE.name },
            GrantedAuthority { Permissions.FITNESS_SERVICE.name },
            )
        val auth = TestAuthentication(authorities)

        // WHEN
        val result = auth.anyOf(Permissions.ADMIN_SERVICE)

        // THEN
        Assertions.assertTrue(result)
    }

    @Test
    fun `does not have permission should return false`(){
        //  GIVEN
        val authorities = mutableListOf(
            GrantedAuthority { Permissions.CHESS_SERVICE_ADMIN.name },
            GrantedAuthority { Permissions.ADMIN_SERVICE.name },
            GrantedAuthority { Permissions.FITNESS_SERVICE.name },
        )
        val auth = TestAuthentication(authorities)

        // WHEN
        val result = auth.anyOf(Permissions.REGISTRY_SERVICE)

        // THEN
        Assertions.assertFalse(result)
    }

    @Test
    fun `has exact permissions should return true`(){
        //  GIVEN
        val authorities = mutableListOf(
            GrantedAuthority { Permissions.CHESS_SERVICE_ADMIN.name },
            GrantedAuthority { Permissions.FITNESS_SERVICE.name },
        )
        val auth = TestAuthentication(authorities)

        // WHEN
        val result = auth.anyOf(Permissions.CHESS_SERVICE_ADMIN, Permissions.FITNESS_SERVICE)

        // THEN
        Assertions.assertTrue(result)
    }

    @Test
    fun `has one of permissions should return true`(){
        //  GIVEN
        val authorities = mutableListOf(
            GrantedAuthority { Permissions.CHESS_SERVICE_ADMIN.name },
            GrantedAuthority { Permissions.FITNESS_SERVICE.name },
            GrantedAuthority { Permissions.USERMANAGEMENT_SERVICE_EDIT_OWN_USER.name },
        )
        val auth = TestAuthentication(authorities)

        // WHEN
        val result = auth.anyOf(Permissions.MUSIC_SERVICE, Permissions.FITNESS_SERVICE)

        // THEN
        Assertions.assertTrue(result)
    }


}

class TestAuthentication(private val authoritiesParam: MutableCollection<GrantedAuthority> = mutableListOf()) : Authentication {
    override fun getName(): String = ""
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authoritiesParam
    override fun getCredentials(): Any = ""
    override fun getDetails(): Any = ""
    override fun getPrincipal(): Any = ""
    override fun isAuthenticated(): Boolean = false
    override fun setAuthenticated(isAuthenticated: Boolean) = Unit
    
    /**
     * Has to be the same as in extension method
     * @see Authentication.anyOf
     */
    fun anyOf(vararg permissions: Permissions): Boolean {
        if (permissions.isEmpty())
            return true

        return permissions.any { permission ->
            authorities.any {
                    a -> a.authority == permission.name
            }
        }
    }
}