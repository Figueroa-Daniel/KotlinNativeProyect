import curl.*
import kotlinx.cinterop.*
import platform.posix.*
import readFiles.leerContextoDeArchivosEnCarpeta
import readFiles.obtenerVariableEnv

// Buffer global para almacenar de manera limpia los trozos que descargue Curl
private var bufferRespuestaApi = ""

// Callback nativo en C necesario para recolectar el stream de datos de internet
@OptIn(ExperimentalForeignApi::class)
private fun curlWriteCallback(
        data: COpaquePointer?,
        size: size_t,
        nmemb: size_t,
        userp: COpaquePointer?
): size_t {
    val totalSize = size * nmemb
    if (data != null) {
        val chunk = data.reinterpret<ByteVar>().toKString()
        bufferRespuestaApi += chunk
    }
    return totalSize
}

/** Realiza una petición POST nativa a la API usando la estructura exacta de Postman. */
@OptIn(ExperimentalForeignApi::class)
fun enviarPromptAI(promptText: String, carpetaContexto: String): String {
    // 1. Resetear el buffer de respuesta para esta llamada
    bufferRespuestaApi = ""

    // --- LEER CONFIGURACIÓN DESDE ARCHIVOS EXTERNOS ---
    val apiUrl = obtenerVariableEnv("GEMINI_API_URL")
    val apiKey = obtenerVariableEnv("GEMINI_API_KEY")
    val miModelo = obtenerVariableEnv("MODEL")
    val miSystemInstruction = obtenerVariableEnv("SYSTEM_INSTRUCTION")
    val miContexto = leerContextoDeArchivosEnCarpeta(carpetaContexto)

    if (apiUrl == null) return "❌ Error: No se pudo leer GEMINI_API_URL. Revisa tu archivo .env"
    if (apiKey == null) return "❌ Error: No se pudo leer GEMINI_API_KEY. Revisa tu archivo .env"
    if (miModelo == null) return "❌ Error: No se pudo leer MODEL. Revisa tu archivo .env"
    if (miSystemInstruction == null)
            return "❌ Error: No se pudo leer SYSTEM_INSTRUCTION. Revisa tu archivo .env"
    if (miContexto.startsWith("❌ Error")) return "❌ Error al leer el contexto: $miContexto"

    val miInput =
            """
        Actúa como un Optimizador de Prompts Experto. Tu tarea es transformar la petición del usuario en un prompt de ingeniería de software detallado y estructurado, basándote en el contexto del proyecto adjunto.
        
         REGLAS DE FORMATO CRUCIALES (Sigue esto estrictamente):
        1. NO generes código fuente (clases, funciones, interfaces).
        2. NO agregues introducciones genéricas ni resúmenes redundantes de "Entregables esperados" al final.
        3. Estructura la respuesta ÚNICAMENTE como una lista de tareas ordenada por archivos reales del proyecto, usando este formato exacto:
        
        ## 🎯 PROMPT OBJETIVO PRINCIPAL
        [Escribe aquí una explicación técnica y global de qué se va a implementar en el sistema]
        
        ### Nombre del Módulo / Ruta del Archivo
        * **Acción:** [Crear / Modificar]
        * **Responsabilidad:** [Qué debe hacer este archivo de forma concisa]
        
        Petición del usuario: "$promptText"
        
        --- CONTEXTO DEL PROYECTO ---
        $miContexto
    """.trimIndent()

    // Escapamos las variables para mantener el JSON válido
    val inputEscapado = miInput.replace("\"", "\\\"").replace("\n", "\\n")
    val instructionEscapada = miSystemInstruction.replace("\"", "\\\"").replace("\n", "\\n")

    // 2. Construir el cuerpo JSON idéntico a tu captura de Postman
    val jsonPayload =
            """
        {
            "model": "$miModelo",
            "input": "$inputEscapado",
            "system_instruction": "$instructionEscapada",
            "generation_config": {
                "temperature": 0.0
            }
        }
    """.trimIndent()

    // 3. Inicializar el manejador de libcurl y hacer la petición en un scope de memoria nativa
    memScoped {
        val curl = curl_easy_init()
        if (curl != null) {
            // Convertimos las variables de Kotlin a C-strings que vivirán durante todo este bloque memScoped
            val apiUrlC = apiUrl.cstr.ptr
            val jsonPayloadC = jsonPayload.cstr.ptr

            // Configurar la URL de destino y el método POST
            curl_easy_setopt(curl, CURLOPT_URL, apiUrlC)
            curl_easy_setopt(curl, CURLOPT_POST, 1L)

            // Adjuntar el Payload JSON
            curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonPayloadC)

            // 4. Configurar Cabeceras (Headers) EXACTAS a tu Postman
            var headersList: CPointer<curl_slist>? = null
            headersList = curl_slist_append(headersList, "Content-Type: application/json")
            headersList = curl_slist_append(headersList, "x-goog-api-key: $apiKey")
            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headersList)

            // 5. Vincular el callback para capturar la respuesta del servidor
            val callbackFunction = staticCFunction(::curlWriteCallback)
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, callbackFunction)
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, curl)

            // 6. Ejecutar la llamada de red síncrona
            val resultadoCode = curl_easy_perform(curl)

            // 7. Liberar la memoria nativa asignada en C
            if (headersList != null) curl_slist_free_all(headersList)
            curl_easy_cleanup(curl)

            // Validar si la transferencia falló a nivel de socket/red
            if (resultadoCode != CURLE_OK) {
                val errorMsg = curl_easy_strerror(resultadoCode)?.toKString()
                return "❌ Error de conexión nativa: $errorMsg"
            }
        } else {
            return "❌ Error: No se pudo inicializar la instancia de libcurl"
        }
    }

    // Retorna la respuesta acumulada, extrayendo el texto limpio del JSON de respuesta de Google
    val inicio = bufferRespuestaApi.indexOf("\"text\": \"")
    val start =
            if (inicio != -1) {
                inicio + 9
            } else {
                val inicioSinEspacio = bufferRespuestaApi.indexOf("\"text\":\"")
                if (inicioSinEspacio != -1) inicioSinEspacio + 8 else -1
            }

    if (start != -1) {
        // Buscamos la siguiente comilla que NO esté escapada por un \
        var fin = start
        while (fin < bufferRespuestaApi.length) {
            fin = bufferRespuestaApi.indexOf("\"", fin)
            if (fin == -1) break
            if (bufferRespuestaApi[fin - 1] != '\\') {
                break
            }
            fin++
        }

        if (fin != -1) {
            val rawText = bufferRespuestaApi.substring(start, fin)
            // Desescapamos comillas, saltos de línea y otros caracteres de control del JSON
            return rawText.replace("\\\"", "\"")
                    .replace("\\n", "\n")
                    .replace("\\t", "\t")
                    .replace("\\\\", "\\")
        }
    }

    // Si ocurre un error de la API, devolvemos un mensaje descriptivo
    if (bufferRespuestaApi.contains("\"error\"")) {
        return "❌ Error de la API de Gemini:\n$bufferRespuestaApi"
    }

    return bufferRespuestaApi
}
