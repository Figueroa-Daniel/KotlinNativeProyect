# KFind (`kfind`)

Una herramienta de terminal de alto rendimiento escrita en Kotlin/Native que imita y simplifica el uso del comando `find` en sistemas UNIX mediante la API POSIX subyacente.

## 🛠️ Cómo Funciona

El programa parte desde un directorio base y analiza recursivamente todos sus subdirectorios en la búsqueda temporal de un nombre especificado mediante la línea de comandos. 

### Uso básico
```bash
./kfind.kexe <nombre_a_buscar> [ruta_inicial]
```

### Opciones
Admite la inclusión de modificadores dinámicos que se aplican con un guion.
- `-s` : Activa la búsqueda en archivos ocultos (los archivos ocultos de UNIX empiezan por `.`). Esto consume más tiempo ya que accederá a dependencias de NPM, `.git`, etc.
- `-N` (Siendo N un número) : Limita la profundidad de iteraciones para dar una búsqueda más veloz y concisa en el árbol actual de carpetas.

## ⚙️ Compilación

Asegúrate de tener el compilador nativo (Konanc):

```bash
kotlinc-native kfind.kt -o kfind
./kfind.kexe archivo_prueba.txt
```
