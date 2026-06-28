import gtk3.*
import kotlinx.cinterop.*

class AppWidgets(
    val promptEntry: CPointer<GtkEntry>?,
    val contextEntry: CPointer<GtkEntry>?,
    val textView: CPointer<GtkTextView>?
)

@OptIn(ExperimentalForeignApi::class)
fun main() {
    // 1. Inicializar el entorno gráfico de Linux
    gtk_init(null, null)

    // 2. Crear la ventana principal
    val window = gtk_window_new(GtkWindowType.GTK_WINDOW_TOPLEVEL)
    gtk_window_set_title(window?.reinterpret(), "Optimizador de Prompts - Gemini API")
    gtk_window_set_default_size(window?.reinterpret(), 600, 500)
    gtk_container_set_border_width(window?.reinterpret(), 15u)

    // 3. Crear una caja vertical para organizar los elementos
    val vbox = gtk_box_new(GtkOrientation.GTK_ORIENTATION_VERTICAL, 12)
    gtk_container_add(window?.reinterpret(), vbox)

    // --- ENTRADA DEL PROMPT ---
    val promptLabel = gtk_label_new("Escribe tu instrucción o prompt:")
    gtk_label_set_xalign(promptLabel?.reinterpret(), 0.0f)
    gtk_box_pack_start(vbox?.reinterpret(), promptLabel, 0, 0, 0u)

    val promptEntry = gtk_entry_new()
    gtk_entry_set_placeholder_text(promptEntry?.reinterpret(), "Ej: Mejora visualmente el archivo de la screen...")
    gtk_box_pack_start(vbox?.reinterpret(), promptEntry, 0, 0, 0u)

    // --- ENTRADA DE LA RUTA DE CONTEXTO ---
    val contextLabel = gtk_label_new("Ruta de la carpeta de contexto:")
    gtk_label_set_xalign(contextLabel?.reinterpret(), 0.0f)
    gtk_box_pack_start(vbox?.reinterpret(), contextLabel, 0, 0, 0u)

    val contextEntry = gtk_entry_new()
    gtk_entry_set_text(contextEntry?.reinterpret(), "./context")
    gtk_box_pack_start(vbox?.reinterpret(), contextEntry, 0, 0, 0u)

    // --- BOTÓN DE ENVIAR ---
    val button = gtk_button_new_with_label("Optimizar con Inteligencia Artificial 🚀")
    gtk_box_pack_start(vbox?.reinterpret(), button, 0, 0, 5u)

    // --- ÁREA DE RESULTADO ---
    val resultLabel = gtk_label_new("Resultado obtenido:")
    gtk_label_set_xalign(resultLabel?.reinterpret(), 0.0f)
    gtk_box_pack_start(vbox?.reinterpret(), resultLabel, 0, 0, 0u)

    val scrolledWindow = gtk_scrolled_window_new(null, null)
    gtk_scrolled_window_set_policy(scrolledWindow?.reinterpret(), GtkPolicyType.GTK_POLICY_AUTOMATIC, GtkPolicyType.GTK_POLICY_AUTOMATIC)
    
    val textView = gtk_text_view_new()
    gtk_text_view_set_editable(textView?.reinterpret(), 0) // Solo lectura
    gtk_text_view_set_wrap_mode(textView?.reinterpret(), GtkWrapMode.GTK_WRAP_WORD)
    gtk_container_add(scrolledWindow?.reinterpret(), textView)
    
    // Empaquetar el Scroll area de forma que se expanda para llenar el espacio libre
    gtk_box_pack_start(vbox?.reinterpret(), scrolledWindow, 1, 1, 0u)

    // 4. Crear estructura para pasar las referencias al callback
    val widgets = AppWidgets(
        promptEntry?.reinterpret(),
        contextEntry?.reinterpret(),
        textView?.reinterpret()
    )
    val stableRef = StableRef.create(widgets)
    val userDataPtr = stableRef.asCPointer()

    // 5. Configurar lógica del botón (Evento Click)
    g_signal_connect_data(
        button?.reinterpret(),
        "clicked",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { _, userData ->
            val ref = userData?.asStableRef<AppWidgets>()?.get()
            if (ref != null) {
                val promptText = gtk_entry_get_text(ref.promptEntry)?.toKString() ?: ""
                val contextPath = gtk_entry_get_text(ref.contextEntry)?.toKString() ?: ""
                
                val buffer = gtk_text_view_get_buffer(ref.textView)
                gtk_text_buffer_set_text(buffer, "Procesando petición en Gemini... Por favor espera.", -1)
                
                // Forzar a GTK a repintar la pantalla para que muestre el texto de carga
                while (gtk_events_pending() != 0) {
                    gtk_main_iteration()
                }
                
                // Realizar la llamada HTTP a la API
                val respuesta = enviarPromptAI(promptText, contextPath)
                
                // Mostrar el resultado de la API en el TextView
                gtk_text_buffer_set_text(buffer, respuesta, -1)
            }
        }.reinterpret(),
        userDataPtr,
        staticCFunction<COpaquePointer?, Unit> { userData ->
            userData?.asStableRef<AppWidgets>()?.dispose()
        }.reinterpret(),
        0u
    )

    // 6. Configurar evento para cerrar la app al destruir la ventana
    g_signal_connect_data(
        window?.reinterpret(),
        "destroy",
        staticCFunction<COpaquePointer?, COpaquePointer?, Unit> { _, _ -> gtk_main_quit() }
                .reinterpret(),
        null,
        null,
        0u
    )

    // 7. Mostrar todos los widgets y arrancar el loop nativo de GTK
    gtk_widget_show_all(window)
    gtk_main()
}
