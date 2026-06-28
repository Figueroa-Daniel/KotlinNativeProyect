Raíz_del_Proyecto/
│
├── buildSrc/                           # 🛠️ Gestión centralizada de dependencias y versiones (Kotlin DSL)
│   └── src/main/java/Dependencies.kt
│
├── app/                                # 🚀 MÓDULO CONFIGURADOR (Acondiciona Hilt, Firebase, inicializadores)
│   ├── src/main/java/com/app/
│   │   ├── ArchitectureApplication.kt  # Punto de entrada de la App
│   │   └── di/                         # Ensamblaje global de grafos de Hilt
│   └── build.gradle.kts
│
├── features/                           # 🎨 COMPONENTES VISUALES Y DE NEGOCIO (Modularizado por Feature)
│   │
│   ├── auth/                           # Modulo de Autenticación
│   │   ├── data/                       # Capa Data local de la Feature
│   │   ├── domain/                     # Capa Domain local de la Feature
│   │   └── presentation/               # Capa Presentation (Compose Screens del Login/Registro)
│   │
│   └── checkout/                       # Módulo de Pagos / Carrito
│       ├── data/
│       ├── domain/
│       └── presentation/
│
└── core/                               # 🏗️ LIBRERÍAS DE INFRAESTRUCTURA (Compartidas por los módulos de Feature)
    │
    ├── network/                        # Cliente HTTP Centralizado (Ktor/Retrofit)
    │   ├── src/main/java/com/core/network/
    │   │   ├── HttpClientFactory.kt    # Configuración de SSL, Timeouts y Loggers
    │   │   └── model/
    │   │       └── NetworkResponse.kt  # Envoltorio genérico para llamadas API
    │   └── build.gradle.kts
    │
    ├── database/                       # Capa de Persistencia Global (Room)
    │   ├── src/main/java/com/core/database/
    │   │   ├── AppDatabase.kt          # Declaración de la base de datos Room
    │   │   └── EncryptionManager.kt    # Cifrado de base de datos en SQLCipher
    │   └── build.gradle.kts
    │
    └── designsystem/                   # 🎨 SISTEMA DE DISEÑO COMPARTIDO (UI Atómica)
        ├── src/main/java/com/core/designsystem/
        │   ├── theme/                  # Colores, Tipografías y Formas (Tokens de UI)
        │   └── components/             # Botones, Spinners y Tarjetas Custom reutilizables
        └── build.gradle.kts