# Cambios locales conflictivos (2026-10-18)

## Por qué existe esta rama
La rama local `Development` tenía 1 commit sin pushear (`debdb3e Agrego TODO's`) y estaba 112 commits atrás de
`origin/Development`. Al intentar integrar, hubo conflicto de contenido en:
- `src/main/kotlin/ar/edu/unsam/algo3/controller/IngredientController.kt`
- `src/main/kotlin/ar/edu/unsam/algo3/controller/OrderController.kt`

Para no perder el commit ni pisar el remoto, se guardó acá y `Development` local se alineó con `origin/Development`.

## Base
Divergió de `origin/Development` en `93d16a6`.

## Qué contiene
- `debdb3e Agrego TODO's`: comentarios TODO agregados en DishController, IngredientController, LocalController,
  OrderController y DishControllerTest.

## Qué hacer
Revisar si los TODO siguen vigentes contra el código actual de `Development`; si sí, reaplicarlos a mano o
hacer merge resolviendo conflictos. Si no, borrar la rama.
