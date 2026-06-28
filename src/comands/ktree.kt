import platform.posix.*
import kotlinx.cinterop.*

/**
 * Punto de Entrada Temporal para KTree
 */
@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    val rutaInicial = if (args.isEmpty()) "." else args[0]
    
    // Transformar a ruta absoluta para poder armar hipervínculos OSC 8 precisos.
    val resPtr = realpath(rutaInicial, null)
    if (resPtr == null) {
        println("Error: No se pudo resolver la ruta base.")
        return
    }
    
    val rutaAbsoluta = resPtr.toKString()
    free(resPtr)
    
    // Obtener un nombre bonito para el punto de origen maestro
    val nombreRaiz = if (rutaInicial == ".") {
        rutaAbsoluta.substringAfterLast("/")
    } else {
        rutaInicial
    }

    val colorRaiz = "\u001b[1;34m" // Azul Intenso para carpetas
    val reset = "\u001b[0m"
    println("$colorRaiz$nombreRaiz$reset") // Impresión Raíz inicial

    // Empezamos la recursividad desde profundidad cero (Sin prefijos verticales).
    imprimirArbol(rutaAbsoluta, "")
}

/**
 * Función recursiva que itera nodos, dibuja líneas de codos de rama, y reacciona de forma dinámica.
 */
@OptIn(ExperimentalForeignApi::class)
fun imprimirArbol(rutaActual: String, prefijo: String) {
    val dir = opendir(rutaActual) ?: return
    val archivos = mutableListOf<String>()
    
    var entrada = readdir(dir)
    
    // 1. Recolectar del buffer para ver su tamaño exacto (Para saber quien es el último hermano)
    while (entrada != null) {
        val nombre = entrada.pointed.d_name.toKString()
        if (nombre != "." && nombre != "..") {
            archivos.add(nombre)
        }
        entrada = readdir(dir) // Avanzar puntero C
    }
    closedir(dir)
    
    // 2. Orden alfabético y limpio (Estilo Unix)
    archivos.sort()
    
    // 3. Dibujar
    for (i in archivos.indices) {
        val nombre = archivos[i]
        // Boolean Clave: Decide si se dibuja codo '└──' o rama recta '├──'
        val esUltimo = i == archivos.size - 1
        
        val rutaCompleta = if (rutaActual.endsWith("/")) "$rutaActual$nombre" else "$rutaActual/$nombre"
        val esDir = esCarpeta(rutaCompleta)
        
        val punteroArbol = if (esUltimo) "└── " else "├── "
        val color = if (esDir) "\u001b[1;34m" else "\u001b[0m" // Azul o normal
        val reset = "\u001b[0m"
        
        // Imprimir ASCII Line Art
        print("$prefijo$punteroArbol")
        
        // Delegar la impresión de la Entidad a la herramienta de Hipervínculos
        imprimirConLink("$color$nombre$reset", rutaCompleta)
        
        // Entrar recursivamente adaptando la verticalidad de su espaciado
        if (esDir) {
            val nuevoPrefijo = if (esUltimo) {
                // Si su padre fue el último, no necesita tubo vertical, solo espacio invisible
                "$prefijo    "
            } else {
                // Si su padre NO es el último, aún hay hermanos abajo de él, así que dibujamos tubo vertical
                "$prefijo│   "
            }
            imprimirArbol(rutaCompleta, nuevoPrefijo)
        }
    }
}

/**
 * Verificador estricto de directorios a través de memoria Struct <stat>.
 */
@OptIn(ExperimentalForeignApi::class)
fun esCarpeta(ruta: String): Boolean {
    val s = nativeHeap.alloc<stat>()
    val resultado = stat(ruta, s.ptr)
    val esDir = if (resultado == 0) {
        (s.st_mode.toInt() and S_IFMT) == S_IFDIR
    } else false
    nativeHeap.free(s)
    return esDir
}

/**
 * Envolvedor de OSC-8 (Open System Command).
 * Pinta los códigos String para convertir la salida estándar en texto clickeable file://
 */
fun imprimirConLink(texto: String, ruta: String) {
    val osc = "\u001b]8;;"
    val st = "\u001b\\"
    val linkCompleto = "${osc}file://$ruta$st$texto${osc}$st"
    println(linkCompleto)
}
