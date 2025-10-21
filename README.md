### Main Branch

[![Build Main](https://github.com/algo3-unsam/algo-que-pedir-backend-2025-grupo7/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/algo3-unsam/algo-que-pedir-backend-2025-grupo7/actions/workflows/build.yml)
[![Coverage Main](https://raw.githubusercontent.com/algo3-unsam/algo-que-pedir-backend-2025-grupo7/main/.github/badges/jacoco.svg)](https://github.com/algo3-unsam/algo-que-pedir-backend-2025-grupo7/main/.github/badges/jacoco.svg)

### Development Branch

[![Build Dev](https://github.com/algo3-unsam/algo-que-pedir-backend-2025-grupo7/actions/workflows/build.yml/badge.svg?branch=Development)](https://github.com/algo3-unsam/algo-que-pedir-backend-2025-grupo7/actions/workflows/build.yml)
[![Coverage Dev](https://raw.githubusercontent.com/algo3-unsam/algo-que-pedir-backend-2025-grupo7/Development/.github/badges/jacoco.svg)](https://github.com/algo3-unsam/algo-que-pedir-backend-2025-grupo7/Development/.github/badges/jacoco.svg)

# Algoritmos III - Trabajo Práctico 2025 - Backend

## Descripción del proyecto
Este proyecto es el backend del TP "Algo Que Pedir" de la materia Algoritmos III (2025), desarrollado en **Spring Boot** utilizando Kotlin. Se basa en el backend de Algoritmos II y sirve como API REST para la aplicación frontend desarrollada en Svelte.

El backend implementa el dominio general del TP y expone endpoints para:

- Gestión de usuarios (registro, login)
- Gestión de locales
- Gestión de ingredientes
- Gestión de platos
- Gestión de pedidos (orders)

Incluye validaciones, manejo de errores y pruebas automatizadas (unitarias e integración).

## Estructura del proyecto

- `src/main/kotlin` → Código fuente del backend (controllers, services, domain, dto, mapper)
- `src/test/kotlin` → Tests unitarios e integraciones
- `build.gradle.kts` → Dependencias, plugins y configuración de Gradle
- `.github/workflows/build.yml` → Configuración de CI con GitHub Actions
- `.github/badges/` → Badges generados por JaCoCo para coverage

## Requisitos

- JDK 21
- Gradle
- Spring Boot
- Kotlin

## Endpoints principales

### Autenticación
- `POST /v1/auth/register` → Crear un usuario
- `GET /v1/auth/login` → Loguear usuario

### Local
- `GET /v1/local/{id}` → Obtener info del local
- `PUT /v1/local/{id}` → Actualizar info del local

### Ingredientes
- `GET /v1/ingredients` → Listar todos los ingredientes
- `GET /v1/ingredients/{id}` → Obtener un ingrediente
- `POST /v1/ingredients` → Crear ingrediente
- `PUT /v1/ingredients/{id}` → Actualizar ingrediente
- `DELETE /v1/ingredients/{id}` → Borrar ingrediente

### Platos
- `GET /v1/dishes` → Listar todos los platos
- `GET /v1/dishes/{id}` → Obtener un plato
- `POST /v1/dishes` → Crear plato
- `PUT /v1/dishes/{id}` → Actualizar plato
- `DELETE /v1/dishes/{id}` → Borrar plato

### Pedidos (Orders)
- `GET /v1/order` → Listar pedidos por estado o todos
- `GET /v1/order/orderDetail/{orderId}` → Obtener detalle de un pedido

## Testing

- Se incluyen tests unitarios de **services** y **controllers**
- Se incluyen tests de integración para endpoints
- Cobertura de tests generada automáticamente por JaCoCo y subida como badge

## Integración Continua

- Configurada con **GitHub Actions**
- Workflow ejecuta tests y genera coverage automáticamente
- Badges de build y coverage disponibles para **main** y **development**

## Cómo correr el proyecto

1. Clonar el repositorio
2. Configurar JDK 21
3. Ejecutar desde terminal:

```bash
./gradlew bootRun
```

4. Los tests se ejecutan con:

```bash
./gradlew test
```

5. Los reports de cobertura se generan en `build/reports/jacoco/`

## Notas adicionales

* Se respeta la lógica de descuento de platos según el TP de maquetado:

    * Plato nuevo → descuento del 30% disminuyendo 1% por día hasta el día 20, luego 10%
    * Plato en promo → descuento del porcentaje establecido
    * Los descuentos no se solapan

* Validaciones implementadas en backend para:

    * Platos y sus ingredientes
    * Medios de pago
    * Estados de pedidos

* Repositorios usados:

    * Backend: este repositorio
    * Frontend: repositorio de Maquetado/Svelte del TP

