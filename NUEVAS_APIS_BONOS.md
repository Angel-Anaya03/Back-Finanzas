# Nuevas APIs para Gestión de Bonos

## Resumen
Se han agregado 3 nuevas APIs para facilitar la modificación de bonos y la regeneración automática de flujos de caja:

1. **Actualizar TEA** - Modifica solo la Tasa Efectiva Anual
2. **Actualizar Valor Nominal** - Modifica solo el Valor Nominal  
3. **Flujo Completo** - Obtiene el flujo actualizado de un bono

## 1. API para Actualizar TEA (Tasa Efectiva Anual)

### Endpoint
```
PUT /api/bonos/{idBono}/actualizar-tea
```

### Descripción
Actualiza únicamente la TEA (porcentajeTasa) de un bono específico y regenera automáticamente todos sus flujos de caja.

### Parámetros
- **idBono** (path): ID del bono a actualizar
- **Body** (JSON):
```json
{
  "tea": 5.50,
  "teaAnterior": 4.00  // Opcional, para referencia
}
```

### Validaciones
- TEA debe estar entre 0% y 9%
- El campo 'tea' es obligatorio

### Ejemplo de Uso
```bash
curl -X PUT http://localhost:8080/api/bonos/1/actualizar-tea \
-H "Content-Type: application/json" \
-d '{
  "tea": 5.50,
  "teaAnterior": 4.00
}'
```

### Respuesta Exitosa
```json
{
  "mensaje": "TEA actualizada exitosamente y flujos de caja regenerados",
  "teaAnterior": 4.00,
  "teaNueva": 5.50,
  "bono": {
    "idBono": 1,
    "nombreBono": "Bono Ejemplo",
    "porcentajeTasa": 5.50,
    // ... otros campos
  },
  "totalPeriodos": 10,
  "flujos": [
    // ... flujos de caja actualizados
  ]
}
```

## 2. API para Actualizar Valor Nominal

### Endpoint
```
PUT /api/bonos/{idBono}/actualizar-valor-nominal
```

### Descripción
Actualiza únicamente el valor nominal de un bono específico y regenera automáticamente todos sus flujos de caja.

### Parámetros
- **idBono** (path): ID del bono a actualizar
- **Body** (JSON):
```json
{
  "valorNominal": 3500.00,
  "valorAnterior": 3000.00  // Opcional, para referencia
}
```

### Validaciones
- Valor nominal debe estar entre 1,000 y 6,000
- El campo 'valorNominal' es obligatorio

### Ejemplo de Uso
```bash
curl -X PUT http://localhost:8080/api/bonos/1/actualizar-valor-nominal \
-H "Content-Type: application/json" \
-d '{
  "valorNominal": 3500.00,
  "valorAnterior": 3000.00
}'
```

### Respuesta Exitosa
```json
{
  "mensaje": "Valor nominal actualizado exitosamente y flujos de caja regenerados",
  "valorAnterior": 3000.00,
  "valorNuevo": 3500.00,
  "bono": {
    "idBono": 1,
    "nombreBono": "Bono Ejemplo",
    "valorNominal": 3500.00,
    // ... otros campos
  },
  "totalPeriodos": 10,
  "flujos": [
    // ... flujos de caja actualizados
  ]
}
```

## 3. API para Obtener Flujo Completo

### Endpoint
```
GET /api/bonos/{idBono}/flujo-completo
```

### Descripción
Obtiene el flujo de caja completo y actualizado de un bono. Si no existen flujos, los genera automáticamente.

### Parámetros
- **idBono** (path): ID del bono

### Ejemplo de Uso
```bash
curl -X GET http://localhost:8080/api/bonos/1/flujo-completo
```

### Respuesta Exitosa
```json
{
  "bono": {
    "id": 1,
    "nombre": "Bono Ejemplo",
    "valorNominal": 3500.00,
    "tea": 5.50,
    "plazo": 5,
    "frecuenciaPago": "Semestral"
  },
  "resumen": {
    "totalPeriodos": 10,
    "fechaUltimaActualizacion": "2024-01-15T10:30:00Z"
  },
  "flujosCaja": [
    {
      "nroPeriodo": 1,
      "tasaCupon": 50.0000000,
      "amortizacion": 350.00,
      "cavali": 5.00,
      "valorComercial": 3500.00,
      "primaRedencion": 0.00
    },
    // ... más flujos
  ]
}
```

## Características Importantes

### Regeneración Automática
- Todas las APIs regeneran automáticamente los flujos de caja
- Los flujos anteriores se eliminan y se crean nuevos
- Los cálculos se basan en la nueva TEA o valor nominal

### Validaciones
- **TEA**: Entre 0% y 9%
- **Valor Nominal**: Entre 1,000 y 6,000
- **Bono**: Debe existir en la base de datos

### Manejo de Errores
- Validación de campos obligatorios
- Validación de rangos permitidos
- Verificación de existencia del bono
- Manejo de errores internos del servidor

## Endpoints Existentes Relacionados

### APIs ya existentes que también regeneran flujos:
- `PUT /api/bonos/{idBono}/actualizar-con-flujos` - Actualiza todo el bono
- `POST /api/bonos/crear-con-flujos` - Crea bono con flujos
- `POST /api/bonos/{idBono}/generar-flujos-caja` - Solo genera flujos

### APIs para consultar flujos:
- `GET /api/bonos/{idBono}/flujos-caja` - Obtiene flujos guardados
- `GET /api/bonos/{idBono}/flujo-caja` - Genera flujos en tiempo real

## Casos de Uso Típicos

### 1. Cambiar TEA por condiciones de mercado
```bash
# Ejemplo: Cambiar TEA de 4% a 5.5%
curl -X PUT http://localhost:8080/api/bonos/1/actualizar-tea \
-H "Content-Type: application/json" \
-d '{"tea": 5.50}'
```

### 2. Ajustar valor nominal por revaluación
```bash
# Ejemplo: Aumentar valor nominal de 3000 a 3500
curl -X PUT http://localhost:8080/api/bonos/1/actualizar-valor-nominal \
-H "Content-Type: application/json" \
-d '{"valorNominal": 3500.00}'
```

### 3. Verificar flujos después de cambios
```bash
# Obtener flujo completo actualizado
curl -X GET http://localhost:8080/api/bonos/1/flujo-completo
```

## Notas Técnicas

1. **Transaccionalidad**: Cada actualización se hace en una transacción
2. **Cascada**: Los flujos se regeneran automáticamente 
3. **Valor Comercial**: Al actualizar valor nominal, se actualiza automáticamente si es igual al nominal
4. **Fecha de Actualización**: Se registra automáticamente en las respuestas

