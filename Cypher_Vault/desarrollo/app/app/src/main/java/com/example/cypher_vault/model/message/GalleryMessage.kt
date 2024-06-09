package com.example.cypher_vault.model.message


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask


fun message1(): String {
    return "La galería acepta formato de tipo JPG, PNG, JPEG, GIF, BMP, WEBP."
}


fun message2(): String {
    return "Puedes revisar el historial de su sesión en la sección de perfil."
}

fun message3(): String {
    return "Seleccionando el botón (+) puede almacenar sus imágenes."
}

fun message4(): String {
    return "Puedes cambiar la contraseña en la sección de perfil."
}

fun message5(): String {
    return "Puedes cambiar la foto de perfil dentro del panel de usuario."
}

fun message6(): String {
    return "Puedes comprar el paquete premium y obtener más capacidad de almacenamiento."
}

fun message7(): String {
    return "Podrás eliminar varias imágenes manteniendo presionada la imagen que deseas eliminar."
}

fun messageLimitModePrueba(): String{
    return "Has llegado al límite del modo prueba. Por favor, compre el paquete premium para obtener más almacenamiento."
}

fun deleteImgInProgress(): String {
    return "Estamos eliminando las imágenes. Por favor, sea paciente."
}

fun deleteImgComplete(): String {
    return "Las imágenes se han eliminado con éxito."
}

fun messageLimitModePremium(): String{
    return "Has llegado al límite del modo Premium."
}

fun messageLoading(): String {
    return "Estamos cargando las imágenes de su galería."
}

fun messageLoadingComplete(): String {
    return "Todas sus imágenes se han cargado exitosamente."
}

fun messageAddingImages(): String {
    return "Estamos guardando sus imágenes en su galería de manera segura."
}

fun messageAddingImagesComplete(): String {
    return "Todas sus imágenes se han guardado exitosamente."
}

fun startMessageChannel(): Channel<String> {
    val messages = listOf(::message1, ::message2, ::message3, ::message4, ::message5, ::message6, ::message7)
    val channel = Channel<String>(Channel.CONFLATED)
    var index = 0

    val ticker = ticker(delayMillis = 5000, initialDelayMillis = 0)

    CoroutineScope(Dispatchers.Default).launch {
        for (event in ticker) {
            channel.send(messages[index]())
            index = (index + 1) % messages.size
        }
    }

    return channel
}