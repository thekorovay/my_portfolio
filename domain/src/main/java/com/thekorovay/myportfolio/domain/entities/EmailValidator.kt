package com.thekorovay.myportfolio.domain.entities

interface EmailValidator {
    fun isValidEmail(email: String?): Boolean
}