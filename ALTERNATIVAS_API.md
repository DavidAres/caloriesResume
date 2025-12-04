# Alternativas a Spoonacular para Análisis de Imágenes de Comida

## Opciones Recomendadas

### 1. **LogMeal API** (Recomendada)
- **URL**: https://www.logmeal.es/
- **Plan Gratuito**: Sí, con límites
- **Endpoint**: `https://api.logmeal.es/v2/image/segmentation/complete`
- **Autenticación**: Header `Authorization: Bearer {token}`
- **Ventajas**: 
  - Específicamente diseñada para reconocimiento de alimentos
  - Desarrollada en la Universidad de Barcelona
  - Soporta análisis de imágenes directamente
  - Proporciona información nutricional
- **Desventajas**: Puede tener límites en el plan gratuito

### 2. **Nutritionix API**
- **URL**: https://www.nutritionix.com/business/api
- **Plan Gratuito**: Sí (100 requests/día)
- **Endpoint**: `https://trackapi.nutritionix.com/v2/natural/nutrients`
- **Autenticación**: Headers `x-app-id` y `x-app-key`
- **Ventajas**: 
  - Buena documentación
  - Plan gratuito generoso
  - Base de datos extensa
- **Desventajas**: Puede requerir texto descriptivo en lugar de solo imagen

### 3. **Edamam Food Database API**
- **URL**: https://www.edamam.com/
- **Plan Gratuito**: Sí (5,000 requests/mes)
- **Ventajas**: 
  - Base de datos grande (900,000+ alimentos)
  - Plan gratuito generoso
- **Desventajas**: Puede no analizar imágenes directamente, requiere identificación previa

## Recomendación

**Usar LogMeal** porque:
1. Está específicamente diseñada para análisis de imágenes de comida
2. Proporciona información nutricional completa
3. Tiene un plan gratuito disponible
4. Es más directa para nuestro caso de uso

## Pasos para implementar LogMeal:

1. Registrarse en https://www.logmeal.es/
2. Obtener el token de API
3. Actualizar `local.properties` con `LOGMEAL_API_KEY=tu_token`
4. El código ya está preparado para usar LogMeal


