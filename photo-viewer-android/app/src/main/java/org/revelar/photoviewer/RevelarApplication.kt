package org.revelar.photoviewer

import android.app.Application
import org.osmdroid.config.Configuration

/** Inicializa a configuração global do osmdroid (mapa OpenStreetMap). */
class RevelarApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().userAgentValue = packageName
    }
}
