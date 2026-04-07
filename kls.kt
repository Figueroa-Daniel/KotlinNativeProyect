import platform.posix.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    val rutaInicial = if (args.isEmpty()) "." else args[0]
    val resPtr = realpath(rutaInicial, null) ?: return
    val rutaAbsoluta = resPtr.toKString()
    free(resPtr)

    val dir = opendir(rutaAbsoluta) ?: return
    val archivos = mutableListOf<String>()
    var entrada = readdir(dir)
    while (entrada != null) {
        val nombre = entrada.pointed.d_name.toKString()
        if (nombre != "." && nombre != "..") archivos.add(nombre)
        entrada = readdir(dir)
    }
    closedir(dir)

    if (archivos.isEmpty()) return
    archivos.sort()

    // --- CONFIGURACIÓN DE REJILLA ---
    val limiteAnchoColumna = 30 
    val maxNombre = archivos.maxOf { it.length }
    val anchoColumna = if (maxNombre > limiteAnchoColumna) limiteAnchoColumna else maxNombre
    val totalEspacio = anchoColumna + 4
    val numColumnas = if (totalEspacio > 35) 2 else 3

    var columnaActual = 0
    archivos.forEach { nombre ->
        val rutaCompleta = if (rutaAbsoluta.endsWith("/")) "$rutaAbsoluta$nombre" else "$rutaAbsoluta/$nombre"
        
        // Detectar si es directorio para ponerle color (Usando stat)
        val esDirectorio = esCarpeta(rutaCompleta)
        val color = if (esDirectorio) "\u001b[1;34m" else "\u001b[0m" // Azul negrita o normal
        val reset = "\u001b[0m"

        val nombreVisible = if (nombre.length > anchoColumna) nombre.take(anchoColumna - 3) + ".." else nombre
        val espacios = " ".repeat((totalEspacio - nombreVisible.length).coerceAtMost(totalEspacio).coerceAtLeast(1))
        
        columnaActual++
        val esFinDeLinea = (columnaActual % numColumnas == 0)
        
        imprimirConLink("$color$nombreVisible$reset$espacios", rutaCompleta, esFinDeLinea)
    }
    if (columnaActual % numColumnas != 0) println()
}

// Función extra para saber si es carpeta o archivo
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

fun imprimirConLink(texto: String, ruta: String, salto: Boolean) {
    val osc = "\u001b]8;;"
    val st = "\u001b\\"
    // Corregido: "file://" ahora es parte del string correctamente
    val linkCompleto = "${osc}file://$ruta$st$texto${osc}$st"
    if (salto) println(linkCompleto) else print(linkCompleto)
}