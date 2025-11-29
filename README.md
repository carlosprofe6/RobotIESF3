🤖 RoboApp - Asistente Inteligente IES Fernando III

"Dando vida a la robótica educativa con Inteligencia Artificial y movimiento autónomo."

Bienvenido al repositorio oficial del proyecto de control robótico desarrollado en el IES Fernando III. Esta aplicación Android convierte a un robot OrionStar en un anfitrión interactivo capaz de conversar, navegar y conectarse a servicios externos.

👨‍💻 Créditos del Proyecto

Carlos Barroso

🏫 IES Fernando III

Especialidad

Informática - Educación Secundaria

🚀 Funcionalidades Principales

🗣️ Interacción por Voz Avanzada ("Oye Juan")

El robot siempre está escuchando. Utilizando el motor ASR del SDK, hemos implementado un hotword personalizado.

Comando: "Oye Juan, [pregunta]"

Acción: El robot procesa tu voz y responde inteligentemente.

🧠 Cerebro AI (ChatGPT)

Integración completa con la API de OpenAI.

El robot no solo repite frases; entiende y genera respuestas contextuales.

Mantiene el hilo de la conversación en memoria.

👋 Rutina de Bienvenida (Greeting Mode)

Un modo especial diseñado para recibir visitas en el centro:

🚶 Avanza hacia el invitado (Control cinemático lineal).

📢 Saluda y presenta al IES Fernando III.

🔙 Retrocede a su posición original automáticamente.

Todo gestionado con Kotlin Coroutines para una fluidez perfecta.

📡 Conectividad IoT (MQTT)

El robot no está aislado. Se conecta a un broker MQTT para:

Recibir comandos remotos.

Enviar telemetría o estado a otros dispositivos del aula.

🛠️ Stack Tecnológico

Este proyecto ha sido migrado de una arquitectura clásica a una moderna basada en Jetpack Compose:

Lenguaje: Kotlin 100%

UI: Jetpack Compose (Material Design 3)

Arquitectura: MVVM (Model-View-ViewModel)

Asincronía: Coroutines & Flows

Hardware: OrionStar Robot SDK (RobotOS)

Inyección de Dependencias: Hilt

📱 Capturas / Estructura

La aplicación cuenta con una Pantalla Principal (Dashboard) simplificada para el uso diario en el centro:

// Ejemplo de la lógica de rutina simplificada
suspend fun greetingRoutine() {
    robot.moveForward()
    delay(2000)
    robot.speak("¡Bienvenido al IES Fernando III!")
    robot.moveBackward()
}


🔧 Instalación y Despliegue

Para desplegar esta app en el robot físico (que funciona sobre Android), utilizamos ADB inalámbrico:

Conecta el robot y tu PC a la misma WiFi.

Conecta vía ADB:

adb connect <IP_DEL_ROBOT>


Instala la APK:

adb install -r app-debug.apk


📝 Licencia

Este proyecto es de uso educativo para el IES Fernando III.
Desarrollado con ❤️ y mucho código por Carlos Barroso.

¿Te ha gustado el proyecto? ¡Dale una ⭐ estrella al repositorio!
