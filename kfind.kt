import platform.posix.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    if (args.isEmpty() || args.contains("-h") || args.contains("--help")) {
        println("Uso: k-find [opciones] <nombre_a_buscar> [ruta_inicial]")
        println("Opciones:")
        println("  -s      Incluir archivos/carpetas ocultos (ej. .cache, .git)")
        println("  -<N>    Límite de niveles de profundidad (ej. -5)")
        println("  -s<N>   Ocultos + Límite (ej. -s3 busca en ocultos hasta 3 niveles)")
        return
    }

    // 1. Valores por defecto
    var incluirOcultos = false
    var maxNiveles = Int.MAX_VALUE // Infinito por defecto
    
    val argumentosPosicionales = mutableListOf<String>()

    // 2. Procesar los argumentos (Separar las 'flags' de los textos normales)
    for (arg in args) {
        if (arg.startsWith("-")) {
            // Si tiene 's', activamos ocultos
            if (arg.contains("s", ignoreCase = true)) {
                incluirOcultos = true
            }
            // Extraemos solo los números para el límite de niveles
            val digitos = arg.filter { it.isDigit() }
            if (digitos.isNotEmpty()) {
                maxNiveles = digitos.toInt()
            }
        } else {
            argumentosPosicionales.add(arg)
        }
    }

    if (argumentosPosicionales.isEmpty()) {
        println("Error: Falta el nombre del archivo a buscar.")
        return
    }

    val nombreBusqueda = argumentosPosicionales[0]
    val rutaInicial = if (argumentosPosicionales.size > 1) argumentosPosicionales[1] else "."

    // 3. Convertir ruta a absoluta
    val resPtr = realpath(rutaInicial, null)
    if (resPtr == null) {
        println("Error: Ruta inicial no válida.")
        return
    }
    val rutaAbsoluta = resPtr.toKString()
    free(resPtr)

    println(" Buscando '$nombreBusqueda' en: $rutaAbsoluta")
    println(" Configuración -> Ocultos: ${if (incluirOcultos) "Sí" else "No"} | Niveles máximos: ${if (maxNiveles == Int.MAX_VALUE) "Todos" else maxNiveles}\n")

    // Iniciar la búsqueda desde el Nivel 1
    buscarRecursivo(rutaAbsoluta, nombreBusqueda, 1, maxNiveles, incluirOcultos)
    
    println("\n Búsqueda finalizada.")
}

@OptIn(ExperimentalForeignApi::class)
fun buscarRecursivo(rutaActual: String, objetivo: String, nivelActual: Int, maxNiveles: Int, incluirOcultos: Boolean) {
    // Si llegamos al tope de profundidad que pidió el usuario, paramos de entrar
    if (nivelActual > maxNiveles) return

    val dir = opendir(rutaActual) ?: return

    var entrada = readdir(dir)
    while (entrada != null) {
        val nombre = entrada.pointed.d_name.toKString()

        if (nombre != "." && nombre != "..") {
            val esOculto = nombre.startsWith(".")

            // El GRAN filtro: Si es oculto y el usuario no puso la 's', lo saltamos (súper velocidad)
            if (esOculto && !incluirOcultos) {
                entrada = readdir(dir)
                continue
            }

            val rutaCompleta = if (rutaActual.endsWith("/")) "$rutaActual$nombre" else "$rutaActual/$nombre"
            
            // Si coincide el nombre (ignorando mayúsculas)
            if (nombre.contains(objetivo, ignoreCase = true)) {
                imprimirResultado(nombre, rutaCompleta)
            }

            // Si es carpeta, entramos (sumando +1 al nivel actual)
            if (esCarpeta(rutaCompleta)) {
                buscarRecursivo(rutaCompleta, objetivo, nivelActual + 1, maxNiveles, incluirOcultos)
            }
        }
        entrada = readdir(dir)
    }
    closedir(dir)
}

@OptIn(ExperimentalForeignApi::class)
fun esCarpeta(ruta: String): Boolean {
    val s = nativeHeap.alloc<stat>()
    val res = stat(ruta, s.ptr)
    val esDir = if (res == 0) (s.st_mode.toInt() and S_IFMT) == S_IFDIR else false
    nativeHeap.free(s)
    return esDir
}

fun imprimirResultado(nombre: String, ruta: String) {
    val osc = "\u001b]8;;"
    val st = "\u001b\\"
    val color = "\u001b[1;32m" // Verde para destacar el hallazgo
    val reset = "\u001b[0m"
    
    println("✨ ${osc}file://$ruta$st$color$nombre$reset${osc}$st  -> $ruta")
}