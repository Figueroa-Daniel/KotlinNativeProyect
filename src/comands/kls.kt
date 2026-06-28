import platform.posix.*
import kotlinx.cinterop.*

/**
 * Función principal (Punto de entrada).
 * Carga el directorio y presenta los archivos con el estilo 'ls'.
 */
@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    // 1. Obtener la ruta. Si no hay argumentos se asigna la ruta actual "."
    val rutaInicial = if (args.isEmpty()) "." else args[0]
    
    // 2. Obtener ruta absoluta base con C-POSIX
    val resPtr = realpath(rutaInicial, null) ?: return
    val rutaAbsoluta = resPtr.toKString()
    free(resPtr) // Fundamental liberar la memoria apuntada

    // 3. Abrir el directorio (C POSIX opendir)
    val dir = opendir(rutaAbsoluta) ?: return
    val archivos = mutableListOf<String>()
    
    var entrada = readdir(dir)
    while (entrada != null) {
        val nombre = entrada.pointed.d_name.toKString()
        // Omitir enlaces físicos especiales del propio directorio (., ..)
        if (nombre != "." && nombre != "..") archivos.add(nombre)
        entrada = readdir(dir)
    }
    closedir(dir) // Prevenir leaks de recursos cerrando el handler

    if (archivos.isEmpty()) return
    archivos.sort() // Organizar alfabéticamente

    // --- CONFIGURACIÓN DE REJILLA MATRICIAL ---
    val limiteAnchoColumna = 30 
    val maxNombre = archivos.maxOf { it.length }
    // Decide el ancho real de la columna para la tabla
    val anchoColumna = if (maxNombre > limiteAnchoColumna) limiteAnchoColumna else maxNombre
    val totalEspacio = anchoColumna + 4
    // Distribuir en 2 o 3 columnas dinámicamente según el largo del nombre
    val numColumnas = if (totalEspacio > 35) 2 else 3

    var columnaActual = 0
    archivos.forEach { nombre ->
        val rutaCompleta = if (rutaAbsoluta.endsWith("/")) "$rutaAbsoluta$nombre" else "$rutaAbsoluta/$nombre"
        
        // 4. Detectar si este nodo es directorio o archivo para pintarle los colores
        val esDirectorio = esCarpeta(rutaCompleta)
        val color = if (esDirectorio) "\u001b[1;34m" else "\u001b[0m" // Azul si es carpeta, Blanco natural si es archivo
        val reset = "\u001b[0m"

        // Acortar nombres inmensos con ".."
        val nombreVisible = if (nombre.length > anchoColumna) nombre.take(anchoColumna - 3) + ".." else nombre
        val espacios = " ".repeat((totalEspacio - nombreVisible.length).coerceAtMost(totalEspacio).coerceAtLeast(1))
        
        columnaActual++
        val esFinDeLinea = (columnaActual % numColumnas == 0)
        
        // 5. Envío al System.out con sus metadatos
        imprimirConLink("$color$nombreVisible$reset$espacios", rutaCompleta, esFinDeLinea)
    }
    // Salto final si la rejilla no es perfecta
    if (columnaActual % numColumnas != 0) println()
}

/**
 * Examina los metadatos 'st_mode' del archivo vía POSIX.stat para deducir si el archivo es directorio.
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
 * Renderiza el formato String incluyendo Secuencia de Escape de la Terminal para hacer textos Clickeables.
 */
fun imprimirConLink(texto: String, ruta: String, salto: Boolean) {
    val osc = "\u001b]8;;"
    val st = "\u001b\\"
    // Empaqueta el formato URI file:// 
    val linkCompleto = "${osc}file://$ruta$st$texto${osc}$st"
    if (salto) println(linkCompleto) else print(linkCompleto)
}