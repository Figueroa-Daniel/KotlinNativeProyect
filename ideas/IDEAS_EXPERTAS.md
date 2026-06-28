# Ideas Nivel Experto (Magia Oscura en Linux)

Si ya dominas la lectura de archivos, los sockets y la recursividad estructurada de carpetas, el siguiente nivel es interactuar directamente con los subsistemas más profundos del Kernel de Linux usando Kotlin/Native.

## 11. `kwatch` (Vigilante Inmediato de Archivos)
**Propósito:** Como las herramientas modernas `nodemon` o el clásico `inotifywait`. Escribes `./kwatch mi_texto.txt` y el programa se queda "congelado" de fondo sin gastar CPU. En el milisegundo exacto en que alguien guarda cambios en ese texto, tu programa despierta y te avisa.
*   **Reto POSIX:** Usar el subsistema del kernel de Linux llamado **`inotify`** (`sys/inotify.h`). Necesitas crear una instancia `inotify_init()` y agregar "Watches" para que el mismísimo núcleo operativo de Linux despierte a tu binario Kotlin interrumpiéndolo en tiempo real. 

## 12. `kperm` (Gestor de Permisos de Terminal Interactivo)
**Propósito:** En Linux todos odiamos tener que recordar si cambiar permisos era `chmod 755` o `chmod 644`. Esta herramienta listará un archivo y, leyendo pulsaciones de flechas del teclado, te dejará activar o desactivar casillas visuales `[x] Lectura  [ ] Escritura  [x] Ejecución`.
*   **Reto POSIX:** Entrar en el oscuro modo "Raw" de consola. De serie, las terminales de Linux esperan a que pulses "Enter" para enviar tus comandos. Usando la estructura `termios` (`tcgetattr` y `tcsetattr`), puedes desactivar el eco de la terminal para interceptar *keystrokes* individuales (como flecha abajo o flecha arriba) al instante en modo crudo. Cambiar permisos se hace usando la syscall `chmod()`.

## 13. `kar` (Kotlin Archiver / Empaquetador crudo)
**Propósito:** Como el famoso formato `tar` antiguo. Un empaquetador que une diez archivos pequeños diferentes en un solo gran archivo "sólido" (`mis_archivos.kar`), y que luego tiene una función para desempaquetarlo y recrear los 10 archivos.
*   **Reto POSIX:** Manipulación dura de Bytes en disco. Tendrías que definir tú mismo una cabecera binaria (*Header*), escribiendo con `fwrite()` los largos de los nombres y offsets, para después inyectar el contenido bruto (*payload*) de todos los archivos consecutivamente usando bloques seguros de memoria de C.

## 14. `ksudo` o `kauth` (Simulador o Invocador de altos privilegios)
**Propósito:** Un programa que verifique estrictamente si tienes permisos de Administrador (`root`) en el ordenador, y si no los tienes, rechace la ejecución o escale pidiendo contraseña de forma segura.
*   **Reto POSIX:** Funciones del Kernel como `getuid()` (para saber si el UID es igual a 0). Incluso podrías usar la API de C de PAM (Pluggable Authentication Modules) de Linux para validar una contraseña desde cero usando Kotlin.

## 15. `kstat` (Anatomía Pericial de un Archivo)
**Propósito:** Como el comando `stat` normal, pero más enfocado a un formato bonito. Escanea un archivo y te desglosa absolutamente todos sus metadatos ocultos.
*   **Reto POSIX:** Extraer la máxima información técnica de la estructura `stat`: Nodos Inode subyacentes mágicos del disco duro, tiempos en ticks UNIX exactos del último acceso de lectura que alguien hizo sobre el archivo, propietario actual en números de grupo, etc.
