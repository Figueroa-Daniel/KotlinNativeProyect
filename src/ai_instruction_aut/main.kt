import gtk3.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
class StableWidget(val widget: CPointer<GtkWidget>?)

@OptIn(ExperimentalForeignApi::class)
fun main() {
    // 1. Inicializar el entorno gráfico de Linux
    gtk_init(null, null)
    val window = gtk_window_new(GtkWindowType.GTK_WINDOW_TOPLEVEL)
    gtk_window_set_title(window?.reinterpret(), "Generador de instrucciones")
    gtk_window_set_default_size(window?.reinterpret(), 1100, 1000)
    gtk_container_set_border_width(window?.reinterpret(), 15u)
    val notebook = gtk_notebook_new()
    gtk_container_add(window?.reinterpret(), notebook)

    // --- Pestaña 1: Instrucciones de Proyecto ---
    val boxProyecto = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 10)
    val labelProyecto = gtk_label_new("Panel para generar instrucciones asociadas a un Proyecto.")
    gtk_box_pack_start(boxProyecto?.reinterpret(), labelProyecto, 0, 0, 10u)




    val tabLabelProyecto = gtk_label_new("Instrucciones de Proyecto")
    gtk_notebook_append_page(notebook?.reinterpret(), boxProyecto, tabLabelProyecto)

    // --- Pestaña 2: Instrucciones Estándar ---
    val boxEstandar = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 10)
    
    // Contenedor scrollable para meter todas las opciones
    val scrolledEstandar = gtk_scrolled_window_new(null, null)
    gtk_scrolled_window_set_policy(scrolledEstandar?.reinterpret(), GtkPolicyType.GTK_POLICY_NEVER, GtkPolicyType.GTK_POLICY_AUTOMATIC)
    gtk_box_pack_start(boxEstandar?.reinterpret(), scrolledEstandar, 1, 1, 0u)
    
    val scrollContentBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 15)
    gtk_container_set_border_width(scrollContentBox?.reinterpret(), 10u)
    gtk_container_add(scrolledEstandar?.reinterpret(), scrollContentBox)

    val labelEstandar = gtk_label_new("Panel para generar instrucciones estándar o genéricas de codificación.")
    gtk_label_set_xalign(labelEstandar?.reinterpret(), 0.0f)
    gtk_box_pack_start(scrollContentBox?.reinterpret(), labelEstandar, 0, 0, 10u)

    // =========================================================================
    // SECCIÓN 1: REGLAS DE COMMITS
    // =========================================================================
    val frameCommits = gtk_frame_new("Reglas para Commits")
    gtk_box_pack_start(scrollContentBox?.reinterpret(), frameCommits, 0, 0, 5u)
    
    val innerCommitsBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 8)
    gtk_container_set_border_width(innerCommitsBox?.reinterpret(), 8u)
    gtk_container_add(frameCommits?.reinterpret(), innerCommitsBox)
    
    val checkBoxParaCommits = gtk_check_button_new_with_label("Habilitar reglas de commits")
    gtk_box_pack_start(innerCommitsBox?.reinterpret(), checkBoxParaCommits, 0, 0, 0u)
    
    val commitSubBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 8)
    gtk_widget_set_margin_start(commitSubBox?.reinterpret(), 20)
    gtk_widget_set_sensitive(commitSubBox?.reinterpret(), 0) // Deshabilitado inicialmente
    gtk_box_pack_start(innerCommitsBox?.reinterpret(), commitSubBox, 0, 0, 0u)
    
    // Inputs del Commit
    val lblInicio = gtk_label_new("Inicio obligatorio para todos los commits (ej: [Ref]):")
    gtk_label_set_xalign(lblInicio?.reinterpret(), 0.0f)
    gtk_box_pack_start(commitSubBox?.reinterpret(), lblInicio, 0, 0, 0u)
    val entryInicio = gtk_entry_new()
    gtk_entry_set_text(entryInicio?.reinterpret(), "[Ref]")
    gtk_box_pack_start(commitSubBox?.reinterpret(), entryInicio, 0, 0, 0u)
    
    val lblInstruccionesCommits = gtk_label_new("Instrucciones libres para los commits:")
    gtk_label_set_xalign(lblInstruccionesCommits?.reinterpret(), 0.0f)
    gtk_box_pack_start(commitSubBox?.reinterpret(), lblInstruccionesCommits, 0, 0, 0u)
    val entryInstruccionesCommits = gtk_entry_new()
    gtk_entry_set_placeholder_text(entryInstruccionesCommits?.reinterpret(), "Ej: breves, verbos en imperativo...")
    gtk_box_pack_start(commitSubBox?.reinterpret(), entryInstruccionesCommits, 0, 0, 0u)
    
    // Lista de tipos de commits (Grid)
    val lblTipos = gtk_label_new("Prefijos por tipo de cambio:")
    gtk_label_set_xalign(lblTipos?.reinterpret(), 0.0f)
    gtk_box_pack_start(commitSubBox?.reinterpret(), lblTipos, 0, 0, 5u)
    
    val gridCommits = gtk_grid_new()
    gtk_grid_set_row_spacing(gridCommits?.reinterpret(), 6u)
    gtk_grid_set_column_spacing(gridCommits?.reinterpret(), 12u)
    gtk_box_pack_start(commitSubBox?.reinterpret(), gridCommits, 0, 0, 0u)
    
    // Rellenamos el Grid
    val prefijos = listOf(
        "Error / Bugfix" to "Fix",
        "Nueva característica" to "feat",
        "Cambios en documentación" to "docs",
        "Cambios de estilo" to "style",
        "Refactorización" to "refactor",
        "Pruebas unitarias" to "test"
    )
    prefijos.forEachIndexed { i, (labelStr, defaultVal) ->
        val lbl = gtk_label_new(labelStr)
        gtk_label_set_xalign(lbl?.reinterpret(), 0.0f)
        val entry = gtk_entry_new()
        gtk_entry_set_text(entry?.reinterpret(), defaultVal)
        gtk_grid_attach(gridCommits?.reinterpret(), lbl, 0, i, 1, 1)
        gtk_grid_attach(gridCommits?.reinterpret(), entry, 1, i, 1, 1)
    }

    // Toggle listener para Commits
    val refCommits = StableRef.create(StableWidget(commitSubBox?.reinterpret()))
    g_signal_connect_data(
        checkBoxParaCommits?.reinterpret(),
        "toggled",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { checkBtn, userData ->
            val ref = userData?.asStableRef<StableWidget>()?.get()?.widget
            if (checkBtn != null && ref != null) {
                val active = gtk_toggle_button_get_active(checkBtn.reinterpret())
                gtk_widget_set_sensitive(ref, active)
            }
        }.reinterpret(),
        refCommits.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<StableWidget>()?.dispose()
        }.reinterpret(),
        0u
    )

    // =========================================================================
    // SECCIÓN 2: ESTILO DE DOCUMENTACIÓN Y CABECERAS
    // =========================================================================
    val frameDoc = gtk_frame_new("Estilo de Documentación y Cabeceras")
    gtk_box_pack_start(scrollContentBox?.reinterpret(), frameDoc, 0, 0, 5u)
    
    val innerDocBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 8)
    gtk_container_set_border_width(innerDocBox?.reinterpret(), 8u)
    gtk_container_add(frameDoc?.reinterpret(), innerDocBox)
    
    // Tipo de cabecera general
    val lblEstiloCabecera = gtk_label_new("Estilo de cabeceras de documentación (ej. KDocs, JSDoc):")
    gtk_label_set_xalign(lblEstiloCabecera?.reinterpret(), 0.0f)
    gtk_box_pack_start(innerDocBox?.reinterpret(), lblEstiloCabecera, 0, 0, 0u)
    
    val comboEstilo = gtk_combo_box_text_new()
    gtk_combo_box_text_append_text(comboEstilo?.reinterpret(), "KDocs (Kotlin)")
    gtk_combo_box_text_append_text(comboEstilo?.reinterpret(), "JSDoc (JavaScript)")
    gtk_combo_box_text_append_text(comboEstilo?.reinterpret(), "Javadocs (Java)")
    gtk_combo_box_text_append_text(comboEstilo?.reinterpret(), "Docstrings (Python)")
    gtk_combo_box_set_active(comboEstilo?.reinterpret(), 0) // Default: KDocs
    gtk_box_pack_start(innerDocBox?.reinterpret(), comboEstilo, 0, 0, 0u)
    
    // 2.1 Cabeceras de Clases
    val checkBoxParaCabecerasDeClases = gtk_check_button_new_with_label("Habilitar cabeceras de clases")
    gtk_box_pack_start(innerDocBox?.reinterpret(), checkBoxParaCabecerasDeClases, 0, 0, 5u)
    
    val classHeaderSubBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 6)
    gtk_widget_set_margin_start(classHeaderSubBox?.reinterpret(), 20)
    gtk_widget_set_sensitive(classHeaderSubBox?.reinterpret(), 0)
    gtk_box_pack_start(innerDocBox?.reinterpret(), classHeaderSubBox, 0, 0, 0u)
    
    val lblDescClase = gtk_label_new("Indicación sobre cómo debe ser la descripción de la clase:")
    gtk_label_set_xalign(lblDescClase?.reinterpret(), 0.0f)
    gtk_box_pack_start(classHeaderSubBox?.reinterpret(), lblDescClase, 0, 0, 0u)
    val entryDescClase = gtk_entry_new()
    gtk_entry_set_placeholder_text(entryDescClase?.reinterpret(), "Ej: Explicar brevemente la responsabilidad única...")
    gtk_box_pack_start(classHeaderSubBox?.reinterpret(), entryDescClase, 0, 0, 0u)
    
    val checkClaseAutor = gtk_check_button_new_with_label("@autor (Indica el desarrollador)")
    val checkClaseFecha = gtk_check_button_new_with_label("@date (Indica la fecha de creación)")
    val checkClaseFile = gtk_check_button_new_with_label("@file (Nombre del archivo físico)")
    gtk_box_pack_start(classHeaderSubBox?.reinterpret(), checkClaseAutor, 0, 0, 0u)
    gtk_box_pack_start(classHeaderSubBox?.reinterpret(), checkClaseFecha, 0, 0, 0u)
    gtk_box_pack_start(classHeaderSubBox?.reinterpret(), checkClaseFile, 0, 0, 0u)

    // Toggle listener para Cabeceras de Clases
    val refClassHeader = StableRef.create(StableWidget(classHeaderSubBox?.reinterpret()))
    g_signal_connect_data(
        checkBoxParaCabecerasDeClases?.reinterpret(),
        "toggled",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { checkBtn, userData ->
            val ref = userData?.asStableRef<StableWidget>()?.get()?.widget
            if (checkBtn != null && ref != null) {
                val active = gtk_toggle_button_get_active(checkBtn.reinterpret())
                gtk_widget_set_sensitive(ref, active)
            }
        }.reinterpret(),
        refClassHeader.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<StableWidget>()?.dispose()
        }.reinterpret(),
        0u
    )

    // 2.2 Cabeceras de Funciones
    val checkBoxParaCabecerasFunciones = gtk_check_button_new_with_label("Habilitar cabeceras de funciones")
    gtk_box_pack_start(innerDocBox?.reinterpret(), checkBoxParaCabecerasFunciones, 0, 0, 5u)
    
    val funcHeaderSubBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 6)
    gtk_widget_set_margin_start(funcHeaderSubBox?.reinterpret(), 20)
    gtk_widget_set_sensitive(funcHeaderSubBox?.reinterpret(), 0)
    gtk_box_pack_start(innerDocBox?.reinterpret(), funcHeaderSubBox, 0, 0, 0u)
    
    val lblDescFunc = gtk_label_new("Indicación sobre cómo debe ser la descripción de las funciones:")
    gtk_label_set_xalign(lblDescFunc?.reinterpret(), 0.0f)
    gtk_box_pack_start(funcHeaderSubBox?.reinterpret(), lblDescFunc, 0, 0, 0u)
    val entryDescFunc = gtk_entry_new()
    gtk_entry_set_placeholder_text(entryDescFunc?.reinterpret(), "Ej: Resumir el algoritmo o comportamiento...")
    gtk_box_pack_start(funcHeaderSubBox?.reinterpret(), entryDescFunc, 0, 0, 0u)
    
    val checkFuncAutor = gtk_check_button_new_with_label("@author (Autor de la función)")
    val checkFuncFecha = gtk_check_button_new_with_label("@date (Fecha)")
    val checkFuncParams = gtk_check_button_new_with_label("@param (Parámetros de entrada)")
    val checkFuncReturn = gtk_check_button_new_with_label("@return (Valor de retorno)")
    val checkFuncThrows = gtk_check_button_new_with_label("@throws / @exception (Excepciones lanzadas)")
    gtk_box_pack_start(funcHeaderSubBox?.reinterpret(), checkFuncAutor, 0, 0, 0u)
    gtk_box_pack_start(funcHeaderSubBox?.reinterpret(), checkFuncFecha, 0, 0, 0u)
    gtk_box_pack_start(funcHeaderSubBox?.reinterpret(), checkFuncParams, 0, 0, 0u)
    gtk_box_pack_start(funcHeaderSubBox?.reinterpret(), checkFuncReturn, 0, 0, 0u)
    gtk_box_pack_start(funcHeaderSubBox?.reinterpret(), checkFuncThrows, 0, 0, 0u)

    // Toggle listener para Cabeceras de Funciones
    val refFuncHeader = StableRef.create(StableWidget(funcHeaderSubBox?.reinterpret()))
    g_signal_connect_data(
        checkBoxParaCabecerasFunciones?.reinterpret(),
        "toggled",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { checkBtn, userData ->
            val ref = userData?.asStableRef<StableWidget>()?.get()?.widget
            if (checkBtn != null && ref != null) {
                val active = gtk_toggle_button_get_active(checkBtn.reinterpret())
                gtk_widget_set_sensitive(ref, active)
            }
        }.reinterpret(),
        refFuncHeader.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<StableWidget>()?.dispose()
        }.reinterpret(),
        0u
    )

    // =========================================================================
    // SECCIÓN 3: OTROS DETALLES DE CÓDIGO
    // =========================================================================
    val frameOtros = gtk_frame_new("Comentarios en Línea y Ramas")
    gtk_box_pack_start(scrollContentBox?.reinterpret(), frameOtros, 0, 0, 5u)
    
    val innerOtrosBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 8)
    gtk_container_set_border_width(innerOtrosBox?.reinterpret(), 8u)
    gtk_container_add(frameOtros?.reinterpret(), innerOtrosBox)
    
    // 3.1 Comentarios en Línea
    val checkBoxParaComentariosInLine = gtk_check_button_new_with_label("Habilitar reglas para comentarios en línea")
    gtk_box_pack_start(innerOtrosBox?.reinterpret(), checkBoxParaComentariosInLine, 0, 0, 5u)
    
    val inlineCommentSubBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 6)
    gtk_widget_set_margin_start(inlineCommentSubBox?.reinterpret(), 20)
    gtk_widget_set_sensitive(inlineCommentSubBox?.reinterpret(), 0)
    gtk_box_pack_start(innerOtrosBox?.reinterpret(), inlineCommentSubBox, 0, 0, 0u)
    
    val lblInlineDesc = gtk_label_new("Indicaciones para comentarios en línea:")
    gtk_label_set_xalign(lblInlineDesc?.reinterpret(), 0.0f)
    gtk_box_pack_start(inlineCommentSubBox?.reinterpret(), lblInlineDesc, 0, 0, 0u)
    val entryInlineDesc = gtk_entry_new()
    gtk_entry_set_placeholder_text(entryInlineDesc?.reinterpret(), "Ej: Comentar solo lógica compleja y usar doble barra //")
    gtk_box_pack_start(inlineCommentSubBox?.reinterpret(), entryInlineDesc, 0, 0, 0u)

    // Toggle listener para Comentarios en línea
    val refInline = StableRef.create(StableWidget(inlineCommentSubBox?.reinterpret()))
    g_signal_connect_data(
        checkBoxParaComentariosInLine?.reinterpret(),
        "toggled",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { checkBtn, userData ->
            val ref = userData?.asStableRef<StableWidget>()?.get()?.widget
            if (checkBtn != null && ref != null) {
                val active = gtk_toggle_button_get_active(checkBtn.reinterpret())
                gtk_widget_set_sensitive(ref, active)
            }
        }.reinterpret(),
        refInline.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<StableWidget>()?.dispose()
        }.reinterpret(),
        0u
    )

    // 3.2 Nombres de Ramas
    val checkBoxParaNamesBranch = gtk_check_button_new_with_label("Habilitar reglas para nombres de ramas (Branches)")
    gtk_box_pack_start(innerOtrosBox?.reinterpret(), checkBoxParaNamesBranch, 0, 0, 5u)
    
    val branchNameSubBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 6)
    gtk_widget_set_margin_start(branchNameSubBox?.reinterpret(), 20)
    gtk_widget_set_sensitive(branchNameSubBox?.reinterpret(), 0)
    gtk_box_pack_start(innerOtrosBox?.reinterpret(), branchNameSubBox, 0, 0, 0u)
    
    val lblBranchDesc = gtk_label_new("Formato y nomenclatura de las ramas:")
    gtk_label_set_xalign(lblBranchDesc?.reinterpret(), 0.0f)
    gtk_box_pack_start(branchNameSubBox?.reinterpret(), lblBranchDesc, 0, 0, 0u)
    val entryBranchDesc = gtk_entry_new()
    gtk_entry_set_text(entryBranchDesc?.reinterpret(), "feature/nombre, bugfix/nombre, hotfix/nombre")
    gtk_box_pack_start(branchNameSubBox?.reinterpret(), entryBranchDesc, 0, 0, 0u)

    // Toggle listener para Nombres de Ramas
    val refBranch = StableRef.create(StableWidget(branchNameSubBox?.reinterpret()))
    g_signal_connect_data(
        checkBoxParaNamesBranch?.reinterpret(),
        "toggled",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { checkBtn, userData ->
            val ref = userData?.asStableRef<StableWidget>()?.get()?.widget
            if (checkBtn != null && ref != null) {
                val active = gtk_toggle_button_get_active(checkBtn.reinterpret())
                gtk_widget_set_sensitive(ref, active)
            }
        }.reinterpret(),
        refBranch.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<StableWidget>()?.dispose()
        }.reinterpret(),
        0u
    )

    val tabLabelEstandar = gtk_label_new("Instrucciones Estándar")
    gtk_notebook_append_page(notebook?.reinterpret(), boxEstandar, tabLabelEstandar)

    // --- Evento de cerrado de ventana ---
    g_signal_connect_data(
            window?.reinterpret(),
            "destroy",
            staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { _, _ -> gtk_main_quit() }
                    .reinterpret(),
            null,
            null,
            0u
    )

    // --- Mostrar todo y arrancar loop de GTK ---
    gtk_widget_show_all(window)
    gtk_main()
}
