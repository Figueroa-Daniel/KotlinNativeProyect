package readFiles

import kotlinx.cinterop.*
import platform.posix.*

fun obtenerVariableEnv(claveBuscada: String, ruta: String = ".env"): String? {
    // Intentar buscar en variables del sistema primero
    val valorSistema = getenv(claveBuscada)?.toKString()
    if (valorSistema != null) return valorSistema

    var archivo = fopen(ruta, "r")
    // Fallbacks si la ruta por defecto no existe
    if (archivo == null && ruta == ".env") {
        archivo = fopen("../.env", "r") ?: fopen("src/ai_developer/.env", "r")
    }

    if (archivo == null) return null
    val bufferSize = 1024
    val buffer = ByteArray(bufferSize)
    var valorEncontrado: String? = null

    buffer.usePinned { pinned ->
        while (true) {
            val lineaPtr = fgets(pinned.addressOf(0), bufferSize, archivo) ?: break
            val linea = lineaPtr.toKString().trim()

            // Ignorar líneas vacías o comentarios
            if (linea.isEmpty() || linea.startsWith("#")) continue

            // Buscar la separación del signo '='
            val partes = linea.split("=", limit = 2)
            if (partes.size == 2 && partes[0].trim() == claveBuscada) {
                // Limpiamos posibles comillas que rodeen al valor (ej: "mi_token")
                valorEncontrado = partes[1].trim().removeSurrounding("\"").removeSurrounding("'")
                break
            }
        }
    }

    fclose(archivo)
    return valorEncontrado
}
