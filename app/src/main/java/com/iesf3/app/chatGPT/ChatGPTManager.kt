package com.iesf3.app.chatGPT

import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

// Esta clase se encarga de hablar con ChatGPT.
class ChatGPTManager(private val apiKey: String) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // 💬 Esta es la ÚNICA función pública para conversaciones estilo chat.
    // Le pasas los mensajes y te responde en el idioma que le digas.
    fun sendChat(messages: List<Pair<String, String>>, idioma: String, onResponse: (String) -> Unit) {
        val systemPrompt = "Eres un robot asistente cuya función es responder preguntas. Debes responder siempre de forma muy educada y contenta. Tu siempre responderas en el siguiente idioma $idioma"
        sendMessageList(systemPrompt, messages, onResponse)
    }

    // 🔧 Interno: envía una conversación (estilo chat) con un prompt de sistema.
    private fun sendMessageList(system: String, messages: List<Pair<String, String>>, onResponse: (String) -> Unit) {
        coroutineScope.launch {
            try {
                val connection = makeConnection()
                val messageArray = messages.joinToString(",") { (role, content) ->
                    """{"role": "$role", "content": "$content"}"""
                }

                val body = """
                    {
                        "model": "gpt-4o",
                        "messages": [
                            {"role": "system", "content": "$system"},
                            $messageArray
                        ]
                    }
                """.trimIndent()

                connection.outputStream.use { it.write(body.toByteArray()) }
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                val reply = JSONObject(response)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                withContext(Dispatchers.Main) {
                    onResponse(reply)
                    Log.d("CHATBOT", "Respuesta: $reply")
                }

                connection.disconnect()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CHATBOT", "Error: ${e.message}")
                    onResponse("Error al obtener respuesta")
                }
            }
        }
    }

    // 🔧 Interno: envía un prompt con sistema + assistant + user (casos especiales como sinonimos).
    private fun sendRequest(system: String, assistant: String, user: String, onResponse: (String) -> Unit) {
        coroutineScope.launch {
            try {
                val connection = makeConnection()
                val body = """
                    {
                        "model": "gpt-4o",
                        "messages": [
                            {"role": "system", "content": "$system"},
                            {"role": "assistant", "content": "$assistant"},
                            {"role": "user", "content": "$user"}
                        ]
                    }
                """.trimIndent()

                connection.outputStream.use { it.write(body.toByteArray()) }
                val response = connection.inputStream.bufferedReader().use { it.readText() }

                val reply = JSONObject(response)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")

                withContext(Dispatchers.Main) {
                    onResponse(reply)
                    Log.d("CHATBOT", "Respuesta: $reply")
                }

                connection.disconnect()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CHATBOT", "Error: ${e.message}")
                    onResponse("Error al obtener respuesta")
                }
            }
        }
    }

    // 📡 Abre conexión con la API de OpenAI.
    private fun makeConnection(): HttpURLConnection {
        val url = URL("https://api.openai.com/v1/chat/completions")
        return (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Authorization", "Bearer $apiKey")
            doOutput = true
        }
    }
}
