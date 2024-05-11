package com.example.myapplicationtest

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    ///////////////////// BASE DE DATOS /////////////////////
    //// FUNCIONES A TESTEAR ///// DatabaseManager



    ////////////////////////////////////////////////////////
    //// MOCK
    class MainActivity() {
        fun putoElQueLee() : String {
            return "puto"
        }
    }
    ////////////////////////////////////////////////////////


    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun addition_isCorrect2() {
        val puto = MainActivity()
        val stringPuto = puto.putoElQueLee()
        println(stringPuto)
        assertEquals(4, 2 + 2)
    }

}

