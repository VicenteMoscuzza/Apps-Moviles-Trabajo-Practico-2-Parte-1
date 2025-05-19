package com.example.appsmovilestrabajopractico2parte1

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

val Context.dataStore by preferencesDataStore(name = "prefs")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var numeroAleatorio by remember { mutableStateOf(Random.nextInt(1, 6)) }
    var puntaje by remember { mutableStateOf(0) }
    var maxPuntaje by remember { mutableStateOf(0) }
    var errores by remember { mutableStateOf(0) }
    var inputUsuario by remember { mutableStateOf(TextFieldValue("")) }
    var mensaje by remember { mutableStateOf("") }

    val MAX_KEY = intPreferencesKey("max_puntaje")

    // Leer el mejor puntaje al iniciar
    LaunchedEffect(Unit) {
        val prefs = context.dataStore.data.first()
        maxPuntaje = prefs[MAX_KEY] ?: 0
    }

    fun guardarMaxPuntaje(nuevo: Int) {
        scope.launch {
            context.dataStore.edit { prefs ->
                prefs[MAX_KEY] = nuevo
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Adivina el número del 1 al 5")
        TextField(
            value = inputUsuario,
            onValueChange = { inputUsuario = it },
            label = { Text("Tu número") }
        )
        Button(onClick = {
            val adivinanza = inputUsuario.text.toIntOrNull()
            if (adivinanza != null && adivinanza in 1..5) {
                if (adivinanza == numeroAleatorio) {
                    puntaje += 10
                    errores = 0
                    mensaje = "¡Correcto! +10 puntos"
                    numeroAleatorio = Random.nextInt(1, 6)

                    if (puntaje > maxPuntaje) {
                        maxPuntaje = puntaje
                        guardarMaxPuntaje(maxPuntaje)
                    }
                } else {
                    errores++
                    mensaje = "Incorrecto. Intentos fallidos: $errores"
                    if (errores == 5) {
                        puntaje = 0
                        errores = 0
                        mensaje = "Perdiste. Puntaje reiniciado."
                        numeroAleatorio = Random.nextInt(1, 6)
                    }
                }
            } else {
                mensaje = "Por favor ingresa un número válido del 1 al 5."
            }
            inputUsuario = TextFieldValue("")
        }) {
            Text("Intentar")
        }
        Text("Puntaje actual: $puntaje")
        Text("Mejor puntaje: $maxPuntaje")
        Text(mensaje)
    }
}
