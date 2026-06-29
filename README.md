# Ecosistema Kotlin/Native - Linux Utilities

Este proyecto es una suite de utilidades de sistema y herramientas de desarrollo para Linux desarrolladas enteramente en **Kotlin/Native**. El ecosistema está diseñado para ofrecer ejecuciones de alto rendimiento y bajo consumo, eliminando por completo la necesidad de una máquina virtual (JVM).

---

## Arquitectura: Sin JVM

A diferencia del desarrollo tradicional en Kotlin que se compila a bytecode de Java para ser ejecutado por la JVM, **Kotlin/Native** utiliza el compilador de LLVM para compilar el código fuente de Kotlin directamente a código de máquina nativo (`.kexe` en Linux).

### Beneficios Clave:
*   **Arranque Instantáneo:** Al no requerir la inicialización de la JVM, las herramientas de línea de comandos (CLI) y las interfaces gráficas arrancan de forma instantánea.
*   **Bajo Consumo de Memoria:** Sin recolectores de basura pesados ni la infraestructura de la JVM, el consumo de RAM se reduce a unos pocos megabytes.
*   **Binarios Autónomos:** El resultado final es un binario autocontenido que se ejecuta directamente sobre el sistema operativo, facilitando la distribución sin dependencias externas.

---

## Interoperabilidad con C (`cinterop` & `.def`)

Kotlin/Native ofrece interoperabilidad bidireccional de alto rendimiento con el lenguaje C a través de la herramienta **`cinterop`**. Esto nos permite aprovechar todas las bibliotecas del sistema Linux (`libcurl`, `gtk3`, etc.).

### Flujo de Trabajo para Vincular una Librería de C:

1.  **Archivo de Definición (`.def`):** Se crea un archivo de configuración que le indica a Kotlin/Native qué cabeceras de C usar y cómo enlazarlas.
    *   *Ejemplo (`curl.def`):*
        ```ini
        headers = /usr/include/curl/curl.h
        headerFilter = /usr/include/curl/**
        linkerOpts = -lcurl
        ```
2.  **Generación del Binding (`.klib`):** Se procesa el archivo `.def` para generar una biblioteca de Kotlin (`.klib`) que expone las firmas de C como código Kotlin:
    ```bash
    cinterop -def cinterop/def/curl.def -o cinterop/klib/curl.klib
    ```
3.  **Compilación Final:** Se compila el código Kotlin enlazando el archivo `.klib` resultante (indicando la ruta del archivo `.klib` con `-library`):
    ```bash
    kotlinc-native main.kt -library cinterop/klib/curl.klib -o app.kexe
    ```

---

## Desarrollo de Herramientas CLI Nativa y Gestión de Memoria

El desarrollo de comandos de sistema (CLI) en Kotlin/Native hace uso extensivo de las librerías nativas de POSIX de C expuestas en el paquete `platform.posix.*`.

### Gestión de Memoria Nativa:
Dado que interactuamos con estructuras de C que no son gestionadas por el recolector de basura de Kotlin, es crítico controlar el ciclo de vida de la memoria para evitar fugas (memory leaks) o accesos a punteros liberados (dangling pointers):

*   **`memScoped`:** Delimita un bloque de memoria nativa de asignación temporal. Toda la memoria asignada dentro de este bloque (usando `.cstr.ptr`, `.alloc()`, etc.) se libera automáticamente al salir del bloque.
*   **`StableRef`:** Permite envolver un objeto de Kotlin en un puntero nativo de C (`COpaquePointer`) para pasarlo a callbacks asíncronos de C (como los de GTK3) de forma segura, garantizando que el recolector de basura no lo destruya mientras C lo esté usando.

### Ejemplo de paso de memoria de Kotlin a C:
```kotlin
memScoped {
    // Convierte el String de Kotlin a un C-string temporal
    val cString = "Texto de Kotlin".cstr.ptr 
    // Llamada segura a una función de C que espera const char*
    platform.posix.printf("%s\n", cString)
} // La memoria de cString se libera automáticamente aquí
```

---

## Estructura del Proyecto

El ecosistema está organizado en base a características y comandos CLI:

```text
KotlinNative/
│
├── bin/                                # Ejecutables binarios resultantes (.kexe)
├── cinterop/                           # Carpeta de interoperabilidad con C
│   ├── def/                            # Archivos de definición (.def)
│   │   ├── curl.def
│   │   └── gtk3.def
│   └── klib/                           # Librerías de bindings compiladas (.klib)
│       ├── curl.klib
│       └── gtk3.klib
│
├── docs/                               # Documentación detallada de cada módulo
│   ├── ai_developer.md                 # Documentación del optimizador de prompts
│   ├── kfind.md                        # Documentación del comando de búsqueda
│   ├── kls.md                          # Documentación del comando de listado
│   └── ktree.md                        # Documentación del comando de árbol
│
├── src/                                # Código fuente en Kotlin
│   ├── ai_developer/                   # Optimizador de prompts asistido por IA (GTK3 + Curl)
│   │   ├── readFiles/                  # Módulos de lectura nativa (Contexto y Env)
│   │   ├── context/                    # Carpeta para archivos de contexto del proyecto
│   │   ├── main.kt                     # UI Gráfica con GTK3
│   │   └── ai_gestor.kt                # Integración con la API de Gemini (Curl)
│   │
│   └── comands/                        # Utilidades CLI del sistema (POSIX nativo)
│       ├── kfind.kt                    # Buscador de archivos nativo
│       ├── kls.kt                      # Listador de archivos con metadatos
│       └── ktree.kt                    # Visualizador de árboles de directorios
│
├── .gitignore                          # Exclusión de entornos (.env) y binarios (.klib, .kexe)
└── README.md                           # Documento principal del ecosistema (este archivo)
```

---

## Guía de Configuración e Instalación

### Requisitos Previos:
Necesitas disponer de un compilador de C y las cabeceras de desarrollo de las bibliotecas que vayas a enlazar en tu sistema operativo Linux:

```bash
# Instalar compilador de C y herramientas de desarrollo para Linux (Ubuntu/Debian)
sudo apt update
sudo apt install build-essential libcurl4-openssl-dev libgtk-3-dev
```

### Compilar y Ejecutar Módulos:

#### 1. Compilar comandos CLI del sistema (ej: `kls.kt`):
Los comandos del sistema no dependen de librerías de C externas complejas ya que usan POSIX básico:
```bash
# Compilar kls
kotlinc-native src/comands/kls.kt -o bin/kls

# Ejecutar kls
./bin/kls.kexe
```

#### 2. Compilar aplicaciones con interoperabilidad (ej: `ai_developer`):
Requiere generar los bindings de GTK3 y Curl en la carpeta `cinterop/klib` y luego compilar:
```bash
# Generar bindings .klib desde los archivos .def (ejecutar en la raíz)
cinterop -def cinterop/def/curl.def -o cinterop/klib/curl.klib
cinterop -def cinterop/def/gtk3.def -o cinterop/klib/gtk3.klib

# Compilar desde la raíz del proyecto indicando los archivos fuente y las librerías .klib
kotlinc-native src/ai_developer/main.kt src/ai_developer/ai_gestor.kt src/ai_developer/readFiles/read_context.kt src/ai_developer/readFiles/read_env.kt \
  -library cinterop/klib/curl.klib \
  -library cinterop/klib/gtk3.klib \
  -o bin/Up_Prompts \
  -opt-in=kotlinx.cinterop.ExperimentalForeignApi \
  -linker-options -L/usr/lib/x86_64-linux-gnu

# Ejecutar el optimizador
./bin/Up_Prompts.kexe
```
