import gtk3.*
import kotlinx.cinterop.*

@OptIn(ExperimentalForeignApi::class)
class StableWidget(val widget: CPointer<GtkWidget>?)

@OptIn(ExperimentalForeignApi::class)
class ProyectoWidgets(
    val checkRol: CPointer<GtkWidget>?,
    val entryRol: CPointer<GtkWidget>?,
    val checkCtx: CPointer<GtkWidget>?,
    val entryCtx: CPointer<GtkWidget>?,
    val entryArq: CPointer<GtkWidget>?,
    val checkEstilo: CPointer<GtkWidget>?,
    val entryEstilo: CPointer<GtkWidget>?,
    val checkReglas: CPointer<GtkWidget>?,
    val entrySintaxis: CPointer<GtkWidget>?,
    val entryModificar: CPointer<GtkWidget>?,
    val entryArqFija: CPointer<GtkWidget>?,
    val checkTests: CPointer<GtkWidget>?,
    val entryTests: CPointer<GtkWidget>?,
    val comboTests: CPointer<GtkWidget>?,
    val checkDocu: CPointer<GtkWidget>?,
    val entryDocuEscrita: CPointer<GtkWidget>?,
    val comboFormatoDocu: CPointer<GtkWidget>?,
    val comboCuándoDocu: CPointer<GtkWidget>?
)

