import platform.posix.*
import kotlinx.cinterop.*

/**
 * Genera las instrucciones de proyecto a partir de la matriz de la Pestaña 1.
 *
 * Matriz de Proyecto (Pestaña 1):
 * [0]  checkRol          = true/false
 * [1]  entryRol          = "Senior Kotlin/Native Developer"
 * [2]  checkCtx          = true/false
 * [3]  entryCtx          = "Descripción del proyecto..."
 * [4]  entryArq          = "Clean Architecture"
 * [5]  checkEstilo       = true/false
 * [6]  entryEstilo       = "Kotlin Coding Conventions..."
 * [7]  checkReglas       = true/false
 * [8]  entrySintaxis     = "Usar funciones puras..."
 * [9]  entryModificar    = "Solo modificar src/..."
 * [10] entryArqFija      = "Mantener capas separadas..."
 * [11] checkTests        = true/false
 * [12] entryTests        = "Unit tests Mockito..."
 * [13] comboTests        = "Después de programar"
 * [14] checkDocu         = true/false
 * [15] entryDocuEscrita  = "En tono formal..."
 * [16] comboFormatoDocu  = ".md (Markdown)"
 * [17] comboCuándoDocu   = "Después de cada funcionalidad"
 */
fun generateInstructionProject(matrix: MutableList<List<String>>): Boolean {
    var generated = false
    val textoCompletoSB = StringBuilder()

    val promptSistema = "Actua como un generador experto de archivos de directrices de proyecto en un entorno profesional (archivos .instruction).\n" +
                        "A partir del siguiente conjunto de preferencias configuradas por el usuario, escribe un documento de directrices de proyecto en Markdown limpio altamente detallado, robusto y profesional.\n" +
                        "Este documento servira para instruir directamente a otra Inteligencia Artificial de desarrollo sobre exactamente como trabajar en este proyecto.\n" +
                        "Directrices de generacion:\n" +
                        "- Utiliza un tono imperativo, directo, sumamente tecnico y conciso, pero asegurando extenderte en el nivel de detalle necesario para que la IA receptora lo entienda y trabaje sin cometer errores.\n" +
                        "- Explica con claridad y precision profesional cada directriz.\n" +
                        "- Para los campos booleanos, escribe la regla de forma obligatoria SOLO si su valor es 'true'. Si es 'false', ignora esa seccion por completo.\n" +
                        "- Organiza la salida en encabezados claros de Markdown (# Rol de la IA, # Contexto del Proyecto, # Estilo de Codigo, etc.).\n" +
                        "- Devuelve unicamente el Markdown resultante, sin textos introductorios ni explicaciones.\n\n" +
                        "================ REQUERIMIENTOS DE PROYECTO CONFIGURADOS ================\n\n"
    textoCompletoSB.append(promptSistema)

    for (element in matrix) {
        // CHECK 1: Rol de la IA
        if (element[0].equals("checkRol", ignoreCase = true) && element[1].equals("true", ignoreCase = true)) {
            val cabecera = "---- ROL DE LA INTELIGENCIA ARTIFICIAL ----"
            val rol = "La IA debe asumir el siguiente rol profesional: ${matrix[1][1]}"

            textoCompletoSB.append("$cabecera\n$rol\n\n")

        // CHECK 2: Contexto y Arquitectura
        } else if (element[0].equals("checkCtx", ignoreCase = true) && element[1].equals("true", ignoreCase = true)) {
            val cabecera = "---- CONTEXTO Y ARQUITECTURA DEL PROYECTO ----"
            val contexto = "Descripcion del proyecto: ${matrix[3][1]}"
            val arquitectura = "Arquitectura del proyecto: ${matrix[4][1]}"

            textoCompletoSB.append("$cabecera\n$contexto\n$arquitectura\n\n")

        // CHECK 3: Estilo de Código
        } else if (element[0].equals("checkEstilo", ignoreCase = true) && element[1].equals("true", ignoreCase = true)) {
            val cabecera = "---- ESTILO DE CODIGO ----"
            val estilo = "El codigo debe seguir el siguiente estilo: ${matrix[6][1]}"

            textoCompletoSB.append("$cabecera\n$estilo\n\n")

        // CHECK 4: Reglas de Desarrollo
        } else if (element[0].equals("checkReglas", ignoreCase = true) && element[1].equals("true", ignoreCase = true)) {
            val cabecera = "---- REGLAS DE DESARROLLO Y SINTAXIS ----"
            val sintaxis = "Reglas de sintaxis y convenciones: ${matrix[8][1]}"
            val funcionales = "Reglas funcionales (donde debe/no debe modificar la IA): ${matrix[9][1]}"
            val arqFija = "Reglas de arquitectura fijas: ${matrix[10][1]}"

            textoCompletoSB.append("$cabecera\n$sintaxis\n$funcionales\n$arqFija\n\n")

        // CHECK 5: Reglas de Pruebas
        } else if (element[0].equals("checkTests", ignoreCase = true) && element[1].equals("true", ignoreCase = true)) {
            val cabecera = "---- REGLAS DE PRUEBAS (TESTS) ----"
            val tests = "Como deben ser los test y que tipos: ${matrix[12][1]}"
            val cuando = "Cuando deben realizarse los test: ${matrix[13][1]}"

            textoCompletoSB.append("$cabecera\n$tests\n$cuando\n\n")

        // CHECK 6: Documentación
        } else if (element[0].equals("checkDocu", ignoreCase = true) && element[1].equals("true", ignoreCase = true)) {
            val cabecera = "---- REGLAS DE DOCUMENTACION ----"
            val comoEscrita = "La documentacion debe estar escrita de la siguiente manera: ${matrix[15][1]}"
            val formato = "El formato del archivo de documentacion debe ser: ${matrix[16][1]}"
            val cuandoDocu = "Cuando debe realizarse la documentacion: ${matrix[17][1]}"

            textoCompletoSB.append("$cabecera\n$comoEscrita\n$formato\n$cuandoDocu\n\n")
        }
    }

    if (textoCompletoSB.isNotEmpty()) {
        textoCompletoSB.append("================ FIN DE REQUERIMIENTOS DE PROYECTO ================\n\n")
        textoCompletoSB.append("Genera ahora el archivo de directrices de proyecto en Markdown basado en la informacion anterior.")
        val instructionsMarkdown: String = tratamientoConIA(textoCompletoSB.toString())
        writeInstructionProjectFile(instructionsMarkdown)
        generated = true
    }
    return generated
}


/**
 * Recibe una cadena de texto y la añade (append) al final de "instrucciones_proyecto.md".
 */
@OptIn(ExperimentalForeignApi::class)
fun writeInstructionProjectFile(texto: String) {
    val archivo = platform.posix.fopen("instrucciones_proyecto.md", "a") ?: return
    try {
        platform.posix.fputs(texto, archivo)
    } finally {
        platform.posix.fclose(archivo)
    }
}
