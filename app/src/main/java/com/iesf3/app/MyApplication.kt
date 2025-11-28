// Esto simplemente dice en qué carpeta lógica está este archivo.
package com.iesf3.app

// Esta clase es como el punto de arranque de toda la app.
// Se ejecuta antes que cualquier pantalla o función.
import android.app.Application

// Este es el servicio que se encarga de conectar la app con el robot.
import com.iesf3.app.robot.RobotConnectionService

// Esto activa un sistema que te da los objetos ya preparados, sin que tengas que crearlos tú.
// En vez de decir "new", te los sirve en bandeja.
import dagger.hilt.android.HiltAndroidApp

// Esto permite pedir cosas que Hilt nos tiene que dar, como quien dice “oye, pásame esto hecho”.
import javax.inject.Inject

// Esta clase hereda de Application, y gracias a esta etiqueta (@HiltAndroidApp),
// le decimos a Android que aquí empieza todo lo de pedir cosas automáticamente (inyección de dependencias).
@HiltAndroidApp
class MyApplication: Application() {

    // Aquí pedimos que nos den el servicio que se conecta con el robot.
    // No lo creamos nosotros, Hilt nos lo pasa listo.
    @Inject
    lateinit var robotConnectionService: RobotConnectionService

    // Este método se lanza una única vez cuando se abre la app por primera vez.
    override fun onCreate() {
        super.onCreate()

        // Conectamos con el robot. Sin esto, no podríamos darle órdenes.
        robotConnectionService.connectToRobotApi()

        // También activamos las habilidades del robot, como si le dijéramos:
        // "prepara tus trucos, que vamos a usarlos".
        robotConnectionService.connectToSkillApi()
    }
}
