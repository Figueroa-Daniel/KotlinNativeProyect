# 🚀 AI Developer (Up_Prompts)

`ai_developer` es una herramienta gráfica nativa en **Kotlin/Native** diseñada para Linux (sin depender de la JVM de Java). Su propósito es actuar como un **Optimizador de Prompts Experto**, permitiéndote estructurar instrucciones técnicas y detalladas basadas en la arquitectura actual de tu proyecto (ej: Clean Architecture para Android) para que sean interpretadas con total precisión por otras IAs de desarrollo.

---

## ✨ Características Principales

*   **Interfaz Gráfica Nativa (GUI):** Ventana de escritorio desarrollada nativamente para Linux mediante **GTK3** (usando bindings nativos `cinterop` sin sobrecarga de runtime).
*   **Conexión de Red Eficiente con C (`libcurl`):** Realiza llamadas HTTP síncronas a la API de Gemini utilizando la librería nativa de C `libcurl`, asegurando máxima velocidad y consumo mínimo de recursos.
*   **Gestión de Memoria Segura:** Implementa bloques `memScoped` y punteros nativos (`StableRef`, `.cstr.ptr`) para evitar la recolección de basura prematura en los intercambios de datos entre Kotlin y C.
*   **Carga de Contexto Local:** Escanea un directorio local especificado (ej. `./context`) mediante llamadas de bajo nivel del sistema POSIX (`opendir`, `readdir`) para inyectar automáticamente la estructura y guías de estilo de tu proyecto en el prompt de la IA.
*   **Extractor Inteligente de Respuestas:** Limpia y parsea de forma manual la respuesta en formato JSON de Google, desescapando caracteres de control (`\n`, `\"`, `\t`) para pintar un Markdown limpio en pantalla.

---

## 📂 Estructura del Código (`src/ai_developer/`)

### 1. [main.kt](file:///home/figue/Documentos/KotlinNative/src/ai_developer/main.kt)
Controla el punto de entrada de la aplicación gráfica y la UI de GTK3:
*   Inicializa GTK3 y renderiza la ventana (600x500 píxeles).
*   Provee cajas de texto (`GtkEntry`) para introducir la instrucción del usuario y la ruta del contexto del proyecto.
*   Implementa un contenedor de desplazamiento automático (`GtkScrolledWindow` con `GtkTextView`) para leer la respuesta.
*   Implementa un vaciador de hilos de GTK (`gtk_events_pending`, `gtk_main_iteration`) para repintar la interfaz gráfica y mostrar el mensaje de carga (*"Procesando petición en Gemini..."*) antes de la llamada de red bloqueante.

### 2. [ai_gestor.kt](file:///home/figue/Documentos/KotlinNative/src/ai_developer/ai_gestor.kt)
Orquesta la lógica de negocio y la petición de red:
*   Genera el Prompt de Ingeniería de Software enriqueciendo la orden del usuario con las plantillas de reglas de arquitectura leídas en el contexto.
*   Construye el JSON exacto para el endpoint `/v1beta/interactions` de Google Gemini.
*   Configura las cabeceras HTTP (`x-goog-api-key`, `Content-Type`) e inicializa el manejador de Curl.
*   Busca de forma robusta la comilla de cierre de la propiedad `"text"` (omitiendo comillas escapadas `\"`) para aislar la respuesta en texto plano de la IA.

### 3. [readFiles/read_context.kt](file:///home/figue/Documentos/KotlinNative/src/ai_developer/readFiles/read_context.kt)
Administra la lectura del contexto del proyecto:
*   Abre directorios nativos y lee cada archivo regular en memoria para concatenar el estado actual del proyecto.

### 4. [readFiles/read_env.kt](file:///home/figue/Documentos/KotlinNative/src/ai_developer/readFiles/read_env.kt)
Cargador híbrido y tolerante de variables de entorno:
*   Primero intenta leer del entorno real del sistema operativo (`getenv`).
*   Si no se encuentra, busca en un archivo físico `.env` con lógica de fallbacks automáticos (`.env`, `../.env`, `src/ai_developer/.env`), permitiendo que el binario funcione sin importar desde qué directorio se ejecute.

---

## 🛠️ Requisitos de Compilación

Para compilar el proyecto en Linux (distribuciones como Ubuntu o Zorin OS), necesitas tener instaladas las siguientes dependencias de sistema:

```bash
# Instalar bibliotecas de desarrollo de curl y gtk3
sudo apt update
sudo apt install libcurl4-openssl-dev libgtk-3-dev
```

Además, en el directorio de compilación se requieren los binarios `.klib` generados previamente con `cinterop`:
*   `curl.klib` (interfaz nativa para libcurl).
*   `gtk3.klib` (interfaz nativa para GTK3).

---

## 💻 Compilación y Ejecución

Compila todos los archivos fuente de la aplicación enlazando ambas bibliotecas nativas y asignando la ruta del linker de 64 bits:

```bash
# Compilar el ejecutable
kotlinc-native main.kt ai_gestor.kt readFiles/read_context.kt readFiles/read_env.kt -library curl.klib -library gtk3.klib -o Up_Prompts -opt-in=kotlinx.cinterop.ExperimentalForeignApi -linker-options -L/usr/lib/x86_64-linux-gnu

# Ejecutar la aplicación
./Up_Prompts.kexe
```

---

## ⚙️ Configuración del Archivo `.env`

La herramienta busca un archivo `.env` en la raíz de su ejecución para inicializar el cliente de Gemini:

```env
GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/interactions"
GEMINI_API_KEY = "TU_GEMINI_API_KEY_AQUI"
MODEL = "gemini-2.5-flash-lite"
SYSTEM_INSTRUCTION = "Tu único objetivo es actuar como un Optimizador de Prompts Experto..."
```
