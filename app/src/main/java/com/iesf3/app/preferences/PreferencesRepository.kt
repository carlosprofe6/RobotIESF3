package com.iesf3.app.preferences

import android.content.SharedPreferences
import javax.inject.Inject

class PreferencesRepository @Inject constructor(private val sharedPreferences: SharedPreferences) {
    fun getBrokerIp(): String = sharedPreferences.getString("broker_ip", "tcp://10.14.0.182:1883")
        ?: "tcp://10.14.0.182:1883"

    fun setBrokerIp(ip: String) = sharedPreferences.edit().putString("broker_ip", ip).apply()

    fun getMqttUsuario(): String =
        sharedPreferences.getString("mqtt_usuario", "intecfull") ?: "intecfull"

    fun setMqttUsuario(usuarioMqtt: String) =
        sharedPreferences.edit().putString("mqtt_usuario", usuarioMqtt).apply()

    fun getMqttPassword(): String =
        sharedPreferences.getString("mqtt_password", "intecfullpassword") ?: "intecfullpassword"

    fun setMqttPassword(passwordMqtt: String) =
        sharedPreferences.edit().putString("mqtt_password", passwordMqtt).apply()

    fun getMqttClient(): String = sharedPreferences.getString("mqtt_client", "Robot") ?: "Robot"

    fun getToken(): String = sharedPreferences.getString(
        "token",
        ""
    )
        ?: ""

    fun setToken(token: String?) = sharedPreferences.edit().putString("token", token).apply()

}