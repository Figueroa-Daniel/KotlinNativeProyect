# Ideas de Comandos Kotlin/Native

Dado el increíble potencial que proporciona combinar **Kotlin** con la **API POSIX de C** para operar en sistemas Linux a muy bajo nivel, aquí hay algunas ideas para extender el repertorio de "Coreutils" que has empezado.

## 1. `kgrep` (Buscador interno de texto)
**Propósito:** Al igual que `grep`, leería archivos para encontrar coincidencias exactas.
*   **Reto Posix:** Abrir archivos con `fopen()`, leer su contenido con buffers estáticos en C o directamente usar la API nativa de Kotlin `readBytes()` u otros y aplicar expresiones regulares.
*   **Característica Especial:** Salida colorida y fácil de leer.

## 2. `kcat` / `kview` (Visor de archivos mejorado)
**Propósito:** Sustituto de `cat` y `bat` para la visualización de un archivo plano.
*   **Reto:** Incluir números de línea autogenerados a la izquierda y quizás hacer el mapeo de colores básicos reconociendo automáticamente extensiones como `.json` o `.csv`.

## 3. `kdu` (Scanner de espacio en disco)
**Propósito:** Analiza carpetas y cuenta el peso total en MegaBytes o GigaBytes.
*   **Reto:** Utilizar `stat` y su parámetro `st_size` sumándolo recursivamente, gestionando que no se quede bloqueado en rutas inaccesibles por permisos (usar permisos y checks de retorno correctos). Mostraría por ejemplo un "Top 10" de lo más pesado.

## 4. `kmkdir` (Creador Inteligente)
**Propósito:** Crear una ruta de carpetas completa junto al archivo vacío, e.g. `kmkdir ./src/components/button.kt`.
*   **Reto:** Analizar la String (Tokenizar en array con el separador `/`), recorrer el array, ir haciendo de forma segura el uso de la syscall `mkdir()`, y si es un componente final abrir el archivo con `fopen()` y cerrarlo.

## 5. `ktodo` (Gestor de Tareas de Terminal)
**Propósito:** Añadir tareas como `./ktodo add "Aprender Kotlin Native"` y luego mostrarlas.
*   **Reto:** Usar variables de entorno de Linux (ej. `user.home` dir) para hallar y leer/escribir persistemente un archivo `.ktodo.json` en segundo plano cada vez que se ejecutra el comando.

## 6. `kfetch` (Sistema de Información OS)
**Propósito:** Imprimir la RAM, CPU y el Sistema Operativo del equipo junto a un arte de ASCII en color de Kotlin.
*   **Reto:** Analizar la syscall POSIX `uname()` e interactuar con los archivos virtuales de Linux dentro de `/proc/` y `/sys/`.
