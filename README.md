# Calories Resume

Aplicación Android para análisis nutricional de comidas mediante IA.

## Características

- Captura de fotos de comida desde la cámara o galería
- Análisis nutricional mediante LogMeal API
- Segmentación de alimentos con selección de platos candidatos
- Información nutricional detallada (calorías, macronutrientes, micronutrientes)
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
LOGMEAL_API_KEY=tu_api_key_aqui
OPENAI_API_KEY=tu_api_key_aqui
```

3. Obtén tus API keys:
   - **LogMeal**: Regístrate en [LogMeal](https://www.logmeal.es/) y obtén tu API key desde el dashboard
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
│   ├── splash/             # Pantalla de inicio
│   ├── home/               # Pantalla principal
│   ├── camera/             # Pantalla de cámara
│   ├── analysis/           # Pantalla de análisis y selección de platos
│   ├── history/            # Historial de comidas
│   ├── reports/            # Reportes nutricionales
│   └── advice/             # Consejos dietéticos
└── di/                     # Módulos de inyección de dependencias
```

## Uso

1. Abre la aplicación
2. Desde la pantalla principal, selecciona "Cámara" para tomar una foto o "Galería" para seleccionar una imagen
3. La aplicación segmentará la imagen y mostrará los platos candidatos detectados
4. Selecciona el plato que deseas analizar
5. Espera a que se obtenga la información nutricional detallada
6. Revisa los datos nutricionales (calorías, proteínas, carbohidratos, grasas, vitaminas, minerales, etc.)
7. Guarda la entrada para agregarla a tu historial
8. Consulta reportes diarios, semanales o mensuales
9. Obtén consejos dietéticos personalizados y planes de dieta semanales basados en tu consumo

## Permisos

La aplicación requiere los siguientes permisos:
- Cámara (para capturar fotos)
- Lectura de imágenes (para seleccionar de la galería)

## Licencia

Este proyecto es de código abierto.
