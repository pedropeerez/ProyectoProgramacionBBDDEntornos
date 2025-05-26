# Proyecto de Gestión de Ventas - PixelDB

Este proyecto consiste en una aplicación de escritorio desarrollada en Java, diseñada para gestionar un proceso de venta sencillo de productos. Incluye funcionalidades como la selección de productos, visualización de carrito, recogida de datos del cliente y confirmación de la compra.

## Características principales

- Interfaz gráfica intuitiva creada con **Java Swing**.
- Gestión de productos y carrito mediante la clase `Cesta`.
- Validación de datos introducidos por el usuario.
- Optimización del código mediante refactorización.
- Pruebas unitarias implementadas con **JUnit 5**.

## Tecnologías utilizadas

- Java 8+
- Swing (GUI)
- JUnit 5 (pruebas unitarias)
- IDE: Eclipse + WindowBuilder

## Pruebas unitarias

Se han desarrollado varias pruebas unitarias para asegurar la funcionalidad del sistema. Algunas de las pruebas implementadas:

- **CestaTest**: Verifica la correcta gestión del carrito de compra (añadir, eliminar, vaciar y calcular precio total).
- **LoginTest**: Simula el inicio de sesión y evalúa el comportamiento esperado ante credenciales válidas e inválidas.
- **VentaTest**: Asegura que las pantallas del proceso de venta se muestran correctamente sin lanzar errores.
- **GetModeloTest**: Comprueba que la obtención de modelos a partir del ID del producto funciona correctamente para entradas inválidas.
- **OpcionesVentaTest**: Evalúa que el método `mostrarOpcionesVenta()` no lanza excepciones.

## Refactorización

Como parte del trabajo, se ha llevado a cabo una **refactorización del código**:
- Se creó la clase `Cesta` para manejar la lógica del carrito de forma independiente, facilitando la reutilización y las pruebas.
- Se optimizaron métodos en la clase `Principal`, eliminando redundancias y dividiendo métodos complejos en otros más simples y legibles.
- Mejora en la organización del código y separación de responsabilidades.

## Estructura del proyecto
- Principal.java
- Principal.form
- ConexionBBDD.java
- Cesta.java
- Tests
