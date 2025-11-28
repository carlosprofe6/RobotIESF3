package com.iesf3.app.mqtt

val mqttTopicHandlerMap: Map<String, (String) -> Unit> = mapOf(
    "test/pedro" to { message ->
        // ejemplo: responder a un topic
        println("Recibido en test/pedro: $message")
        // robotManager.speak("Mensaje recibido en alex: $message")
    },
    "test/intec" to { message ->
        println("Mensaje test/intec: $message")
    }
)