# KLs (`kls`)

Utilidad CLI basada en la herramienta `ls` de Unix creada mediante interacciones entre Kotlin y la memoria POSIX de C.

## 🛠️ Cómo Funciona

`kls.kt` se encarga de escanear la carpeta actual y de imprimir por pantalla los archivos e identificarlos por colores dependiendo de si la entidad se trata de un archivo ordinario o de un directorio con permisos.

También, emplea **Secuencias de Escape OSC 8** de Terminal para que todos los archivos dibujados por el comando posean hipervínculos interactivos funcionales donde el usuario puede pinchar.

### Uso básico
```bash
./kls.kexe [ruta_inicial_opcional]
```

## ⚙️ Compilación

```bash
kotlinc-native kls.kt -o kls
./kls.kexe
```
