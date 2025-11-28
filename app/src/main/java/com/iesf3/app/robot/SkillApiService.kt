package com.iesf3.app.robot

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.ainirobot.coreservice.client.ApiListener
import com.ainirobot.coreservice.client.speech.SkillApi
import com.ainirobot.coreservice.client.speech.SkillCallback
import javax.inject.Inject

/**
 * SkillApiService
 *
 * Servicio simple para:
 *  - Conectarse a la SkillApi.
 *  - Publicar los “parciales” de voz (dictado) en un LiveData que observa el ViewModel.
 *    (Se usa como apoyo para mostrar la transcripción parcial en la UI).
 */
class SkillApiService @Inject constructor(
    private val context: Context
) {
    private var skillApi: SkillApi = SkillApi()

    /** LiveData con los resultados parciales del reconocimiento (texto en curso). */
    val partialSpeechResult = MutableLiveData<String>()

    private val apiListener = object : ApiListener {
        override fun handleApiDisabled() {}
        override fun handleApiConnected() {
            skillApi.registerCallBack(mSkillCallback)
        }
        override fun handleApiDisconnected() {}
    }

    /** Sólo usamos el parcial aquí. El final lo maneja RobotConnectionService. */
    private val mSkillCallback = object : SkillCallback() {
        override fun onSpeechParResult(text: String?) {
            text?.let {
                Log.d("ESCUCHA",text)
                partialSpeechResult.postValue(it)
            }
        }

        override fun onStart() {
            //TO-DO("Not yet implemented")
        }

        override fun onStop() {
            //TO-DO("Not yet implemented")
        }

        override fun onVolumeChange(volume: Int) {
            //TO-DO("Not yet implemented")
        }

        override fun onQueryEnded(queryEndStatus: Int) {
            //TO-DO("Not yet implemented")
        }
    }

    init {
        connectApi()
    }

    private fun connectApi() {
        skillApi.connectApi(context, apiListener)
    }
}