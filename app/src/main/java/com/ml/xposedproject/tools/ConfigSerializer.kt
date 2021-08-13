package com.ml.xposedproject.tools

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.example.application.ConfigProtos
import com.google.protobuf.InvalidProtocolBufferException
import com.ml.xposedproject.App
import java.io.InputStream
import java.io.OutputStream

object ConfigSerializer : Serializer<ConfigProtos> {
    override val defaultValue: ConfigProtos = ConfigProtos.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ConfigProtos {
        try {
            return ConfigProtos.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: ConfigProtos,
        output: OutputStream
    ) = t.writeTo(output)
}

fun createStore(app: App):DataStore<ConfigProtos>{
    return app.settingsDataStore
}
val Context.settingsDataStore: DataStore<ConfigProtos> by dataStore(
    fileName = "config.pb",
    serializer = ConfigSerializer
)
val configDataStore: DataStore<ConfigProtos> = createStore(App.application)
