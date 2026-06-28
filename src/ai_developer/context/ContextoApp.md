# Contexto del Proyecto: Android App con Clean Architecture

Este archivo define el contexto y la estructura del proyecto actual para que la IA actúe como un arquitecto y desarrollador experto en Android, aplicando principios de Clean Architecture y SOLID.

## 📌 Información General
- **Lenguaje Principal:** Kotlin
- **Arquitectura:** Clean Architecture + MVVM (Model-View-ViewModel)
- **UI Framework:** Jetpack Compose (Declarativo)
- **Gestor de Dependencias:** Hilt (Dependency Injection)
- **Asincronía:** Kotlin Coroutines & Flow

---

## 📂 Estructura del Módulo de Características (Feature Architecture)

El proyecto está dividido por características (features). Cada característica se auto-contiene en tres capas estrictas separadas:

### 1. Capa de Presentación (Presentation Layer)
*Depende únicamente de la capa de Domain.*
- **UI (Jetpack Compose):** Vistas declarativas puras, sin lógica de negocio. Reaccionan al estado expuesto por el ViewModel.
- **ViewModel:** Retiene el estado de la pantalla (`StateFlow`) y procesa los eventos del usuario llamando a los Casos de Uso (`UseCases`).

### 2. Capa de Dominio (Domain Layer)
*Capa central pura. No depende de ninguna otra capa (aislada de frameworks, librerías o Android).*
- **Models:** Clases de datos del negocio (ej. `User`, `Product`).
- **Use Cases (Interactors):** Representan una acción única del negocio (ej. `GetProductUseCase`, `LoginWithEmailUseCase`).
- **Repository Interfaces:** Definición estricta del contrato de datos. La implementación real se delega a la capa Data (Inversión de Dependencias).

### 3. Capa de Datos (Data Layer)
*Depende de la capa de Domain para implementar sus interfaces.*
- **Repositories Implementations:** Orquestan el flujo de datos decidiendo si buscar en la caché local o en la API remota.
- **Data Sources:** - **Remote:** Cliente HTTP (Retrofit/Ktor) para consumir APIs.
  - **Local:** Base de datos Room o DataStore para persistencia local.
- **Mappers:** Funciones de extensión para transformar `DataDTO` (red/bBDD) a `DomainModel` y viceversa, manteniendo el desacoplamiento.

---

## 🛠️ Guía de Estilo y Reglas del Código
1. **Flujo de Datos Unidireccional (UDF):** El ViewModel expone un único `ViewState`, la vista envía `ViewEvent` al ViewModel.
2. **Inversión de Dependencias:** Las clases dependen de interfaces, nunca de implementaciones concretas. Todo se inyecta mediante `@Inject` con Hilt.
3. **Manejo de Errores:** No se propagan excepciones (`try-catch`) hacia la UI. Se utiliza un envoltorio de resultado como `Result<T>` o una clase sellada (`Resource<T>`).
