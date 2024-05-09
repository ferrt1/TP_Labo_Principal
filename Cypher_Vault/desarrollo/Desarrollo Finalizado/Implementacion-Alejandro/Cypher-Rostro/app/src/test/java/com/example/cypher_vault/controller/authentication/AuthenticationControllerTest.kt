package com.example.cypher_vault.controller.authentication

import org.junit.Assert.*
import org.junit.Test

class AuthenticationControllerTest {

    @Test
    fun validateMailTest() {
        val email = "spectre@spectre.com"
        val ret = validateMail(email)
        assertEquals(true,ret)
    }

    @Test
    fun validateMailTest2() {
        val email = "spectre%&!@spectre.com"
        val ret = validateMail(email)
        assertEquals(true,ret)
    }

    @Test
    fun validateMailTest3() {
        val email = "spectre%&!@spectre.com"
        val ret = validateMail2(email)
        assertEquals(true,ret)
    }

    @Test
    fun validateMailTest4() {
        val email = "spectre@spectre.com"
        val ret = validateMail2(email)
        assertEquals(true,ret)
    }

    //pertenecientes a la clase
    fun validateMail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateMail2(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }
}