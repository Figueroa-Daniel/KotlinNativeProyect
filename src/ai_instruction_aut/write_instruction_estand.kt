import platform.posix.*
import kotlinx.cinterop.*
import curl.*






fun generateInstructionEstand(matrix: MutableList<List<String>>): Boolean {
    var generated = false
    val textoCompletoSB = StringBuilder()
    val promptSistema = "Actua como un generador experto de archivos de directrices de desarrollo en un entorno profesional (archivos .instruction).\n" +
                        "A partir del siguiente conjunto de preferencias configuradas por el usuario, escribe un documento de directrices de desarrollo en Markdown limpio altamente detallado, robusto y profesional.\n" +
                        "Este documento servira para instruir directamente a otra Inteligencia Artificial de desarrollo sobre exactamente como trabajar en el proyecto.\n" +
                        "Directrices de generacion:\n" +
                        "- Utiliza un tono imperativo, directo, sumamente tecnico y conciso, pero asegurando extenderte en el nivel de detalle necesario para que la IA receptora lo entienda y trabaje sin cometer errores.\n" +
                        "- Explica con claridad y precision profesional cada directriz.\n" +
                        "- Para los campos booleanos, escribe la regla de forma obligatoria SOLO si su valor es 'true'. Si es 'false', ignora esa regla por completo.\n" +
                        "- Organiza la salida en encabezados claros de Markdown (# Commits, # Cabeceras de Codigo, etc.).\n" +
                        "- Devuelve unicamente el Markdown resultante, sin textos introductorios ni explicaciones.\n\n" +
                        "================ REQUERIMIENTOS CONFIGURADOS ================\n\n"
    textoCompletoSB.append(promptSistema)
    for (element in matrix) {
        if (element[0].equals("checkBoxParaCommits", ignoreCase = true) && element[1].equals("true", ignoreCase = true)) {
            val cabecera: String = "---- ESTAS SON LAS SIGUIENTES INSTRUCCIONES QUE DEBE SEGUIR LOS COMMITS ----" 
            val startCommit: String = "El commit debe iniciar de la siguiente manera: ${matrix[1][1]}" // INDICACION DE COMO DEBE EMPEZAR UN COMMIT
            val rulesCommit: String = "Debes tener en cuenta que estas son las reglas generales de commit: ${matrix[2][1]}"
            val prefixCabecera = "Estos son los prefijos que deben tener los commit segun su tematica:"
            val prefixes = "Prefijo de error (Bugfix): ${matrix[3][1]}; " +
                           "Prefijo de Nueva característica: ${matrix[4][1]}; " +
                           "Prefijo de cambios en documentación: ${matrix[5][1]}; " +
                           "Prefijo de cambios de estilo: ${matrix[6][1]}; " +
                           "Prefijo de refactorización: ${matrix[7][1]}; " +
                           "Prefijo de pruebas unitarias: ${matrix[8][1]}"

            textoCompletoSB.append("$cabecera\n$startCommit\n$rulesCommit\n$prefixCabecera\n$prefixes\n\n")

        } else if(element[0].equals("checkBoxParaCabecerasDeClases", ignoreCase = true) && element[1].equals("true", ignoreCase = true)){
            val cabecera = "---- ESTAS SON LAS SIGUIENTES INSTRUCCIONES QUE DEBE SEGUIR LAS CABECERAS DE CLASES ----"
            val herramienta = "Se usara el estilo: ${matrix[9][1]}"
            val descripcionClase = "La descripcion de la clase debe indicar: ${matrix[11][1]}"
            val metadataCabecera = "Debes tener en cuenta que estos metadatos deben estar incluidos o no segun su valor booleano:\n" +
                                   "Autor: ${matrix[12][1]} (Nombre: ${matrix[26][1]})\n" +
                                   "Fecha de creacion: ${matrix[13][1]}\n" +
                                   "Nombre del archivo fisico: ${matrix[14][1]}"
            
            textoCompletoSB.append("$cabecera\n$herramienta\n$descripcionClase\n$metadataCabecera\n\n")

        } else if(element[0].equals("checkBoxParaCabecerasFunciones", ignoreCase = true) && element[1].equals("true", ignoreCase = true)){
            val cabecera = "---- ESTAS SON LAS SIGUIENTES INSTRUCCIONES QUE DEBE SEGUIR LAS CABECERAS DE FUNCIONES ----"
            val descripcionFunc = "La descripcion de las funciones debe indicar: ${matrix[16][1]}"
            val metadataCabecera = "Debes tener en cuenta que estos metadatos deben estar incluidos o no segun su valor booleano:\n" +
                                   "Autor: ${matrix[17][1]} (Nombre: ${matrix[26][1]})\n" +
                                   "Fecha de creacion: ${matrix[18][1]}\n" +
                                   "Parametros (@param): ${matrix[19][1]}\n" +
                                   "Retorno (@return): ${matrix[20][1]}\n" +
                                   "Excepciones (@throws): ${matrix[21][1]}"
            
            textoCompletoSB.append("$cabecera\n$descripcionFunc\n$metadataCabecera\n\n")

        } else if(element[0].equals("checkBoxParaComentariosInLine", ignoreCase = true) && element[1].equals("true", ignoreCase = true)){
            val cabecera = "---- ESTAS SON LAS SIGUIENTES INSTRUCCIONES QUE DEBE SEGUIR LOS COMENTARIOS EN LINEA ----"
            val descripcionInline = "Los comentarios en linea deben seguir las siguientes indicaciones: ${matrix[23][1]}"
            
            textoCompletoSB.append("$cabecera\n$descripcionInline\n\n")

        } else if(element[0].equals("checkBoxParaNamesBranch", ignoreCase = true) && element[1].equals("true", ignoreCase = true)){
            val cabecera = "---- ESTAS SON LAS SIGUIENTES INSTRUCCIONES QUE DEBE SEGUIR LOS NOMBRES DE LAS RAMAS ----"
            val formatoRamas = "El formato y nomenclatura de las ramas debe ser: ${matrix[25][1]}"
            
            textoCompletoSB.append("$cabecera\n$formatoRamas\n\n")
        }
    }

    if (textoCompletoSB.isNotEmpty()) {
        textoCompletoSB.append("================ FIN DE REQUERIMIENTOS ================\n\n")
        textoCompletoSB.append("Genera ahora el archivo de directrices en Markdown basado en la informacion anterior.")
        val instructionsMarkdown: String = tratamientoConIA(textoCompletoSB.toString())
        writeInstructionEstandFile(instructionsMarkdown)
        generated = true
    }
    return generated
}








