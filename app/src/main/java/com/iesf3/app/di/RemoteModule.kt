package com.iesf3.app.di

import android.content.Context
import android.content.SharedPreferences
import com.ainirobot.coreservice.client.speech.SkillApi
import com.iesf3.app.chatGPT.ChatGPTManager
import com.iesf3.app.preferences.PreferencesRepository
import com.iesf3.app.robot.RobotConnectionService
import com.iesf3.app.robot.RobotManager
import com.iesf3.app.robot.SkillApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// 📦 Módulo de dependencias que estarán disponibles para toda la aplicación
@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    // 🤖 Proporciona una única instancia de RobotManager (gestor del robot)
    @Singleton
    @Provides
    fun provideRobotManager(robotConnectionService: RobotConnectionService): RobotManager {
        return RobotManager(robotConnectionService) // Necesita RobotConnectionService como dependencia
    }

    // 🔌 Proporciona una instancia de RobotConnectionService (maneja conexión con el robot)
    @Singleton
    @Provides
    fun provideRobotConnectionService(@ApplicationContext context: Context): RobotConnectionService {
        return RobotConnectionService(context, skillApi = SkillApi()) // Usa SkillApi internamente
    }

    // 🗣️ Servicio que encapsula operaciones de voz del robot (hablar, escuchar, etc.)
    @Singleton
    @Provides
    fun provideSkillApi(@ApplicationContext context: Context): SkillApiService {
        return SkillApiService(context) // Recibe el contexto para funcionar
    }

    // 💾 Proporciona acceso a SharedPreferences (para guardar configuraciones locales)
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
    }

    // ⚙️ Proporciona un repositorio para interactuar fácilmente con SharedPreferences
    @Provides
    @Singleton
    fun providePreferenceRepository(sharedPreferences: SharedPreferences) : PreferencesRepository {
        return PreferencesRepository(sharedPreferences)
    }

    // 🤖 GPT: Proporciona una instancia inicial de ChatGPTManager (puede sobrescribirse luego con nuevo token)
    @Provides
    @Singleton
    fun provideChatGPTManager(): ChatGPTManager {
        return ChatGPTManager("") // Se puede actualizar más adelante desde el ViewModel
    }
}
