package readFiles

import kotlinx.cinterop.*
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
fun leerContextoDeArchivosEnCarpeta(rutaCarpeta: String): String {
    var contenidoTotal =
            "CONTEXTO DEL PROGRAMA(este contexto se te envia para que seas capaz de mejorar el prompt de forma mas eficiente y veraz):\n"

    // 1. Abrir el directorio
    val carpeta =
            opendir(rutaCarpeta) ?: return "❌ Error: No se pudo abrir la carpeta en $rutaCarpeta"

    // 2. Asegurar que la ruta de la carpeta termine en '/' para concatenar bien las rutas de los
    // archivos
    val rutaBase = if (rutaCarpeta.endsWith("/")) rutaCarpeta else "$rutaCarpeta/"

    try {
        while (true) {
            // Leer la siguiente entrada (archivo o subcarpeta) del directorio
            val entrada = readdir(carpeta) ?: break // Si es null, terminamos de leer

            // Convertir el nombre del archivo de C a String de Kotlin
            val nombreArchivo = entrada.pointed.d_name.toKString()

            // Ignorar los directorios especiales "." (actual) y ".." (superior)
            if (nombreArchivo == "." || nombreArchivo == "..") continue

            // Construir la ruta completa del archivo
            val rutaCompleta = "$rutaBase$nombreArchivo"

            // Opcional: Podrías verificar aquí si es un archivo regular antes de leerlo,
            // pero para simplificar, llamamos directamente a tu función.
            val contenidoArchivo = leerContextoFile(rutaCompleta)

            // Si el archivo se leyó con éxito (no empieza con el mensaje de error), lo concatenamos
            if (!contenidoArchivo.startsWith("❌ Error:")) {
                contenidoTotal += "--- Archivo: $nombreArchivo ---\n"
                contenidoTotal += contenidoArchivo
                contenidoTotal += "\n\n" // Separador entre archivos
            }
        }
    } finally {
        // 3. Cerrar la carpeta siempre (incluso si ocurre un fallo) para evitar fugas de memoria
        closedir(carpeta)
    }

    return contenidoTotal
}

@OptIn(ExperimentalForeignApi::class)
fun leerContextoFile(ruta: String): String {
    val archivo = fopen(ruta, "r") ?: return "Error: No se pudo abrir el archivo en $ruta"

    var contenido = ""
    val bufferSize = 1024
    // Creamos un buffer temporal en la memoria nativa
    val buffer = ByteArray(bufferSize)

    buffer.usePinned { pinned ->
        while (true) {
            val resultado = fgets(pinned.addressOf(0), bufferSize, archivo) ?: break
            contenido += resultado.toKString()
        }
    }

    fclose(archivo)
    return contenido
}
