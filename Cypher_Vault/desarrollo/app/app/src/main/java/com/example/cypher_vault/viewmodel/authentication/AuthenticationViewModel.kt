package com.example.cypher_vault.viewmodel.authentication

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthenticationViewModel : ViewModel() {

    private val _navigateTo = MutableStateFlow<String?>(null)
    val navigateTo: StateFlow<String?> get() = _navigateTo


    private fun navigateToCamera() {
        _navigateTo.value = "camera"
    }

    fun navigateToConfirmation() {
        _navigateTo.value = "confirmation"
    }

    fun navigateToListLogin(){
        _navigateTo.value = "list"
    }

    fun navigateToRegister(){
        _navigateTo.value = "register"
    }


    fun registerUser(
        email: String,
        name: String,
        showDialog: MutableState<Boolean>,
        errorMessage: MutableState<String>
    ) {
        if (!validateFields(email, name) && validateMail(email) && validateName(name)) {
            navigateToCamera()
        }
        else if(validateFields(name, email)){
            showDialog.value = true
            errorMessage.value = "Por favor, rellena todos los campos correctamente."
        }
        else if (!validateMail(email)) {
            showDialog.value = true
            errorMessage.value = "El email debe ser válido"
        }
        else if (!validateName(name)){
            showDialog.value = true
            errorMessage.value = "El nombre debe tener más de 3 carácteres"
        }
    }

    private fun validateMail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateName(name: String): Boolean{
        return name.length >= 3;
    }

    private fun validateFields(email: String, name: String): Boolean{
        return name.isEmpty() || email.isEmpty()
    }

}
