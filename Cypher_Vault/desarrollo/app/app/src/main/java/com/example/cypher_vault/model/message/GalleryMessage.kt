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
    return "Puede revisar el historial de su sesión en la sección de perfil."
}

fun message3(): String {
    return "Seleccionando el botón (+) puede almacenar sus imágenes."
}

fun message4(): String {
    return "Puede cambiar la contraseña en la sección de perfil."
}

fun messageLimitModePrueba(): String{
    return "Has llegado al límite del modo prueba. Por favor, compré el paquete premium para obtener más almacenamiento."
}

fun messageLimitModePremium(): String{
    return "Has llegado al límite del modo Premium."
}

fun startMessageChannel(): Channel<String> {
    val messages = listOf(::message1, ::message2, ::message3, ::message4)
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