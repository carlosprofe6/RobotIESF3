package com.iesf3.app.robot

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ainirobot.coreservice.client.Definition
import com.ainirobot.coreservice.client.RobotApi
import com.ainirobot.coreservice.client.actionbean.Pose
import com.ainirobot.coreservice.client.listener.ActionListener
import com.ainirobot.coreservice.client.listener.CommandListener
import com.ainirobot.coreservice.client.listener.Person
import com.ainirobot.coreservice.client.listener.TextListener
import com.ainirobot.coreservice.client.person.PersonApi
import com.ainirobot.coreservice.client.person.PersonListener
import com.iesf3.app.robot.data.Place
import com.iesf3.app.robot.listeners.NavigationListener
import com.iesf3.app.robot.listeners.SpeechRecognitionListener
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * RobotManager
 *
 * Punto central para:
 *  - Enviar órdenes de movimiento / navegación / cabeza / TTS al robot.
 *  - Suscribirse a eventos (estado de navegación, detección de personas, etc).
 *  - Exponer listas de ubicaciones guardadas (places) como LiveData.
 *
 * Internamente usa RobotConnectionService para conectarse a las APIs del SDK.
 */
@Singleton
class RobotManager @Inject constructor(
    private val robotConnectionService: RobotConnectionService
) {
    // ===== Observadores/Listeners propios de la app =====

    /** Callback opcional para entregar resultados de ASR (voz a texto) al ViewModel. */
    private var speechRecognitionListener: SpeechRecognitionListener? = null

    /** Lista de escuchas de navegación que se notifican con eventos de estado. */
    private val navigationListeners = mutableListOf<NavigationListener>()

    // ===== Listeners del SDK (se inicializan en setupXxx) =====
    private lateinit var commandListener: CommandListener
    private lateinit var personListener: PersonListener
    private lateinit var actionListener: ActionListener
    private lateinit var placesListener: CommandListener
    private lateinit var headListener: CommandListener
    private lateinit var textListener: TextListener

    // ===== Datos expuestos =====
    /** Cache local de "places" del mapa del robot. */
    val placesList: MutableList<Place> = mutableListOf()

    /** Lista observable de nombres de lugares para la UI. */
    private val _destinationsList = MutableLiveData(listOf<String>())

    /** Callback que la capa superior puede usar cuando cambien las personas detectadas. */
    var onPersonDetected: ((List<Person>?) -> Unit)? = null

    /** API de personas del SDK. */
    private val personApi = PersonApi.getInstance()

    init {
        // Preparamos todos los listeners del SDK.
        setupActionListener()
        setupCommandListener()
        setupPersonListener()
        setupPlacesListener()
        setupHeadListener()
        setupTextListener()

        // Conectamos a las APIs del SDK (voz + núcleo) vía nuestro servicio.
        robotConnectionService.connectToSkillApi()

        // Cuando el núcleo esté listo, pedimos la lista de lugares.
        robotConnectionService.onRobotApiConnected = { getPlaceList() }
        robotConnectionService.connectToRobotApi()
    }

    // ===== Gestión de listeners de navegación (propios de la app) =====
    fun addNavigationListener(listener: NavigationListener) { navigationListeners.add(listener) }
    fun removeNavigationListener(listener: NavigationListener) { navigationListeners.remove(listener) }

    // ===== Detección de personas =====
    fun unregisterPersonListener() {
        Log.d("RobotMan PersonListener", "Unregistering Person")
        personApi.unregisterPersonListener(personListener)
    }

    fun registerPersonListener() {
        Log.d("RobotMan PersonListener", "Registering Person")
        personApi.registerPersonListener(personListener)
    }

    /** Inicia el seguimiento de una persona (por id). Devuelve la lista actual de personas. */
    fun detectPerson(faceId: Int): List<Person>? {
        startFocusFollow(faceId)
        return personApi.allPersons
    }

    /** Para la detección/seguimiento. */
    fun stopDetection() {
        stopFocusFollow()
        unregisterPersonListener()
    }

    // ===== Listeners del SDK =====

    /** Estados generales de acciones (navegación, seguimiento, etc.). */
    private fun setupActionListener() {
        actionListener = object : ActionListener() {
            @Deprecated("SDK legacy")
            override fun onStatusUpdate(status: Int, data: String) {
                when (status) {
                    Definition.STATUS_NAVI_AVOID ->
                        navigationListeners.forEach { it.onRouteBlocked() }
                    Definition.STATUS_NAVI_AVOID_END ->
                        navigationListeners.forEach { it.onObstacleDisappeared() }
                    Definition.STATUS_START_NAVIGATION ->
                        navigationListeners.forEach { it.onNavigationStarted() }

                    // Otros estados útiles para depurar
                    Definition.STATUS_TRACK_TARGET_SUCCEED -> Log.d("RobotManager", "Target tracking succeeded")
                    Definition.STATUS_GUEST_APPEAR       -> Log.d("RobotManager", "Guest appeared")
                    Definition.STATUS_GUEST_LOST         -> Log.d("RobotManager", "Guest lost")
                    Definition.STATUS_GUEST_FARAWAY      -> Log.d("RobotManager", "Guest far away")
                }
            }

            @Deprecated("SDK legacy")
            override fun onError(errorCode: Int, errorString: String?) {
                Log.e("RobotManager", "Tracking error: $errorString")
            }

            @Deprecated("SDK legacy")
            override fun onResult(status: Int, responseString: String?) {
                Log.d("RobotManager", "Tracking result: $responseString")
            }
        }
    }

    /** Respuesta genérica de comandos (ok/ko + estados intermedios). */
    private fun setupCommandListener() {
        commandListener = object : CommandListener() {
            override fun onResult(result: Int, message: String, extraData: String?) {
                if (message == "succeed") Log.d("RobotManager", "Command succeeded")
                else Log.d("RobotManager", "Command failed")
            }

            override fun onStatusUpdate(status: Int, data: String?, extraData: String?) {
                super.onStatusUpdate(status, data, extraData)
                Log.d("RobotManager", "Command status: $status, data=$data, extra=$extraData")
            }
        }
    }

    /** Respuestas de mover cabeza (parsea JSON de estado). */
    private fun setupHeadListener() {
        headListener = object : CommandListener() {
            override fun onResult(result: Int, message: String) {
                try {
                    val json = JSONObject(message)
                    val status = json.getString("status")
                    if (Definition.CMD_STATUS_OK == status) {
                        // OK
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /** Procesa el JSON de "lista de lugares" y actualiza LiveData. */
    private fun setupPlacesListener() {
        placesListener = object : CommandListener() {
            @Deprecated("SDK legacy")
            override fun onResult(result: Int, message: String) {
                try {
                    val jsonArray = JSONArray(message)
                    val newPlaces = mutableListOf<Place>()

                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val x = json.getDouble("x")
                        val y = json.getDouble("y")
                        val theta = json.getDouble("theta")
                        val name = json.getString("name")

                        // Estructuras propias de la app
                        val pose = Pose(x.toFloat(), y.toFloat(), theta.toFloat(), name, false, 1)
                        val place = Place(x, y, theta, name)

                        newPlaces.add(place)
                        Log.d("RobotManager PLACES", "Pose=$pose, Place=$place")
                    }

                    placesList.addAll(newPlaces)
                    _destinationsList.value = placesList.map { it.name }
                    Log.d("RobotManager PLACES", placesList.toString())
                } catch (e: JSONException) {
                    Log.e("ERROR", "Error parsing JSON", e)
                } catch (e: NullPointerException) {
                    Log.e("ERROR", "Null pointer", e)
                }
            }
        }
    }

    /** Notifica cuando cambian personas detectadas. */
    private fun setupPersonListener() {
        personListener = object : PersonListener() {
            override fun personChanged() {
                val personList = PersonApi.getInstance().allPersons
                onPersonDetected?.invoke(personList)
                Log.d("RobotMan PersonListener", "Person changed: $personList")
            }
        }
    }

    /** Estados del TTS (inicio/fin/errores). */
    private fun setupTextListener() {
        textListener = object : TextListener() {
            override fun onStart()  {}
            override fun onStop()   {}
            override fun onError()  {}
            override fun onComplete() {}
        }
    }

    // ===== API pública que usa el ViewModel/UI =====

    /** Pide al robot su lista de destinos guardados. */
    fun getPlaceList(): MutableLiveData<List<String>> {
        Log.d("RobotManager", "Getting place list")
        RobotApi.getInstance().getPlaceList(1, placesListener)
        return _destinationsList
    }

    /** Empieza a seguir/centrarse en una persona (por id). */
    fun startFocusFollow(faceId: Int) {
        registerPersonListener()
        RobotApi.getInstance().startFocusFollow(0, faceId, 10L, 100f, actionListener)
    }

    /** Detiene el seguimiento. */
    fun stopFocusFollow() {
        Log.d("stopFocusFollow", "Stopping follow")
        unregisterPersonListener()
        RobotApi.getInstance().stopFocusFollow(0)
    }

    // Movimiento base
    fun moveForward()  { RobotApi.getInstance().goForward (0, 0.3f, 2f, true, commandListener) }
    fun moveBackward() { RobotApi.getInstance().goBackward(0, 0.3f,          commandListener) }
    fun moveLeft()     { RobotApi.getInstance().turnLeft  (0, 0.3f,          commandListener) }
    fun moveRight()    { RobotApi.getInstance().turnRight (0, 0.3f,          commandListener) }

    // Cabeza
    fun moveHeadUp()   { RobotApi.getInstance().moveHead(0, "absolute", "absolute", 50, 10, headListener) }
    fun moveHeadDown() { RobotApi.getInstance().moveHead(0, "absolute", "absolute", 50, 80, headListener) }
    fun resetHead()    { RobotApi.getInstance().resetHead(0, headListener) }

    // Parada de movimiento
    fun stopForward()  { RobotApi.getInstance().stopMove(1, commandListener) }

    // ASR (voz a texto): pasarela hacia el servicio de conexión
    fun setSpeechRecognitionListener(listener: SpeechRecognitionListener?) {
        this.speechRecognitionListener = listener
        robotConnectionService.setSpeechRecognitionListener(listener)
    }
    fun startASR() = robotConnectionService.startAsr()
    fun stopASR()  = robotConnectionService.stopAsr()

    // TTS
    fun speak(text: String) {
        robotConnectionService.skillApi.playText(text, textListener)
    }

    // Navegación a un destino por nombre
    fun goTo(destinyGoal: String) {
        RobotApi.getInstance().startNavigation(0, destinyGoal, 0.12345, 100000, actionListener)
    }

    // (Privados) Puente interno por si se quisieran usar en el futuro
    private fun notifySpeechPartialResult(result: String) { speechRecognitionListener?.onSpeechPartialResult(result) }
    private fun notifySpeechFinalResult(result: String)   { speechRecognitionListener?.onSpeechFinalResult(result) }
}
