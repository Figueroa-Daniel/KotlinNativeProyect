# KTree (`ktree`)

Utilidad súper cómoda basada en el comando UNIX predeterminado `tree`. Genera una representación jerárquica ASCII en forma de caja, navegando por todo el directorio.

## ⚖️ `ktree` vs `tree` clásico

Aunque `ktree` se diseñó para imitar la visualización jerárquica exacta de la herramienta `tree` tradicional de Linux, existen grandes diferencias en la experiencia y su tecnología:

| Característica | GNU `tree` | `ktree` (Kotlin Native) |
| :--- | :--- | :--- |
| **Interactividad** | ❌ Texto plano y muerto | ✅ **Hipervínculos** (Haz *Ctrl+Click* en la línea conectada de cualquier archivo para abrirlo al instante en tu IDE) |
| **Colorimetría** | ⚠️ Requiere flags extra (`-C`) | ✅ Automático (Carpetas pintadas en azul ANSI constante para identificarlas a simple vista) |
| **Filosofía** | ⚙️ Herramienta monstruosa con demasiadas banderas en su manual UNIX | 🚀 Minimalista y orientada 100% a la navegación en editores modernos de desarrollo |
| **Motor** | 📉 Puramente en C clásico | 🛡️ Kotlin Native fuertemente tipado conectado al recolector nativo POSIX usando memoria heap segura |

## 🛠️ Cómo Funciona

Sigue un explorador iterativo nativo. Almacena silenciosamente en memoria RAM todas las sub-ramas de la carpeta matriz actual para obtener algo crucial: saber **cuál es el último elemento de todos**. Con esa información decide entre dibujar en tu terminal un caño de conexión intermedio (`├──`) o sellar y terminar la forma gráfica del árbol para ese hijo (`└──`). 

A los nietos subyacentes se les transmite el cálculo lógico para definir cómo tienen que pintar su propio tupo vertical (`│`).

### Uso básico
```bash
./ktree.kexe [ruta_opcional_a_imprimir]
```

## ⚙️ Compilación

```bash
cd src
kotlinc-native ktree.kt -o ktree
./ktree.kexe
```
