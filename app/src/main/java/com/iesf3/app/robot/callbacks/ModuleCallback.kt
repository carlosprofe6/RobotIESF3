package com.iesf3.app.robot.callbacks

import android.os.RemoteException
import android.util.Log
import com.ainirobot.coreservice.client.module.ModuleCallbackApi

class ModuleCallback : ModuleCallbackApi() {
    @Throws(RemoteException::class)
    override fun onSendRequest(
        reqId: Int,
        reqType: String,
        reqText: String,
        reqParam: String
    ): Boolean {

        // Recibe el comando de voz
        // reqType: tipo de comando de voz
        // reqText: texto convertido del comando de voz
        // reqParam: parámetro del comando de voz
        Log.d("SPEECH RECOGNIZED", reqText)

        return false
    }

    @Throws(RemoteException::class)
    override fun onRecovery() {
        // Cuando se recibe este evento, recupera el control del robot
    }

    @Throws(RemoteException::class)
    override fun onSuspend() {
        // El sistema priva del control. Cuando se recibe este evento, todas las llamadas a la API son inválidas
    }
}