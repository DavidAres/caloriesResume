# Explicación del Error de Gradle

## ¿Qué está pasando?

El error que ves significa:

```
Failed to notify project evaluation listener.
> 'org.gradle.api.file.FileCollection org.gradle.api.artifacts.Configuration.fileCollection(org.gradle.api.specs.Spec)'
```

### Traducción simple:

1. **"Failed to notify project evaluation listener"**: 
   - Gradle está intentando configurar tu proyecto
   - Hay un "listener" (un plugin o extensión) que está escuchando este proceso
   - Ese listener está fallando

2. **"fileCollection(Spec)"**:
   - Este es un método de la API de Gradle
   - Este método fue **deprecado** (marcado como obsoleto) y luego **eliminado** en versiones recientes de Gradle
   - Algún plugin en tu proyecto (probablemente **kapt**, **Hilt** o **Room**) todavía está intentando usar este método antiguo

### ¿Por qué pasa esto?

- **kapt** (Kotlin Annotation Processing Tool) es un plugin antiguo
- En versiones nuevas de Gradle (9.x o 10.x), este método ya no existe
- Los plugins que usan kapt (como Hilt y Room) intentan llamar a este método y fallan

### Analogía simple:

Imagina que tienes un coche viejo (kapt) que necesita una herramienta específica (fileCollection) que ya no se fabrica (fue eliminada de Gradle). El coche intenta usar la herramienta, pero no la encuentra, y se rompe.

## Soluciones

### ✅ Solución Aplicada: Migración a KSP

Se ha migrado completamente de **kapt** a **KSP** (Kotlin Symbol Processing).

**Cambios realizados:**
1. ✅ Reemplazado `kotlin-kapt` por `com.google.devtools.ksp`
2. ✅ Cambiadas todas las dependencias `kapt()` a `ksp()`
3. ✅ Actualizada la configuración de kapt a ksp
4. ✅ Eliminadas propiedades de kapt de gradle.properties

**Ventajas de KSP:**
- ✅ Más rápido que kapt (hasta 2x)
- ✅ Compatible con versiones modernas de Gradle
- ✅ No usa APIs deprecadas
- ✅ Mejor soporte para Kotlin multiplataforma
- ✅ Es el futuro recomendado por Google y JetBrains

**Próximos pasos:**
1. Sincroniza el proyecto: File → Sync Project with Gradle Files
2. El error debería desaparecer ahora

