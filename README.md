# Presupuesto

Aplicación Android para crear presupuestos y generarlos en PDF para enviarlos a los clientes.

## Descripción

La app permite armar un presupuesto con los productos o servicios, sus cantidades y precios, y exportarlo como archivo PDF para compartirlo por cualquier medio (correo, WhatsApp, etc.).

El formato de referencia de los presupuestos incluye:

- Código, descripción, cantidad, precio unitario e importe por ítem.
- Total con IVA incluido.
- Condición de venta (por ejemplo, Cuenta Corriente).
- Datos del emisor en el encabezado.

## Requisitos

- Android Studio (versión reciente con soporte para Kotlin DSL de Gradle).
- JDK 17 o superior (confirmar la versión exigida por `build.gradle.kts`).
- Un dispositivo o emulador Android (confirmar `minSdk` en `app/build.gradle.kts`).

## Instalación

1. Clonar el repositorio:

   ```bash
   git clone https://github.com/gastonnicora/Presupuesto.git
   cd Presupuesto
   ```

2. Abrir la carpeta del proyecto en Android Studio.
3. Esperar a que Gradle sincronice las dependencias.
4. Ejecutar la app desde **Run** en un emulador o dispositivo conectado.

También se puede compilar desde terminal:

```bash
./gradlew assembleDebug
```

El APK de depuración queda en `app/build/outputs/apk/debug/`.

## Uso

1. Crear un nuevo presupuesto.
2. Cargar los ítems con código, descripción, cantidad y precio unitario.
3. Revisar el total con IVA incluido.
4. Generar el PDF y compartirlo.

## Estructura del proyecto

```
Presupuesto/
├── app/            Código fuente de la aplicación Android
├── gradle/         Wrapper de Gradle
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── gradlew / gradlew.bat
```

Paquete principal: `com.gastonnicora.presupuesto`.

## Estado

Proyecto en desarrollo.

## Autor

Gastón Nicora ([@gastonnicora](https://github.com/gastonnicora))

## Licencia

Pendiente de definir.
