package com.iesf3.app.ui.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.iesf3.app.chatGPT.ChatGPTManager
import com.iesf3.app.mqtt.MqttManager
import com.iesf3.app.preferences.PreferencesRepository
import com.iesf3.app.robot.RobotManager
import com.iesf3.app.robot.SkillApiService
import com.iesf3.app.robot.listeners.SpeechRecognitionListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * RobotViewModel
 *
 * Expone a la UI:
 *  - Estado de “escuchando” (ASR ON/OFF).
 *  - Texto parcial/final de voz (para mostrar en pantallas).
 *  - Mensajes del chat con GPT.
 *  - Lista de destinos del robot.
 *
 * Orquesta llamadas a RobotManager (movimiento, TTS, ASR, navegación, etc.)
 * y a servicios auxiliares (MQTT, preferencias, ChatGPT).
 */
@HiltViewModel
class RobotViewModel @Inject constructor(
    application: Application,
    private val robotManager : RobotManager,
    private val skillApiService: SkillApiService,
    preferences: PreferencesRepository // Agregamos el repositorio de preferencias
) :AndroidViewModel(application), SpeechRecognitionListener {

    // ===== ChatGPT =====
    private var chatGPTManager = ChatGPTManager(preferences.getToken())
    private val preferencesRepo = preferences

    // ===== Estado de escucha (ASR) =====
    private val _isListening = MutableLiveData(false)
    val isListening: LiveData<Boolean> = _isListening

    // Texto reconocido (para binding tradicional con LiveData si hace falta)
    private val _recognizedText = MutableLiveData<String>()
    val recognizedText: LiveData<String> = _recognizedText

    // ===== Chat en memoria (usuario/assistant) =====
    private val _messages = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val messages: StateFlow<List<Pair<String, String>>> = _messages

    // ===== Voz en tiempo real (Compose-friendly) =====
    private val _speechText = MutableStateFlow("")
    val speechText = _speechText.asStateFlow()

    // Destinos de navegación (llegan del RobotManager)
    val destinationsList = robotManager.getPlaceList()

    // Bandera interna para evitar reprocesar detecciones de persona
    private var hasHandledPersonDetection = false

    // (Opcional) Flujo separado para resultados finales de ASR
    private val _speechFinalResult = MutableStateFlow("")
    val speechFinalResult = _speechFinalResult.asStateFlow()

    init {
        Log.d("RobotViewModel", "Init")
        // Entregamos a RobotManager nuestro listener de voz (esta clase)
        robotManager.setSpeechRecognitionListener(this)

        // Configuración de detección de personas (se reintenta si no hay)
        configurePersonDetection()

        // MQTT si está configurado en preferencias
        initMqttIfConfigured(getApplication<Application>().applicationContext)

        // Parciales de voz (vía SkillApiService) -> StateFlow para Compose
        skillApiService.partialSpeechResult.observeForever { speechResult ->
            viewModelScope.launch { _speechText.value = speechResult }
        }
    }

    // ===== Detección de personas =====
    private fun configurePersonDetection() {
        robotManager.onPersonDetected = { personList ->
            if (personList != null && !hasHandledPersonDetection) {
                handlePersonDetection(personList)
            } else {
                startPersonDetection()
            }
        }
    }

    private fun startPersonDetection() { robotManager.detectPerson(0) }
    private fun handlePersonDetection(personList: List<Any>) {
        if (personList.isNotEmpty() && !hasHandledPersonDetection) hasHandledPersonDetection = true
    }

    // ===== Movimiento y navegación expuestos a la UI =====
    fun comenzarSeguimiento() = robotManager.registerPersonListener()
    fun pararSeguimiento()     = robotManager.stopDetection()
    fun irAdelante()           = robotManager.moveForward()
    fun parar()                = robotManager.stopForward()
    fun irAtras()              = robotManager.moveBackward()
    fun mirarArriba()          = robotManager.moveHeadUp()
    fun mirarAbajo()           = robotManager.moveHeadDown()
    fun irA(destino: String)   = robotManager.goTo(destino)

    // ===== Control del ASR (voz a texto) =====
    fun startListening() {
        if (_isListening.value == true) return
        _isListening.value = true
        _recognizedText.value = ""
        robotManager.setSpeechRecognitionListener(this) // nos registramos
        robotManager.startASR()                          // encendemos ASR
    }

    fun stopListening() {
        if (_isListening.value == false) return
        _isListening.value = false
        _recognizedText.value = ""
        robotManager.stopASR()                           // apagamos ASR
        robotManager.setSpeechRecognitionListener(null)  // dejamos de recibir callbacks
    }

    fun toggleListening() { if (_isListening.value == true) stopListening() else startListening() }


    // ===== Callbacks de voz (vienen del SDK vía RobotConnectionService) =====
    override fun onSpeechPartialResult(result: String) {
        _recognizedText.postValue(result)
        viewModelScope.launch { _speechText.value = result }
    }

    override fun onSpeechFinalResult(result: String) {
        _recognizedText.postValue(result)
        viewModelScope.launch {
            _speechText.value = result
            // Si se quiere diferenciar “final” vs “parcial”
            // _speechFinalResult.value = result
        }
    }

    // ===== TTS =====
    fun speak(text: String) { robotManager.speak(text) }


    // ===== ChatGPT =====
    fun sendMessage(message: String) {
        val updated = _messages.value.toMutableList().apply { add("user" to message) }
        _messages.value = updated

        chatGPTManager.sendChat(updated, "Español") { response ->
            _messages.value = updated + ("assistant" to response)
            speak(response) // lo hace hablar
        }
    }

    fun clearMessages() { _messages.value = emptyList() }

    fun setChatGptToken(newToken: String) {
        preferencesRepo.setToken(newToken)
        chatGPTManager = ChatGPTManager(newToken)
    }

    fun getToken(): String = preferencesRepo.getToken()

    // ===== MQTT (opcionales para integraciones externas) =====
    fun getMqttIp(): String        = preferencesRepo.getBrokerIp()
    fun setMqttIp(ip: String)      = preferencesRepo.setBrokerIp(ip)
    fun getMqttUser(): String      = preferencesRepo.getMqttUsuario()
    fun setMqttUser(user: String)  = preferencesRepo.setMqttUsuario(user)
    fun getMqttPassword(): String  = preferencesRepo.getMqttPassword()
    fun setMqttPassword(pass: String) = preferencesRepo.setMqttPassword(pass)

    /** Arranca MQTT si hay IP+ClientId configurados en preferencias. */
    fun initMqttIfConfigured(context: Context) {
        val ip = preferencesRepo.getBrokerIp()
        val user = preferencesRepo.getMqttUsuario()
        val password = preferencesRepo.getMqttPassword()
        val clientId = preferencesRepo.getMqttClient()

        if (ip.isNotBlank() && clientId.isNotBlank()) {
            MqttManager.init(
                context = context,
                serverUri = ip,
                clientId = clientId,
                user = user,
                password = password,
                onConnected = { Log.d("MQTT", "Conectado desde ViewModel") },
                onError = { Log.e("MQTT", "Error desde ViewModel: ${it.message}") }
            )
        } else {
            Log.w("MQTT", "Configuración MQTT incompleta")
        }
    }

}
