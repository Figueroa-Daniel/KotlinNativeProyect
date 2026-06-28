import platform.posix.*
import kotlinx.cinterop.*

/**
 * Función principal (Punto de entrada).
 * Procesa los argumentos de la terminal y arranca la búsqueda controlando variables de entorno.
 */
@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    // 1. Mostrar ayuda si no hay parámetros obligatorios o si se solicita ayuda por banderas (-h)
    if (args.isEmpty() || args.contains("-h") || args.contains("--help")) {
        println("Uso: k-find [opciones] <nombre_a_buscar> [ruta_inicial]")
        println("Opciones:")
        println("  -s      Incluir archivos/carpetas ocultos (ej. .cache, .git)")
        println("  -<N>    Límite de niveles de profundidad (ej. -5)")
        println("  -s<N>   Ocultos + Límite (ej. -s3 busca en ocultos hasta 3 niveles)")
        return
    }

    // 2. Valores por defecto
    var incluirOcultos = false
    var maxNiveles = Int.MAX_VALUE // Infinito por defecto para no limitar al usuario inicial de forma brusca
    
    val argumentosPosicionales = mutableListOf<String>()

    // 3. Procesar los argumentos (Separar las 'flags' u opciones de los textos posicionales que forman reglas)
    for (arg in args) {
        if (arg.startsWith("-")) {
            // Activar ocultos detectando bandera múltiple ('s')
            if (arg.contains("s", ignoreCase = true)) {
                incluirOcultos = true
            }
            // Extraer del String unicamente los números (limites recursivos)
            val digitos = arg.filter { it.isDigit() }
            if (digitos.isNotEmpty()) {
                maxNiveles = digitos.toInt()
            }
        } else {
            argumentosPosicionales.add(arg)
        }
    }

    // Comprobar colisiones lógicas
    if (argumentosPosicionales.isEmpty()) {
        println("Error: Falta el nombre del archivo a buscar.")
        return
    }

    val nombreBusqueda = argumentosPosicionales[0]
    val rutaInicial = if (argumentosPosicionales.size > 1) argumentosPosicionales[1] else "."

    // 4. Transformar subrutas relativas a la Ruta Absoluta del nodo mediante funciones de C (POSIX)
    val resPtr = realpath(rutaInicial, null)
    if (resPtr == null) {
        println("Error: Ruta inicial no válida.")
        return
    }
    val rutaAbsoluta = resPtr.toKString() // Interpreta el byte C como un String natural compilado
    free(resPtr) // Importante liberar el puntero C (Previene los famosos Out Of Memory Leaks)

    println(" Buscando '$nombreBusqueda' en: $rutaAbsoluta")
    println(" Configuración -> Ocultos: ${if (incluirOcultos) "Sí" else "No"} | Niveles máximos: ${if (maxNiveles == Int.MAX_VALUE) "Todos" else maxNiveles}\n")

    // 5. Iniciar la búsqueda principal iterada asumiendo profundidad del Nivel 1 Base..
    buscarRecursivo(rutaAbsoluta, nombreBusqueda, 1, maxNiveles, incluirOcultos)
    
    println("\n Búsqueda finalizada.")
}

/**
 * Escanea recursivamente leyendo el puntero nativo cada rama del árbol de directorios del usuario.
 * @param rutaActual El directorio actual que se está analizando.
 * @param objetivo String al que se busca coincidencias relativas.
 * @param nivelActual Conteo dinámico de la recursividad que sube progresivamente.
 * @param maxNiveles Tope superior dictado por el CLI.
 * @param incluirOcultos Si se deben procesar los nodos ocultos.
 */
@OptIn(ExperimentalForeignApi::class)
fun buscarRecursivo(rutaActual: String, objetivo: String, nivelActual: Int, maxNiveles: Int, incluirOcultos: Boolean) {
    // Si llegamos al tope de profundidad que pidió el usuario, paramos de entrar (Stop condition)
    if (nivelActual > maxNiveles) return

    val dir = opendir(rutaActual) ?: return

    var entrada = readdir(dir)
    while (entrada != null) {
        val nombre = entrada.pointed.d_name.toKString()

        if (nombre != "." && nombre != "..") {
            val esOculto = nombre.startsWith(".")

            // El GRAN filtro optimizador: Si es oculto y el usuario no puso la 's', lo saltamos
            if (esOculto && !incluirOcultos) {
                entrada = readdir(dir)
                continue
            }

            // Normalizador C format path
            val rutaCompleta = if (rutaActual.endsWith("/")) "$rutaActual$nombre" else "$rutaActual/$nombre"
            
            // Evaluando filtro de validación sin diferenciar caracteres case sensitive
            if (nombre.contains(objetivo, ignoreCase = true)) {
                imprimirResultado(nombre, rutaCompleta)
            }

            // Llamada recursiva tras verificar que el apuntador es nodo Padre (Directorio)
            if (esCarpeta(rutaCompleta)) {
                buscarRecursivo(rutaCompleta, objetivo, nivelActual + 1, maxNiveles, incluirOcultos)
            }
        }
        // Avance cíclico POSIX (Siguiente Item de este Buffer actual)
        entrada = readdir(dir)
    }
    closedir(dir) // Liberación memoria
}

/**
 * Emula la comprobación binaria System Check POSIX para diferenciar File vs Tree Dir Node.
 */
@OptIn(ExperimentalForeignApi::class)
fun esCarpeta(ruta: String): Boolean {
    // Preparar el espacio del heap para los datos C Struct.. Memory management
    val s = nativeHeap.alloc<stat>()
    val res = stat(ruta, s.ptr)
    // Extrae la máscara de 16bits del bloque st_mode para identificar el formato primitivo del SO (Si es carpeta == true)
    val esDir = if (res == 0) (s.st_mode.toInt() and S_IFMT) == S_IFDIR else false
    nativeHeap.free(s)
    return esDir
}

/**
 * Añade la secuencia de escape universal OSC para Terminales modernas. Genera un hypervínculo dinámico file://
 */
fun imprimirResultado(nombre: String, ruta: String) {
    val osc = "\u001b]8;;"
    val st = "\u001b\\"
    val color = "\u001b[1;32m" // Verde para destacar el hallazgo ANSI
    val reset = "\u001b[0m"
    
    println("✨ ${osc}file://$ruta$st$color$nombre$reset${osc}$st  -> $ruta")
}