// Buffer global para acumular los chunks de la respuesta de la API
private var bufferRespuestaInstAut = ""

// Callback nativo requerido por libcurl para recolectar el stream de datos
@OptIn(ExperimentalForeignApi::class)
private fun curlWriteCallbackInst(
        data: COpaquePointer?,
        size: size_t,
        nmemb: size_t,
        userp: COpaquePointer?
): size_t {
    val totalSize = size * nmemb
    if (data != null) {
        val chunk = data.reinterpret<ByteVar>().toKString()
        bufferRespuestaInstAut += chunk
    }
    return totalSize
}

/**
 * Envía el prompt completo con las instrucciones configuradas a la API de Gemini
 * y devuelve el texto Markdown limpio generado por la IA.
 */
@OptIn(ExperimentalForeignApi::class)
fun tratamientoConIA(instructions: String): String {
    bufferRespuestaInstAut = ""

    val apiUrl = obtenerVariableEnvInst("GEMINI_API_URL")
    val apiKey = obtenerVariableEnvInst("GEMINI_API_KEY")
    val miModelo = obtenerVariableEnvInst("MODEL")

    if (apiUrl == null) return "❌ Error: No se pudo leer GEMINI_API_URL. Revisa tu archivo .env"
    if (apiKey == null) return "❌ Error: No se pudo leer GEMINI_API_KEY. Revisa tu archivo .env"
    if (miModelo == null) return "❌ Error: No se pudo leer MODEL. Revisa tu archivo .env"

    // Escapar el prompt para incluirlo como JSON válido
    val inputEscapado = instructions.replace("\"", "\\\"").replace("\n", "\\n")

    val jsonPayload = """
        {
            "model": "$miModelo",
            "input": "$inputEscapado",
            "generation_config": {
                "temperature": 0.0
            }
        }
    """.trimIndent()

    memScoped {
        val curl = curl_easy_init()
        if (curl != null) {
            val apiUrlC = apiUrl.cstr.ptr
            val jsonPayloadC = jsonPayload.cstr.ptr

            curl_easy_setopt(curl, CURLOPT_URL, apiUrlC)
            curl_easy_setopt(curl, CURLOPT_POST, 1L)
            curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonPayloadC)

            var headersList: CPointer<curl_slist>? = null
            headersList = curl_slist_append(headersList, "Content-Type: application/json")
            headersList = curl_slist_append(headersList, "x-goog-api-key: $apiKey")
            curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headersList)

            val callbackFunction = staticCFunction(::curlWriteCallbackInst)
            curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, callbackFunction)
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, curl)

            val resultadoCode = curl_easy_perform(curl)

            if (headersList != null) curl_slist_free_all(headersList)
            curl_easy_cleanup(curl)

            if (resultadoCode != CURLE_OK) {
                val errorMsg = curl_easy_strerror(resultadoCode)?.toKString()
                return "❌ Error de conexión: $errorMsg"
            }
        } else {
            return "❌ Error: No se pudo inicializar libcurl"
        }
    }

    // Extraer el texto limpio del JSON de respuesta de Gemini
    val inicio = bufferRespuestaInstAut.indexOf("\"text\": \"")
    val start = if (inicio != -1) {
        inicio + 9
    } else {
        val inicioSinEspacio = bufferRespuestaInstAut.indexOf("\"text\":\"")
        if (inicioSinEspacio != -1) inicioSinEspacio + 8 else -1
    }

    if (start != -1) {
        var fin = start
        while (fin < bufferRespuestaInstAut.length) {
            fin = bufferRespuestaInstAut.indexOf("\"", fin)
            if (fin == -1) break
            if (bufferRespuestaInstAut[fin - 1] != '\\') break
            fin++
        }
        if (fin != -1) {
            val rawText = bufferRespuestaInstAut.substring(start, fin)
            return rawText
                    .replace("\\\"", "\"")
                    .replace("\\n", "\n")
                    .replace("\\t", "\t")
                    .replace("\\\\", "\\")
        }
    }

    if (bufferRespuestaInstAut.contains("\"error\"")) {
        return "❌ Error de la API de Gemini:\n$bufferRespuestaInstAut"
    }

    return bufferRespuestaInstAut
}






/**
 * Recibe una cadena de texto y la añade (append) al final de "instrucciones_estandar.md".
 */
@OptIn(ExperimentalForeignApi::class)
fun writeInstructionEstandFile(texto: String) {
    val archivo = platform.posix.fopen("instrucciones_estandar.md", "a") ?: return
    try {
        platform.posix.fputs(texto, archivo)
    } finally {
        platform.posix.fclose(archivo)
    }
}
