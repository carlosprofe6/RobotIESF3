package com.iesf3.app.mqtt

import android.content.Context
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import com.iesf3.app.mqtt.topics.mqttTopics // 🗂 Lista de topics a suscribirse
import org.eclipse.paho.client.mqttv3.*

// 🧠 Singleton que gestiona conexión MQTT para toda la app
object MqttManager {

    private lateinit var mqttClient: MqttAndroidClient // Cliente principal de Paho para Android
    private var initialized = false // Bandera para evitar reconexiones múltiples

    /**
     * 🔌 Inicializa la conexión con el broker MQTT
     * @param context Contexto de la app (para el cliente)
     * @param serverUri URI del broker (ej: "tcp://192.168.1.100:1883")
     * @param clientId Identificador único del cliente
     * @param user Usuario para autenticación (opcional)
     * @param password Contraseña para autenticación (opcional)
     * @param onConnected Callback al conectar
     * @param onError Callback si falla la conexión
     */
    fun init(
        context: Context,
        serverUri: String,
        clientId: String,
        user: String?,
        password: String?,
        onConnected: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        if (initialized) return // Si ya está conectado, no hacemos nada

        // 🧩 Creamos el cliente MQTT de Android
        mqttClient = MqttAndroidClient(context, serverUri, clientId)

        // ⚙️ Configuración de conexión
        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = true  // Reintenta si se pierde conexión
            isCleanSession = false       // Mantiene la sesión viva entre reinicios
            user?.let { this.userName = it } // Añade usuario si lo hay
            password?.let { this.password = it.toCharArray() } // Añade password si lo hay
        }

        // 🎯 Callback para cuando llega un mensaje, o se pierde conexión
        mqttClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                // (opcional) Puedes reintentar o loguear
            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                val payload = message.toString()
                Log.d("MQTT", "[$topic] $payload") // Imprime mensaje recibido
                mqttTopicHandlerMap[topic]?.invoke(payload) // Ejecuta handler si hay uno definido para este topic
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // Puedes notificar que el mensaje fue entregado si lo necesitas
            }
        })

        // 🚀 Intentamos conectar al broker
        mqttClient.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                // ✅ Nos suscribimos a todos los topics definidos
                mqttTopics.forEach { subscribe(it) }
                initialized = true
                onConnected() // Llamamos callback de éxito
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                // ❌ Algo salió mal, avisamos con callback de error
                onError(exception ?: Exception("Unknown error"))
            }
        })
    }

    /**
     * 📨 Publica un mensaje a un topic
     */
    fun publish(topic: String, message: String) {
        if (mqttClient.isConnected) {
            mqttClient.publish(topic, MqttMessage(message.toByteArray()))
        } else {
            Log.w("MQTT", "No conectado al broker")
        }
    }

    /**
     * 📡 Se suscribe a un topic específico
     */
    fun subscribe(topic: String) {
        if (mqttClient.isConnected) {
            mqttClient.subscribe(topic, 1) // QoS 1 = entrega al menos una vez
        }
    }

    /**
     * 🔌 Cierra la conexión
     */
    fun disconnect() {
        if (mqttClient.isConnected) mqttClient.disconnect()
    }

    /**
     * ✅ Devuelve si estamos conectados al broker
     */
    fun isConnected(): Boolean = mqttClient.isConnected
}
