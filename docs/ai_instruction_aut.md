# 🤖 Generador de Instrucciones Automatizado (Generador_Instrucciones)

`ai_instruction_aut` es una herramienta gráfica nativa desarrollada en **Kotlin/Native** utilizando **GTK3**. Su objetivo principal es facilitar la configuración estandarizada de proyectos, generando automáticamente archivos de directrices de proyecto o archivos `.md` de reglas técnicas (instrucciones) mediante la IA de Gemini.

---

## ✨ Características Principales

*   **Interfaz Gráfica Multi-Pestaña (GTK3):** Interfaz dividida en pestañas que diferencian configuraciones a nivel de "Proyecto" (arquitectura, roles, testing) y a nivel "Estándar" (reglas de commit, cabeceras de código).
*   **Generación Inteligente de Prompts:** Transforma selecciones de la interfaz (Checkbox, ComboBox, Entry) en prompts estructurados listos para ser asimilados por una IA técnica.
*   **Integración con Gemini (libcurl):** Uso nativo de llamadas en red sin JVM usando C (`libcurl`) para comunicarse con la API de Google Gemini y extraer de forma limpia el Markdown generado.
*   **Salida Automática a Archivos:** El resultado generado por la IA es anexado en tiempo real en archivos locales (`instrucciones_proyecto.md` y `instrucciones_estandar.md`).

---

## 📂 Estructura del Código (`src/ai_instruction_aut/`)

### 1. [main.kt](file:///home/figue/Documentos/KotlinNative/src/ai_instruction_aut/main.kt)
El controlador principal de la GUI y la inicialización de la aplicación:
*   Define y renderiza las pestañas (Notebook), CheckBoxes, Entradas de texto y ComboBoxes mediante interop con la biblioteca GTK3 de C.
*   Extrae el estado de cada widget gráfico para transformarlo en una matriz de configuración temporal.
*   Invoca a los módulos generadores en función de qué botón ha sido pulsado.

### 2. [write_instruction_project.kt](file:///home/figue/Documentos/KotlinNative/src/ai_instruction_aut/write_instruction_project.kt)
Maneja las reglas a nivel de proyecto (Pestaña 1):
*   Recibe la matriz de datos, la cruza con las descripciones técnicas y construye el bloque de requerimientos.
*   Aplica un *System Prompt* exhaustivo orientado a que la IA actúe de manera profesional y detallada.
*   Llama al gestor de la IA y escribe de forma nativa (`fopen`, `fputs`) en `instrucciones_proyecto.md`.

### 3. [write_instruction_estand.kt](file:///home/figue/Documentos/KotlinNative/src/ai_instruction_aut/write_instruction_estand.kt)
Maneja las reglas de desarrollo estándar (Pestaña 2) y gestiona la llamada de red HTTP:
*   Construye las reglas de convención de Commits, Funciones y Clases basándose en las selecciones de la GUI.
*   Contiene la función `tratamientoConIA(instructions)`, encargada de encapsular el prompt en JSON, hacer la petición HTTP `POST` a Gemini usando `libcurl`, y limpiar la respuesta devuelta.
*   Escribe la salida nativamente en `instrucciones_estandar.md`.

### 4. [read_env.kt](file:///home/figue/Documentos/KotlinNative/src/ai_instruction_aut/read_env.kt)
Gestión robusta de configuración de entorno:
*   Encuentra y lee el archivo `.env` utilizando un sistema de cascada de rutas (buscando en `./`, `../`, `src/...`) para asegurar que funcione independientemente desde dónde se ejecute el binario (ej. `bin/` vs raíz).
*   Obtiene la API KEY necesaria para interactuar con Gemini.

---

## 🛠️ Requisitos y Dependencias

Dependencias idénticas a los demás módulos con interfaz en Linux:
*   Librerías de C en el sistema: `libcurl4-openssl-dev`, `libgtk-3-dev`.
*   Bindings nativos Klib generados en Kotlin/Native (`curl.klib`, `gtk3.klib`).

---

## 💻 Compilación y Ejecución

Compila desde la raíz del ecosistema con el siguiente comando:

```bash
# Compilar el ejecutable
kotlinc-native src/ai_instruction_aut/main.kt src/ai_instruction_aut/read_env.kt src/ai_instruction_aut/write_instruction_estand.kt src/ai_instruction_aut/write_instruction_project.kt -library cinterop/klib/curl.klib -library cinterop/klib/gtk3.klib -o bin/Generador_Instrucciones -opt-in=kotlinx.cinterop.ExperimentalForeignApi -linker-options -L/usr/lib/x86_64-linux-gnu

# Ejecutar el generador
./bin/Generador_Instrucciones.kexe
```

---

## ⚙️ Configuración del Archivo `.env`

Ubicado en `src/ai_instruction_aut/.env` (o creado junto al binario):

```env
GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/interactions"
GEMINI_API_KEY = "TU_GEMINI_API_KEY_AQUI"
MODEL = "gemini-2.0-flash"
```
