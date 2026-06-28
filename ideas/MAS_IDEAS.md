# Más Ideas de Comandos (Nivel Avanzado)

Aquí tienes más conceptos interesantes y retadores para llevar tus habilidades con la API de C y Kotlin/Native a un nivel superior:

## 7. `ktree` (Árbol de directorios)
**Propósito:** Igual que el comando clásico de Linux `tree`. En lugar de solo listar archivos como `ls`, esta herramienta hace una exploración profunda y dibuja una jerarquía visual con caracteres (├─, └─).
*   **Reto POSIX:** Lógica recursiva avanzada. Necesitas llevar un registro (pasando arreglos o booleanos a la función) de cuándo un archivo es el "último" de una lista obtenida con `readdir()` para decidir si dibujas `├─` o `└─`.

## 8. `kps` o `ktop` (Monitor de Procesos)
**Propósito:** Listar todos los programas y procesos de fondo que se están ejecutando en tu actual sesión, similar a los comandos `ps` o el famoso `top`.
*   **Reto POSIX:** En Linux, TODO es un archivo. La información en vivo del sistema está en la carpeta `/proc`. El reto está en leer `/proc`, filtrar solo las carpetas que tienen números (los PIDs o Process IDs), entrar a ellas y leer el archivo `/proc/PID/status` usando `fopen()` para extraer la línea del "Name:".

## 9. `kping` (Testeador de conexión)
**Propósito:** Imitar a `ping 8.8.8.8`, donde envías un "latido" a un servidor de internet y mides en milisegundos cuánto tarda en responderte la luz/cable.
*   **Reto POSIX:** Entrar al mundo de las redes y los "Sockets". Implica aprender a crear Pings usando `socket(AF_INET, SOCK_RAW, IPPROTO_ICMP)`. Descubrirías como funcionan los paquetes de bajo nivel de internet.

## 10. `kcurl` (Peticiones Web Nativas)
**Propósito:** Un mini `curl`. Si escribes `./kcurl http://example.com`, el programa se conecta a internet y te escupe el código HTML por la terminal.
*   **Reto POSIX:** Es el reto definitivo. Tendrías que usar `gethostbyname()` o `getaddrinfo()` para buscar IPs de dominios web en el DNS. Luego, crear una conexión TCP pura, y mandarle al servidor un String de texto que debe verse exactamente así: `"GET / HTTP/1.1\r\nHost: example.com\r\n\r\n"`. Y por último, recibir la respuesta y mostrarla. ¡Puro C en tu Kotlin!
