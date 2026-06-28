# Ideas Simples de Comandos Kotlin/Native

Herramientas pequeñas, concretas y útiles. Perfectas para practicar sin perderse en complejidades innecesarias.

---

## 25. `kecho` (Echo con Color)

**Propósito:** Como el comando `echo` pero con soporte de color directo: `./kecho --red "Error crítico"` o `./kecho --green "Todo bien"`. Sin necesidad de recordar códigos ANSI.

*   **Reto POSIX:** Escribir en `stdout` usando `write()` o `fputs()` con los códigos de escape ANSI (`\033[31m` para rojo, etc.). Parsear los argumentos de la línea de comandos manualmente con el array `argv`.

---

## 26. `kcount` (Contador de Líneas, Palabras y Caracteres)

**Propósito:** Como el comando `wc`. Pasas un archivo y te dice cuántas líneas, palabras y caracteres tiene: `./kcount archivo.txt`. También acepta texto por pipe: `cat archivo.txt | ./kcount`.

*   **Reto POSIX:** Leer el archivo byte a byte con `fgetc()` y llevar tres contadores. Detectar si la entrada viene de un pipe comprobando si `stdin` es un terminal con `isatty(0)`.

---

## 27. `krename` (Renombrado en Masa)

**Propósito:** Renombra múltiples archivos a la vez con un patrón: `./krename "*.log" "backup_*.log"` añade el prefijo `backup_` a todos los `.log` de la carpeta actual.

*   **Reto POSIX:** Iterar archivos con `opendir()` y `readdir()`. Filtrar por extensión comparando strings. Renombrar usando la syscall `rename()` que es atómica a nivel de sistema de archivos.

---

## 28. `khead` / `ktail` (Lector de Extremos de Archivo)

**Propósito:** Muestra las primeras o últimas N líneas de un archivo: `./khead 5 archivo.txt` o `./ktail 10 archivo.txt`. Simple y directo.

*   **Reto POSIX:** Para `khead`, leer línea a línea con `fgets()` y parar al llegar a N. Para `ktail`, el reto real es ir al final del archivo con `fseek(f, 0, SEEK_END)` y recorrer hacia atrás buscando saltos de línea.

---

## 29. `kask` (Prompt Interactivo Simple)

**Propósito:** Pide confirmación al usuario antes de ejecutar algo peligroso: `./kask "¿Borrar todo?" && rm -rf ./build`. Si el usuario escribe `s` o `y`, devuelve éxito. Si escribe cualquier otra cosa, aborta.

*   **Reto POSIX:** Leer un solo carácter de `stdin` con `fgetc()`. Comprobar el valor y retornar el código de salida correcto con `exit(0)` o `exit(1)` para que funcione encadenado con `&&` en la shell.

---

## 30. `kclear` (Limpiador de Terminal Mejorado)

**Propósito:** Limpia la terminal como `clear` pero también mueve el cursor arriba del todo y opcionalmente imprime una línea decorativa de separación con la hora actual: `./kclear` o `./kclear --banner`.

*   **Reto POSIX:** Obtener la hora actual con `time()` y `localtime()`. Escribir las secuencias de escape ANSI de limpieza (`\033[2J\033[H`) directamente en `stdout`. Calcular el ancho del terminal leyendo la estructura `winsize` con la llamada `ioctl(STDOUT_FILENO, TIOCGWINSZ, &w)`.

---

## 31. `ktype` (Detector de Tipo de Archivo)

**Propósito:** Averigua qué tipo de archivo es sin fiarse de la extensión. `./ktype foto.jpg` confirma si realmente es una imagen JPEG o si alguien le cambió el nombre. Lee los primeros bytes ("magic bytes") del archivo.

*   **Reto POSIX:** Abrir el archivo con `fopen()` y leer solo los primeros 8 bytes con `fread()`. Comparar contra firmas conocidas: JPEG empieza con `FF D8 FF`, PNG con `89 50 4E 47`, ZIP con `50 4B 03 04`, etc.

---

## 32. `klines` (Numerador de Líneas)

**Propósito:** Lee un archivo y lo imprime con números de línea a la izquierda, como hace `cat -n` pero con formato mejorado y color opcional para los números: `./klines archivo.kt`.

*   **Reto POSIX:** Leer el archivo línea a línea con `fgets()`, llevar un contador entero e imprimirlo formateado con `printf()` usando `%4d` para alinear correctamente aunque haya miles de líneas.

---

## 33. `krepeat` (Repetidor de Comandos)

**Propósito:** Ejecuta un comando N veces o cada X segundos: `./krepeat 5 echo "hola"` lo ejecuta 5 veces. `./krepeat --every 2s date` imprime la fecha cada 2 segundos hasta que pulsas Ctrl+C.

*   **Reto POSIX:** Usar `fork()` y `execvp()` para lanzar el comando como proceso hijo, y `waitpid()` para esperar a que termine. Para el modo periódico, usar `sleep()` o `nanosleep()` entre iteraciones y capturar `SIGINT` con `signal()` para salir limpiamente.

---

## 34. `khash` (Calculador de Hash de Archivos)

**Propósito:** Calcula el hash de un archivo para verificar su integridad: `./khash archivo.zip` muestra un hash único. Si el archivo cambia aunque sea un byte, el hash cambia completamente. Implementa un algoritmo simple como djb2 o FNV-1a.

*   **Reto POSIX:** Leer el archivo en bloques con `fread()` e ir acumulando el hash byte a byte con una función matemática sencilla. No hace falta implementar SHA-256 completo: djb2 (`hash = hash * 33 + c`) es suficiente para practicar y produce resultados visualmente convincentes.
