# Calories Resume

Aplicación Android para análisis nutricional de comidas mediante IA.

## Características

- Captura de fotos de comida desde la cámara o galería
- Análisis nutricional mediante Spoonacular API
- Almacenamiento local de historial de comidas
- Reportes diarios, semanales y mensuales
- Consejos dietéticos personalizados mediante OpenAI GPT-3.5-turbo

## Arquitectura

El proyecto sigue **Clean Architecture** con las siguientes capas:

- **Domain**: Casos de uso, modelos de dominio y repositorios (interfaces)
- **Data**: Implementación de repositorios, fuentes de datos locales y remotas
- **Presentation**: ViewModels, UI con Jetpack Compose

## Configuración

1. Clona el repositorio
2. Crea un archivo `local.properties` en la raíz del proyecto con tus API keys:

```
SPOONACULAR_API_KEY=tu_api_key_aqui
OPENAI_API_KEY=tu_api_key_aqui
```

3. Obtén tus API keys:
   - **Spoonacular**: Regístrate en [RapidAPI](https://rapidapi.com/spoonacular/api/spoonacular-recipe-food-nutrition-v1) (plan gratuito: 150 requests/día)
   - **OpenAI**: Regístrate en [OpenAI](https://platform.openai.com/) y obtén tu API key

4. Abre el proyecto en Android Studio y sincroniza Gradle
5. Ejecuta la aplicación

## Testing

El proyecto incluye tests unitarios. Para ejecutar los tests:

```bash
./gradlew test
```

## Tecnologías

- Kotlin
- Jetpack Compose
- Room Database
- Retrofit
- Hilt (Dependency Injection)
- CameraX
- Coroutines & Flow
- JUnit, Mockito, Turbine (Testing)

## Estructura del Proyecto

```
app/
├── data/
│   ├── local/database/     # Room entities y DAOs
│   ├── remote/api/         # Interfaces Retrofit
│   ├── mapper/             # Mappers entre capas
│   └── repository/         # Implementación de repositorios
├── domain/
│   ├── model/              # Modelos de dominio
│   ├── repository/         # Interfaces de repositorios
│   └── usecase/            # Casos de uso
├── ui/
│   ├── camera/             # Pantalla de cámara
│   ├── analysis/           # Pantalla de análisis
│   ├── history/            # Historial de comidas
│   ├── reports/            # Reportes nutricionales
│   └── advice/             # Consejos dietéticos
└── di/                     # Módulos de inyección de dependencias
```

## Uso

1. Abre la aplicación
2. Toma una foto de tu comida o selecciona una de la galería
3. Espera a que se analice la imagen
4. Revisa la información nutricional
5. Guarda la entrada para agregarla a tu historial
6. Consulta reportes diarios, semanales o mensuales
7. Obtén consejos dietéticos personalizados basados en tu consumo

## Permisos

La aplicación requiere los siguientes permisos:
- Cámara (para capturar fotos)
- Lectura de imágenes (para seleccionar de la galería)

## Licencia

Este proyecto es de código abierto.
