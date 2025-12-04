# Solución al Error de Gradle

## Error
```
A problem occurred configuring project ':app'.
> Failed to notify project evaluation listener.
   > 'org.gradle.api.file.FileCollection org.gradle.api.artifacts.Configuration.fileCollection(org.gradle.api.specs.Spec)'
```

## Soluciones Aplicadas

1. **Versiones de plugins ajustadas**:
   - AGP: 8.3.2 (versión estable)
   - Kotlin: 1.9.22
   - Compose Compiler: 1.5.4

2. **Configuración de kapt**:
   - `useBuildCache = false` para evitar conflictos
   - Opciones de javac configuradas

3. **Propiedades de Gradle**:
   - `kapt.use.worker.api=false`
   - `kapt.incremental.apt=false`

## Si el error persiste

### Opción 1: Limpiar caché de Gradle
1. En Android Studio: File → Invalidate Caches / Restart
2. Selecciona "Invalidate and Restart"
3. Espera a que se reinicie y sincroniza de nuevo

### Opción 2: Verificar versión de Gradle
1. File → Project Structure → Project
2. Verifica la versión de Gradle
3. Si es 9.x o superior, cámbiala a 8.5 o 8.6

### Opción 3: Limpiar manualmente
Elimina estas carpetas y sincroniza:
- `.gradle/` (en el directorio del proyecto)
- `build/` (en app/)
- `app/build/`

### Opción 4: Crear wrapper de Gradle
Si no existe `gradle/wrapper/gradle-wrapper.properties`, créalo con:
```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```