@OptIn(ExperimentalForeignApi::class)
class EstandarWidgets(
    val checkBoxParaCommits: CPointer<GtkWidget>?,
    val entryInicio: CPointer<GtkWidget>?,
    val entryInstruccionesCommits: CPointer<GtkWidget>?,
    val commitEntries: List<CPointer<GtkWidget>?>,
    val comboEstilo: CPointer<GtkWidget>?,
    val checkBoxParaCabecerasDeClases: CPointer<GtkWidget>?,
    val entryDescClase: CPointer<GtkWidget>?,
    val checkClaseAutor: CPointer<GtkWidget>?,
    val checkClaseFecha: CPointer<GtkWidget>?,
    val checkClaseFile: CPointer<GtkWidget>?,
    val checkBoxParaCabecerasFunciones: CPointer<GtkWidget>?,
    val entryDescFunc: CPointer<GtkWidget>?,
    val checkFuncAutor: CPointer<GtkWidget>?,
    val checkFuncFecha: CPointer<GtkWidget>?,
    val checkFuncParams: CPointer<GtkWidget>?,
    val checkFuncReturn: CPointer<GtkWidget>?,
    val checkFuncThrows: CPointer<GtkWidget>?,
    val checkBoxParaComentariosInLine: CPointer<GtkWidget>?,
    val entryInlineDesc: CPointer<GtkWidget>?,
    val checkBoxParaNamesBranch: CPointer<GtkWidget>?,
    val entryBranchDesc: CPointer<GtkWidget>?
)

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
    
    // Contenedor scrollable
    val scrolledProyecto = gtk_scrolled_window_new(null, null)
    gtk_scrolled_window_set_policy(scrolledProyecto?.reinterpret(), GtkPolicyType.GTK_POLICY_NEVER, GtkPolicyType.GTK_POLICY_AUTOMATIC)
    gtk_box_pack_start(boxProyecto?.reinterpret(), scrolledProyecto, 1, 1, 0u)
    
    val scrollProyectoBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 15)
    gtk_container_set_border_width(scrollProyectoBox?.reinterpret(), 10u)
    gtk_container_add(scrolledProyecto?.reinterpret(), scrollProyectoBox)

    val labelProyecto = gtk_label_new("Panel para generar instrucciones asociadas a un Proyecto específico.")
    gtk_label_set_xalign(labelProyecto?.reinterpret(), 0.0f)
    gtk_box_pack_start(scrollProyectoBox?.reinterpret(), labelProyecto, 0, 0, 10u)

    // CHECK 1: Rol de IA
    val frameRol = gtk_frame_new("Rol de la IA")
    gtk_box_pack_start(scrollProyectoBox?.reinterpret(), frameRol, 0, 0, 5u)
    val innerRolBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 8)
    gtk_container_set_border_width(innerRolBox?.reinterpret(), 8u)
    gtk_container_add(frameRol?.reinterpret(), innerRolBox)

    val checkRol = gtk_check_button_new_with_label("Especificar rol de la IA")
    gtk_box_pack_start(innerRolBox?.reinterpret(), checkRol, 0, 0, 0u)

    val subBoxRol = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 6)
    gtk_widget_set_margin_start(subBoxRol?.reinterpret(), 20)
    gtk_widget_set_sensitive(subBoxRol?.reinterpret(), 0)
    gtk_box_pack_start(innerRolBox?.reinterpret(), subBoxRol, 0, 0, 0u)

    val lblRol = gtk_label_new("Rol que debe tomar la IA (ej: Senior Android Developer):")
    gtk_label_set_xalign(lblRol?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxRol?.reinterpret(), lblRol, 0, 0, 0u)
    val entryRol = gtk_entry_new()
    gtk_entry_set_text(entryRol?.reinterpret(), "Senior Software Engineer")
    gtk_box_pack_start(subBoxRol?.reinterpret(), entryRol, 0, 0, 0u)

    // Toggle listener para CHECK 1
    val refRol = StableRef.create(StableWidget(subBoxRol?.reinterpret()))
    g_signal_connect_data(
        checkRol?.reinterpret(),
        "toggled",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { checkBtn, userData ->
            val ref = userData?.asStableRef<StableWidget>()?.get()?.widget
            if (checkBtn != null && ref != null) {
                val active = gtk_toggle_button_get_active(checkBtn.reinterpret())
                gtk_widget_set_sensitive(ref, active)
            }
        }.reinterpret(),
        refRol.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<StableWidget>()?.dispose()
        }.reinterpret(),
        0u
    )

    // CHECK 2: Contexto y Arquitectura
    val frameCtx = gtk_frame_new("Contexto y Arquitectura del Proyecto")
    gtk_box_pack_start(scrollProyectoBox?.reinterpret(), frameCtx, 0, 0, 5u)
    val innerCtxBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 8)
    gtk_container_set_border_width(innerCtxBox?.reinterpret(), 8u)
    gtk_container_add(frameCtx?.reinterpret(), innerCtxBox)

    val checkCtx = gtk_check_button_new_with_label("Habilitar contexto y arquitectura")
    gtk_box_pack_start(innerCtxBox?.reinterpret(), checkCtx, 0, 0, 0u)

    val subBoxCtx = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 6)
    gtk_widget_set_margin_start(subBoxCtx?.reinterpret(), 20)
    gtk_widget_set_sensitive(subBoxCtx?.reinterpret(), 0)
    gtk_box_pack_start(innerCtxBox?.reinterpret(), subBoxCtx, 0, 0, 0u)

    val lblCtx = gtk_label_new("Descripción del proyecto (Contexto):")
    gtk_label_set_xalign(lblCtx?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxCtx?.reinterpret(), lblCtx, 0, 0, 0u)
    val entryCtx = gtk_entry_new()
    gtk_entry_set_placeholder_text(entryCtx?.reinterpret(), "Ej: Aplicación e-commerce con catálogo y carrito...")
    gtk_box_pack_start(subBoxCtx?.reinterpret(), entryCtx, 0, 0, 0u)

    val lblArq = gtk_label_new("Arquitectura del proyecto (ej: Clean Architecture, MVVM):")
    gtk_label_set_xalign(lblArq?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxCtx?.reinterpret(), lblArq, 0, 0, 0u)
    val entryArq = gtk_entry_new()
    gtk_entry_set_text(entryArq?.reinterpret(), "Clean Architecture")
    gtk_box_pack_start(subBoxCtx?.reinterpret(), entryArq, 0, 0, 0u)

    // Toggle listener para CHECK 2
    val refCtx = StableRef.create(StableWidget(subBoxCtx?.reinterpret()))
    g_signal_connect_data(
        checkCtx?.reinterpret(),
        "toggled",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { checkBtn, userData ->
            val ref = userData?.asStableRef<StableWidget>()?.get()?.widget
            if (checkBtn != null && ref != null) {
                val active = gtk_toggle_button_get_active(checkBtn.reinterpret())
                gtk_widget_set_sensitive(ref, active)
            }
        }.reinterpret(),
        refCtx.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<StableWidget>()?.dispose()
        }.reinterpret(),
        0u
    )

    // CHECK 3: Estilo de código
    val frameEstilo = gtk_frame_new("Estilo de Código")
    gtk_box_pack_start(scrollProyectoBox?.reinterpret(), frameEstilo, 0, 0, 5u)
    val innerEstiloBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 8)
    gtk_container_set_border_width(innerEstiloBox?.reinterpret(), 8u)
    gtk_container_add(frameEstilo?.reinterpret(), innerEstiloBox)

    val checkEstilo = gtk_check_button_new_with_label("Definir estilo de código")
    gtk_box_pack_start(innerEstiloBox?.reinterpret(), checkEstilo, 0, 0, 0u)

    val subBoxEstilo = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 6)
    gtk_widget_set_margin_start(subBoxEstilo?.reinterpret(), 20)
    gtk_widget_set_sensitive(subBoxEstilo?.reinterpret(), 0)
    gtk_box_pack_start(innerEstiloBox?.reinterpret(), subBoxEstilo, 0, 0, 0u)

    val lblEstilo = gtk_label_new("Estilo de código (ej: Guías oficiales de Google, identación 4 espacios):")
    gtk_label_set_xalign(lblEstilo?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxEstilo?.reinterpret(), lblEstilo, 0, 0, 0u)
    val entryEstilo = gtk_entry_new()
    gtk_entry_set_text(entryEstilo?.reinterpret(), "Guía de estilo oficial de Kotlin con 4 espacios")
    gtk_box_pack_start(subBoxEstilo?.reinterpret(), entryEstilo, 0, 0, 0u)

    // Toggle listener para CHECK 3
    val refEstilo = StableRef.create(StableWidget(subBoxEstilo?.reinterpret()))
    g_signal_connect_data(
        checkEstilo?.reinterpret(),
        "toggled",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { checkBtn, userData ->
            val ref = userData?.asStableRef<StableWidget>()?.get()?.widget
            if (checkBtn != null && ref != null) {
                val active = gtk_toggle_button_get_active(checkBtn.reinterpret())
                gtk_widget_set_sensitive(ref, active)
            }
        }.reinterpret(),
        refEstilo.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<StableWidget>()?.dispose()
        }.reinterpret(),
        0u
    )

    // CHECK 4: Reglas de Desarrollo
    val frameReglas = gtk_frame_new("Reglas de Desarrollo y Sintaxis")
    gtk_box_pack_start(scrollProyectoBox?.reinterpret(), frameReglas, 0, 0, 5u)
    val innerReglasBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 8)
    gtk_container_set_border_width(innerReglasBox?.reinterpret(), 8u)
    gtk_container_add(frameReglas?.reinterpret(), innerReglasBox)

    val checkReglas = gtk_check_button_new_with_label("Habilitar reglas de desarrollo")
    gtk_box_pack_start(innerReglasBox?.reinterpret(), checkReglas, 0, 0, 0u)

    val subBoxReglas = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 6)
    gtk_widget_set_margin_start(subBoxReglas?.reinterpret(), 20)
    gtk_widget_set_sensitive(subBoxReglas?.reinterpret(), 0)
    gtk_box_pack_start(innerReglasBox?.reinterpret(), subBoxReglas, 0, 0, 0u)

    val lblSintaxis = gtk_label_new("Reglas de sintaxis y convenciones:")
    gtk_label_set_xalign(lblSintaxis?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxReglas?.reinterpret(), lblSintaxis, 0, 0, 0u)
    val entrySintaxis = gtk_entry_new()
    gtk_entry_set_placeholder_text(entrySintaxis?.reinterpret(), "Ej: usar clases de datos y funciones de extensión...")
    gtk_box_pack_start(subBoxReglas?.reinterpret(), entrySintaxis, 0, 0, 0u)

    val lblModificar = gtk_label_new("Reglas funcionales (dónde debe/no debe modificar la IA):")
    gtk_label_set_xalign(lblModificar?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxReglas?.reinterpret(), lblModificar, 0, 0, 0u)
    val entryModificar = gtk_entry_new()
    gtk_entry_set_placeholder_text(entryModificar?.reinterpret(), "Ej: no modificar archivos de configuración gradle...")
    gtk_box_pack_start(subBoxReglas?.reinterpret(), entryModificar, 0, 0, 0u)

    val lblArqFija = gtk_label_new("Reglas de arquitectura fijas:")
    gtk_label_set_xalign(lblArqFija?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxReglas?.reinterpret(), lblArqFija, 0, 0, 0u)
    val entryArqFija = gtk_entry_new()
    gtk_entry_set_placeholder_text(entryArqFija?.reinterpret(), "Ej: usar inyección de dependencias con Koin...")
    gtk_box_pack_start(subBoxReglas?.reinterpret(), entryArqFija, 0, 0, 0u)

    // Toggle listener para CHECK 4
    val refReglas = StableRef.create(StableWidget(subBoxReglas?.reinterpret()))
    g_signal_connect_data(
        checkReglas?.reinterpret(),
        "toggled",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { checkBtn, userData ->
            val ref = userData?.asStableRef<StableWidget>()?.get()?.widget
            if (checkBtn != null && ref != null) {
                val active = gtk_toggle_button_get_active(checkBtn.reinterpret())
                gtk_widget_set_sensitive(ref, active)
            }
        }.reinterpret(),
        refReglas.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<StableWidget>()?.dispose()
        }.reinterpret(),
        0u
    )

    // CHECK 5: Reglas de Pruebas
    val frameTests = gtk_frame_new("Reglas de Pruebas (Tests)")
    gtk_box_pack_start(scrollProyectoBox?.reinterpret(), frameTests, 0, 0, 5u)
    val innerTestsBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 8)
    gtk_container_set_border_width(innerTestsBox?.reinterpret(), 8u)
    gtk_container_add(frameTests?.reinterpret(), innerTestsBox)

    val checkTests = gtk_check_button_new_with_label("Habilitar reglas de pruebas")
    gtk_box_pack_start(innerTestsBox?.reinterpret(), checkTests, 0, 0, 0u)

    val subBoxTests = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 6)
    gtk_widget_set_margin_start(subBoxTests?.reinterpret(), 20)
    gtk_widget_set_sensitive(subBoxTests?.reinterpret(), 0)
    gtk_box_pack_start(innerTestsBox?.reinterpret(), subBoxTests, 0, 0, 0u)

    val lblTests = gtk_label_new("Cómo deben ser los test y qué tipos de test debe haber:")
    gtk_label_set_xalign(lblTests?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxTests?.reinterpret(), lblTests, 0, 0, 0u)
    val entryTests = gtk_entry_new()
    gtk_entry_set_placeholder_text(entryTests?.reinterpret(), "Ej: Pruebas unitarias de Mockito en todos los usecases...")
    gtk_box_pack_start(subBoxTests?.reinterpret(), entryTests, 0, 0, 0u)

    val lblCuándoTests = gtk_label_new("Cuándo deben realizarse los test:")
    gtk_label_set_xalign(lblCuándoTests?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxTests?.reinterpret(), lblCuándoTests, 0, 0, 0u)
    
    val comboTests = gtk_combo_box_text_new()
    gtk_combo_box_text_append_text(comboTests?.reinterpret(), "Antes de programar (TDD)")
    gtk_combo_box_text_append_text(comboTests?.reinterpret(), "Después de programar")
    gtk_combo_box_set_active(comboTests?.reinterpret(), 1)
    gtk_box_pack_start(subBoxTests?.reinterpret(), comboTests, 0, 0, 0u)

    // Toggle listener para CHECK 5
    val refTests = StableRef.create(StableWidget(subBoxTests?.reinterpret()))
    g_signal_connect_data(
        checkTests?.reinterpret(),
        "toggled",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { checkBtn, userData ->
            val ref = userData?.asStableRef<StableWidget>()?.get()?.widget
            if (checkBtn != null && ref != null) {
                val active = gtk_toggle_button_get_active(checkBtn.reinterpret())
                gtk_widget_set_sensitive(ref, active)
            }
        }.reinterpret(),
        refTests.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<StableWidget>()?.dispose()
        }.reinterpret(),
        0u
    )

    // CHECK 6: Documentación
    val frameDocu = gtk_frame_new("Reglas de Documentación")
    gtk_box_pack_start(scrollProyectoBox?.reinterpret(), frameDocu, 0, 0, 5u)
    val innerDocuBox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 8)
    gtk_container_set_border_width(innerDocuBox?.reinterpret(), 8u)
    gtk_container_add(frameDocu?.reinterpret(), innerDocuBox)

    val checkDocu = gtk_check_button_new_with_label("Habilitar reglas de documentación")
    gtk_box_pack_start(innerDocuBox?.reinterpret(), checkDocu, 0, 0, 0u)

    val subBoxDocu = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 6)
    gtk_widget_set_margin_start(subBoxDocu?.reinterpret(), 20)
    gtk_widget_set_sensitive(subBoxDocu?.reinterpret(), 0)
    gtk_box_pack_start(innerDocuBox?.reinterpret(), subBoxDocu, 0, 0, 0u)

    val lblDocuEscrita = gtk_label_new("Cómo debe estar escrita la documentación:")
    gtk_label_set_xalign(lblDocuEscrita?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxDocu?.reinterpret(), lblDocuEscrita, 0, 0, 0u)
    val entryDocuEscrita = gtk_entry_new()
    gtk_entry_set_placeholder_text(entryDocuEscrita?.reinterpret(), "Ej: En tono formal, explicando parámetros...")
    gtk_box_pack_start(subBoxDocu?.reinterpret(), entryDocuEscrita, 0, 0, 0u)

    val lblFormatoDocu = gtk_label_new("Formato del archivo de documentación:")
    gtk_label_set_xalign(lblFormatoDocu?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxDocu?.reinterpret(), lblFormatoDocu, 0, 0, 0u)
    
    val comboFormatoDocu = gtk_combo_box_text_new()
    gtk_combo_box_text_append_text(comboFormatoDocu?.reinterpret(), ".md (Markdown)")
    gtk_combo_box_text_append_text(comboFormatoDocu?.reinterpret(), ".txt (Texto plano)")
    gtk_combo_box_text_append_text(comboFormatoDocu?.reinterpret(), ".adoc (AsciiDoc)")
    gtk_combo_box_set_active(comboFormatoDocu?.reinterpret(), 0)
    gtk_box_pack_start(subBoxDocu?.reinterpret(), comboFormatoDocu, 0, 0, 0u)

    val lblCuándoDocu = gtk_label_new("Cuándo debe realizarse la documentación:")
    gtk_label_set_xalign(lblCuándoDocu?.reinterpret(), 0.0f)
    gtk_box_pack_start(subBoxDocu?.reinterpret(), lblCuándoDocu, 0, 0, 0u)

    val comboCuándoDocu = gtk_combo_box_text_new()
    gtk_combo_box_text_append_text(comboCuándoDocu?.reinterpret(), "Antes de cada funcionalidad")
    gtk_combo_box_text_append_text(comboCuándoDocu?.reinterpret(), "Antes de cada bloque")
    gtk_combo_box_text_append_text(comboCuándoDocu?.reinterpret(), "Después de cada funcionalidad")
    gtk_combo_box_text_append_text(comboCuándoDocu?.reinterpret(), "Después de cada bloque")
    gtk_combo_box_set_active(comboCuándoDocu?.reinterpret(), 2)
    gtk_box_pack_start(subBoxDocu?.reinterpret(), comboCuándoDocu, 0, 0, 0u)

    // Toggle listener para CHECK 6
    val refDocu = StableRef.create(StableWidget(subBoxDocu?.reinterpret()))
    g_signal_connect_data(
        checkDocu?.reinterpret(),
        "toggled",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { checkBtn, userData ->
            val ref = userData?.asStableRef<StableWidget>()?.get()?.widget
            if (checkBtn != null && ref != null) {
                val active = gtk_toggle_button_get_active(checkBtn.reinterpret())
                gtk_widget_set_sensitive(ref, active)
            }
        }.reinterpret(),
        refDocu.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<StableWidget>()?.dispose()
        }.reinterpret(),
        0u
    )

    // --- BOTÓN GENERAR PROYECTO ---
    val btnGenerarProyecto = gtk_button_new_with_label("Generar instrucciones de Proyecto 🚀")
    gtk_box_pack_start(boxProyecto?.reinterpret(), btnGenerarProyecto, 0, 0, 10u)

    val widgetsProyecto = ProyectoWidgets(
        checkRol?.reinterpret(),
        entryRol?.reinterpret(),
        checkCtx?.reinterpret(),
        entryCtx?.reinterpret(),
        entryArq?.reinterpret(),
        checkEstilo?.reinterpret(),
        entryEstilo?.reinterpret(),
        checkReglas?.reinterpret(),
        entrySintaxis?.reinterpret(),
        entryModificar?.reinterpret(),
        entryArqFija?.reinterpret(),
        checkTests?.reinterpret(),
        entryTests?.reinterpret(),
        comboTests?.reinterpret(),
        checkDocu?.reinterpret(),
        entryDocuEscrita?.reinterpret(),
        comboFormatoDocu?.reinterpret(),
        comboCuándoDocu?.reinterpret()
    )
    val refWidgetsProyecto = StableRef.create(widgetsProyecto)

    g_signal_connect_data(
        btnGenerarProyecto?.reinterpret(),
        "clicked",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { _, userData ->
            val ref = userData?.asStableRef<ProyectoWidgets>()?.get()
            if (ref != null) {
                val matrix = mutableListOf<List<String>>()

                val activeRol = gtk_toggle_button_get_active(ref.checkRol?.reinterpret()) != 0
                matrix.add(listOf("checkRol", activeRol.toString()))
                matrix.add(listOf("entryRol", gtk_entry_get_text(ref.entryRol?.reinterpret())?.toKString() ?: ""))

                val activeCtx = gtk_toggle_button_get_active(ref.checkCtx?.reinterpret()) != 0
                matrix.add(listOf("checkCtx", activeCtx.toString()))
                matrix.add(listOf("entryCtx", gtk_entry_get_text(ref.entryCtx?.reinterpret())?.toKString() ?: ""))
                matrix.add(listOf("entryArq", gtk_entry_get_text(ref.entryArq?.reinterpret())?.toKString() ?: ""))

                val activeEstilo = gtk_toggle_button_get_active(ref.checkEstilo?.reinterpret()) != 0
                matrix.add(listOf("checkEstilo", activeEstilo.toString()))
                matrix.add(listOf("entryEstilo", gtk_entry_get_text(ref.entryEstilo?.reinterpret())?.toKString() ?: ""))

                val activeReglas = gtk_toggle_button_get_active(ref.checkReglas?.reinterpret()) != 0
                matrix.add(listOf("checkReglas", activeReglas.toString()))
                matrix.add(listOf("entrySintaxis", gtk_entry_get_text(ref.entrySintaxis?.reinterpret())?.toKString() ?: ""))
                matrix.add(listOf("entryModificar", gtk_entry_get_text(ref.entryModificar?.reinterpret())?.toKString() ?: ""))
                matrix.add(listOf("entryArqFija", gtk_entry_get_text(ref.entryArqFija?.reinterpret())?.toKString() ?: ""))

                val activeTests = gtk_toggle_button_get_active(ref.checkTests?.reinterpret()) != 0
                matrix.add(listOf("checkTests", activeTests.toString()))
                matrix.add(listOf("entryTests", gtk_entry_get_text(ref.entryTests?.reinterpret())?.toKString() ?: ""))
                val activeTextTests = gtk_combo_box_text_get_active_text(ref.comboTests?.reinterpret())
                matrix.add(listOf("comboTests", activeTextTests?.toKString() ?: ""))

                val activeDocu = gtk_toggle_button_get_active(ref.checkDocu?.reinterpret()) != 0
                matrix.add(listOf("checkDocu", activeDocu.toString()))
                matrix.add(listOf("entryDocuEscrita", gtk_entry_get_text(ref.entryDocuEscrita?.reinterpret())?.toKString() ?: ""))
                val activeTextFormato = gtk_combo_box_text_get_active_text(ref.comboFormatoDocu?.reinterpret())
                matrix.add(listOf("comboFormatoDocu", activeTextFormato?.toKString() ?: ""))
                val activeTextCuando = gtk_combo_box_text_get_active_text(ref.comboCuándoDocu?.reinterpret())
                matrix.add(listOf("comboCu\u00e1ndoDocu", activeTextCuando?.toKString() ?: ""))

                println("--- MATRIZ INSTRUCCIONES PROYECTO ---")
                matrix.forEach { row ->
                    println("${row.getOrNull(0)} = ${row.getOrNull(1)}")
                }
            }
        }.reinterpret(),
        refWidgetsProyecto.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<ProyectoWidgets>()?.dispose()
        }.reinterpret(),
        0u
    )

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
    val commitEntriesList = mutableListOf<CPointer<GtkWidget>?>()
    prefijos.forEachIndexed { i, (labelStr, defaultVal) ->
        val lbl = gtk_label_new(labelStr)
        gtk_label_set_xalign(lbl?.reinterpret(), 0.0f)
        val entry = gtk_entry_new()
        gtk_entry_set_text(entry?.reinterpret(), defaultVal)
        gtk_grid_attach(gridCommits?.reinterpret(), lbl, 0, i, 1, 1)
        gtk_grid_attach(gridCommits?.reinterpret(), entry, 1, i, 1, 1)
        commitEntriesList.add(entry?.reinterpret())
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

    // --- BOTÓN GENERAR ESTÁNDAR ---
    val btnGenerarEstandar = gtk_button_new_with_label("Generar instrucciones Estándar 🚀")
    gtk_box_pack_start(boxEstandar?.reinterpret(), btnGenerarEstandar, 0, 0, 10u)

    val widgetsEstandar = EstandarWidgets(
        checkBoxParaCommits?.reinterpret(),
        entryInicio?.reinterpret(),
        entryInstruccionesCommits?.reinterpret(),
        commitEntriesList,
        comboEstilo?.reinterpret(),
        checkBoxParaCabecerasDeClases?.reinterpret(),
        entryDescClase?.reinterpret(),
        checkClaseAutor?.reinterpret(),
        checkClaseFecha?.reinterpret(),
        checkClaseFile?.reinterpret(),
        checkBoxParaCabecerasFunciones?.reinterpret(),
        entryDescFunc?.reinterpret(),
        checkFuncAutor?.reinterpret(),
        checkFuncFecha?.reinterpret(),
        checkFuncParams?.reinterpret(),
        checkFuncReturn?.reinterpret(),
        checkFuncThrows?.reinterpret(),
        checkBoxParaComentariosInLine?.reinterpret(),
        entryInlineDesc?.reinterpret(),
        checkBoxParaNamesBranch?.reinterpret(),
        entryBranchDesc?.reinterpret()
    )
    val refWidgetsEstandar = StableRef.create(widgetsEstandar)

    g_signal_connect_data(
        btnGenerarEstandar?.reinterpret(),
        "clicked",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { _, userData ->
            val ref = userData?.asStableRef<EstandarWidgets>()?.get()
            if (ref != null) {
                val matrix = mutableListOf<List<String>>()

                val activeCommits = gtk_toggle_button_get_active(ref.checkBoxParaCommits?.reinterpret()) != 0
                matrix.add(listOf("checkBoxParaCommits", activeCommits.toString()))
                matrix.add(listOf("entryInicio", gtk_entry_get_text(ref.entryInicio?.reinterpret())?.toKString() ?: ""))
                matrix.add(listOf("entryInstruccionesCommits", gtk_entry_get_text(ref.entryInstruccionesCommits?.reinterpret())?.toKString() ?: ""))
                
                val labels = listOf("Error/Bugfix", "Nueva característica", "Cambios en docu", "Cambios de estilo", "Refactor", "Pruebas")
                ref.commitEntries.forEachIndexed { idx, entryPtr ->
                    val text = gtk_entry_get_text(entryPtr?.reinterpret())?.toKString() ?: ""
                    matrix.add(listOf("commitPrefix_" + labels.getOrNull(idx), text))
                }

                val activeTextEstilo = gtk_combo_box_text_get_active_text(ref.comboEstilo?.reinterpret())
                matrix.add(listOf("comboEstilo", activeTextEstilo?.toKString() ?: ""))

                val activeClases = gtk_toggle_button_get_active(ref.checkBoxParaCabecerasDeClases?.reinterpret()) != 0
                matrix.add(listOf("checkBoxParaCabecerasDeClases", activeClases.toString()))
                matrix.add(listOf("entryDescClase", gtk_entry_get_text(ref.entryDescClase?.reinterpret())?.toKString() ?: ""))
                matrix.add(listOf("checkClaseAutor", (gtk_toggle_button_get_active(ref.checkClaseAutor?.reinterpret()) != 0).toString()))
                matrix.add(listOf("checkClaseFecha", (gtk_toggle_button_get_active(ref.checkClaseFecha?.reinterpret()) != 0).toString()))
                matrix.add(listOf("checkClaseFile", (gtk_toggle_button_get_active(ref.checkClaseFile?.reinterpret()) != 0).toString()))

                val activeFuncs = gtk_toggle_button_get_active(ref.checkBoxParaCabecerasFunciones?.reinterpret()) != 0
                matrix.add(listOf("checkBoxParaCabecerasFunciones", activeFuncs.toString()))
                matrix.add(listOf("entryDescFunc", gtk_entry_get_text(ref.entryDescFunc?.reinterpret())?.toKString() ?: ""))
                matrix.add(listOf("checkFuncAutor", (gtk_toggle_button_get_active(ref.checkFuncAutor?.reinterpret()) != 0).toString()))
                matrix.add(listOf("checkFuncFecha", (gtk_toggle_button_get_active(ref.checkFuncFecha?.reinterpret()) != 0).toString()))
                matrix.add(listOf("checkFuncParams", (gtk_toggle_button_get_active(ref.checkFuncParams?.reinterpret()) != 0).toString()))
                matrix.add(listOf("checkFuncReturn", (gtk_toggle_button_get_active(ref.checkFuncReturn?.reinterpret()) != 0).toString()))
                matrix.add(listOf("checkFuncThrows", (gtk_toggle_button_get_active(ref.checkFuncThrows?.reinterpret()) != 0).toString()))

                val activeInline = gtk_toggle_button_get_active(ref.checkBoxParaComentariosInLine?.reinterpret()) != 0
                matrix.add(listOf("checkBoxParaComentariosInLine", activeInline.toString()))
                matrix.add(listOf("entryInlineDesc", gtk_entry_get_text(ref.entryInlineDesc?.reinterpret())?.toKString() ?: ""))

                val activeBranch = gtk_toggle_button_get_active(ref.checkBoxParaNamesBranch?.reinterpret()) != 0
                matrix.add(listOf("checkBoxParaNamesBranch", activeBranch.toString()))
                matrix.add(listOf("entryBranchDesc", gtk_entry_get_text(ref.entryBranchDesc?.reinterpret())?.toKString() ?: ""))

                println("--- MATRIZ INSTRUCCIONES ESTÁNDAR ---")
                matrix.forEach { row ->
                    println("${row.getOrNull(0)} = ${row.getOrNull(1)}")
                }
            }
        }.reinterpret(),
        refWidgetsEstandar.asCPointer(),
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<EstandarWidgets>()?.dispose()
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
