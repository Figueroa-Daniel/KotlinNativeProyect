# Ideas Frescas de Comandos Kotlin/Native

Ideas distintas, enfocadas en herramientas del día a día de un desarrollador, pensadas para que cada una explore una zona diferente de la API POSIX y del Kernel de Linux.

---

## 16. `kenv` (Inspector de Variables de Entorno)

**Propósito:** Una versión mejorada del clásico `env`. Ejecutas `./kenv` y ves todas tus variables de entorno del sistema formateadas en columnas de color, con búsqueda por nombre. Ejecutas `./kenv set EDITOR nano` y persiste el valor en tu `.bashrc` o `.zshrc` automáticamente.

*   **Reto POSIX:** Acceder al puntero global `environ` de C (`extern char** environ`) desde Kotlin/Native para leer todas las variables. Usar `getenv()` y `setenv()`. Para la escritura persistente, tendrás que abrir el archivo de configuración del shell con `fopen()` y añadir la línea al final con `fputs()`.

---

## 17. `klock` (Bloqueador de Archivos)

**Propósito:** Marca un archivo como "en uso exclusivo" a nivel de Kernel: `./klock informe.pdf`. Mientras esté bloqueado, ningún otro proceso puede escribirlo (aunque tenga permisos). Ejecutas `./klock --release informe.pdf` para liberarlo.

*   **Reto POSIX:** Usar la syscall `fcntl()` con `F_SETLK` / `F_SETLKW` para establecer *advisory locks* de escritura (`F_WRLCK`). Aprenderás cómo Linux coordina el acceso concurrente a archivos entre procesos rivales a nivel de Kernel, algo que librerías de alto nivel esconden completamente.

---

## 18. `ksplit` (Troceador de Archivos Grandes)

**Propósito:** Divide cualquier archivo binario o de texto en N trozos de tamaño fijo. Por ejemplo `./ksplit video.mp4 --size 50MB` genera `video.part1`, `video.part2`, etc. Y luego `./ksplit --join video.part*` los reconstruye en un único archivo idéntico al original (verificable con hash).

*   **Reto POSIX:** Trabajar con offsets binarios precisos usando `fseek()` y `ftell()`. Leer y escribir bloques crudos de bytes con `fread()` y `fwrite()`. Para la verificación, implementar un hash simple (suma de bytes XOR o CRC32) calculado manualmente byte a byte.

---

## 19. `knet` (Escáner de Puertos Local)

**Propósito:** Descubres qué puertos de tu propio ordenador están abiertos escuchando conexiones: `./knet scan`. Muestra una tabla con Puerto, Protocolo (TCP/UDP) y el nombre del servicio si lo reconoce (ej: puerto 22 → SSH, 80 → HTTP).

*   **Reto POSIX:** Leer el archivo virtual `/proc/net/tcp` y `/proc/net/tcp6` donde el Kernel escribe en tiempo real el estado de todos los sockets del sistema en hexadecimal. Deberás parsear ese formato crudo (`local_address`, `rem_address`, `state`), convertir los valores hex a IPs/puertos legibles y cruzarlos con `/proc/PID/fd/` para saber qué proceso los tiene abiertos.

---

## 20. `ksig` (Gestor de Señales de Procesos)

**Propósito:** Una interfaz amigable para enviar señales a procesos: `./ksig list` muestra todos los procesos actuales. `./ksig kill 1234` manda `SIGTERM` con gracia. `./ksig pause 1234` congela un proceso con `SIGSTOP` y `./ksig resume 1234` lo reactiva con `SIGCONT`.

*   **Reto POSIX:** Usar la syscall `kill(pid, signal)` (que a pesar del nombre sirve para mandar cualquier señal, no solo matar). Recorrer `/proc` para listar procesos. El reto real es manejar correctamente los errores de permisos (`EPERM`) cuando intentas señalizar procesos de otro usuario o del sistema.

---

## 21. `ktime` (Cronómetro de Comandos con Detalle)

**Propósito:** Como el comando `time`, pero con desglose completo. Escribes `./ktime sleep 2` y al acabar te muestra: tiempo real transcurrido, tiempo de CPU en modo usuario, tiempo de CPU en modo kernel, cambios de contexto voluntarios e involuntarios y fallos de página de memoria.

*   **Reto POSIX:** Usar la syscall `wait4()` o `waitpid()` con `RUSAGE_CHILDREN` para obtener la estructura `rusage` que el Kernel rellena al acabar un proceso hijo. También `fork()` + `execvp()` para lanzar el comando como hijo. Aprenderás cómo el sistema operativo contabiliza internamente el tiempo de CPU real vs. el "tiempo de pared" del reloj.

---

## 22. `kclip` (Portapapeles desde la Terminal)

**Propósito:** Lee y escribe en el portapapeles del sistema sin salir de la terminal. `./kclip copy "hola mundo"` copia el texto. `./kclip paste` pega lo que haya. Funciona también con pipes: `cat archivo.txt | ./kclip copy`.

*   **Reto POSIX:** Comunicarse con el servidor X11 o Wayland directamente via sockets de dominio Unix (`AF_UNIX`). Usando `socket()`, `connect()` y el protocolo de X11 a pelo (sin librerías como Xlib), enviar los mensajes binarios para interactuar con la selección `CLIPBOARD`. Una de las experiencias más reveladoras sobre cómo funciona realmente un entorno gráfico de Linux bajo el capó.

---

## 23. `ktemplate` (Generador de Proyectos)

**Propósito:** Crea la estructura completa de un proyecto a partir de plantillas locales. `./ktemplate new kotlin-app MiProyecto` genera carpetas, archivos base (`.gitignore`, `README.md`, `build.gradle.kts`) con el nombre del proyecto interpolado dentro. Las plantillas son carpetas normales que tú defines en `~/.ktemplates/`.

*   **Reto POSIX:** Recursividad de directorios con `opendir()` / `readdir()` / `mkdir()`. Hacer sustitución de variables en texto (buscar y reemplazar el token `{{PROJECT_NAME}}`) leyendo y escribiendo archivos byte a byte con buffers de C. Gestionar simbólicamente permisos de los archivos generados con `chmod()` según el tipo de archivo.

---

## 24. `kmem` (Inspector de Memoria de un Proceso)

**Propósito:** Espías la memoria de cualquier proceso en ejecución: `./kmem 1234` muestra un mapa de todas las regiones de memoria del proceso (heap, stack, librerías cargadas, código), sus tamaños y permisos (lectura, escritura, ejecución).

*   **Reto POSIX:** Leer y parsear `/proc/PID/maps`, un archivo virtual donde el Kernel expone el mapa de memoria virtual completo de cada proceso. El reto está en interpretar el formato hexadecimal de los rangos de direcciones, los flags `rwxp` y los nombres de los archivos mapeados. Para el nivel hardcore: usar `ptrace(PTRACE_ATTACH, ...)` para leer bytes concretos de la memoria de un proceso ajeno.
