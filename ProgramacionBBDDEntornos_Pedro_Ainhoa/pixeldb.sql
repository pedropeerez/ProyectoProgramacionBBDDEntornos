-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 26-05-2025 a las 14:58:37
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `pixeldb`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `almacen`
--

CREATE TABLE `almacen` (
  `id_producto` int(11) NOT NULL,
  `stock_disponible` int(11) NOT NULL DEFAULT 0,
  `color` varchar(20) DEFAULT NULL,
  `variante` varchar(30) DEFAULT NULL,
  `modelo` varchar(50) DEFAULT NULL,
  `precio` decimal(10,2) NOT NULL DEFAULT 0.00 CHECK (`precio` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `almacen`
--

INSERT INTO `almacen` (`id_producto`, `stock_disponible`, `color`, `variante`, `modelo`, `precio`) VALUES
(1024, 3, NULL, NULL, 'PixelDB 1_8GB RAM/64GB ROM', 599.99),
(1025, 10, NULL, NULL, 'PixelDB 1_12GB RAM/128GB ROM', 799.99),
(1026, 4, NULL, NULL, 'PixelDB 1_16GB RAM/256GB ROM', 499.99),
(1028, 8, NULL, NULL, 'PixelDB 1 Lite_8GB RAM/64GB ROM', 299.99),
(1029, 0, NULL, NULL, 'PixelDB 1 Pro', 699.99),
(1030, 3, NULL, NULL, 'PixelDB 1 Pro_8GB RAM/64GB ROM', 699.99);

--
-- Disparadores `almacen`
--
DELIMITER $$
CREATE TRIGGER `trigger_almacen_delete` AFTER DELETE ON `almacen` FOR EACH ROW BEGIN
  INSERT INTO logs (
    tabla_afectada, accion, id_registro, datos_anteriores, datos_nuevos, usuario, fecha_hora
  )
  VALUES (
    'almacen',
    'DELETE',
    OLD.id_producto,
    JSON_OBJECT(
      'id_producto', OLD.id_producto,
      'stock_disponible', OLD.stock_disponible,
      'color', OLD.color,
      'variante', OLD.variante,
      'modelo', OLD.modelo,
      'precio', OLD.precio
    ),
    NULL,
    NULL,
    NOW()
  );
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trigger_almacen_insert` AFTER INSERT ON `almacen` FOR EACH ROW BEGIN
  INSERT INTO logs (
    tabla_afectada, accion, id_registro, datos_anteriores, datos_nuevos, usuario, fecha_hora
  )
  VALUES (
    'almacen',
    'INSERT',
    NEW.id_producto,
    NULL,
    JSON_OBJECT(
      'id_producto', NEW.id_producto,
      'stock_disponible', NEW.stock_disponible,
      'color', NEW.color,
      'variante', NEW.variante,
      'modelo', NEW.modelo,
      'precio', NEW.precio
    ),
    NULL,
    NOW()
  );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `logs`
--

CREATE TABLE `logs` (
  `id_log` int(11) NOT NULL,
  `tabla_afectada` varchar(50) NOT NULL,
  `accion` varchar(10) NOT NULL,
  `id_registro` int(11) NOT NULL,
  `datos_anteriores` text DEFAULT NULL,
  `datos_nuevos` text DEFAULT NULL,
  `usuario` varchar(50) DEFAULT NULL,
  `fecha_hora` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `logs`
--

INSERT INTO `logs` (`id_log`, `tabla_afectada`, `accion`, `id_registro`, `datos_anteriores`, `datos_nuevos`, `usuario`, `fecha_hora`) VALUES
(10, 'almacen', 'DELETE', 101, '{\"id_producto\": 101, \"stock_disponible\": 50, \"color\": \"Azul\", \"variante\": \"L\", \"modelo\": \"Camiseta deportiva\", \"precio\": 19.99}', NULL, NULL, '2025-05-25 19:50:15'),
(11, 'almacen', 'INSERT', 1002, NULL, '{\"id_producto\": 1002, \"stock_disponible\": 0, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1\", \"precio\": 599.00}', NULL, '2025-05-25 23:47:24'),
(12, 'almacen', 'INSERT', 1003, NULL, '{\"id_producto\": 1003, \"stock_disponible\": 1, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_8GB RAM/64GB ROM\", \"precio\": 599.00}', NULL, '2025-05-25 23:47:24'),
(13, 'almacen', 'INSERT', 1004, NULL, '{\"id_producto\": 1004, \"stock_disponible\": 0, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Lite\", \"precio\": 299.99}', NULL, '2025-05-25 23:57:17'),
(14, 'almacen', 'INSERT', 1005, NULL, '{\"id_producto\": 1005, \"stock_disponible\": 5, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Lite_8GB RAM/64GB ROM\", \"precio\": 299.99}', NULL, '2025-05-25 23:57:17'),
(15, 'almacen', 'DELETE', 1003, '{\"id_producto\": 1003, \"stock_disponible\": 1, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_8GB RAM/64GB ROM\", \"precio\": 599.00}', NULL, NULL, '2025-05-25 23:59:40'),
(16, 'almacen', 'DELETE', 1005, '{\"id_producto\": 1005, \"stock_disponible\": 5, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Lite_8GB RAM/64GB ROM\", \"precio\": 299.99}', NULL, NULL, '2025-05-26 00:00:01'),
(17, 'almacen', 'INSERT', 1012, NULL, '{\"id_producto\": 1012, \"stock_disponible\": 2, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_16GB RAM/256GB ROM\", \"precio\": 799.99}', NULL, '2025-05-26 07:02:45'),
(18, 'almacen', 'DELETE', 1012, '{\"id_producto\": 1012, \"stock_disponible\": 2, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_16GB RAM/256GB ROM\", \"precio\": 799.99}', NULL, NULL, '2025-05-26 07:07:08'),
(19, 'almacen', 'INSERT', 1013, NULL, '{\"id_producto\": 1013, \"stock_disponible\": 1, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_8GB RAM/64GB ROM\", \"precio\": 599.00}', NULL, '2025-05-26 07:30:53'),
(20, 'almacen', 'INSERT', 1014, NULL, '{\"id_producto\": 1014, \"stock_disponible\": 2, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_12GB RAM/128GB ROM\", \"precio\": 699.99}', NULL, '2025-05-26 07:48:03'),
(21, 'almacen', 'DELETE', 1014, '{\"id_producto\": 1014, \"stock_disponible\": 2, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_12GB RAM/128GB ROM\", \"precio\": 699.99}', NULL, NULL, '2025-05-26 07:48:09'),
(22, 'almacen', 'INSERT', 1015, NULL, '{\"id_producto\": 1015, \"stock_disponible\": 6, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_12GB RAM/128GB ROM\", \"precio\": 650.00}', NULL, '2025-05-26 07:48:29'),
(23, 'almacen', 'INSERT', 1016, NULL, '{\"id_producto\": 1016, \"stock_disponible\": 3, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Lite_8GB RAM/64GB ROM\", \"precio\": 299.00}', NULL, '2025-05-26 07:49:04'),
(24, 'almacen', 'INSERT', 1017, NULL, '{\"id_producto\": 1017, \"stock_disponible\": 0, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Pro\", \"precio\": 699.99}', NULL, '2025-05-26 07:49:49'),
(25, 'almacen', 'INSERT', 1018, NULL, '{\"id_producto\": 1018, \"stock_disponible\": 5, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Pro_8GB RAM/64GB ROM\", \"precio\": 699.99}', NULL, '2025-05-26 07:49:49'),
(26, 'almacen', 'DELETE', 1018, '{\"id_producto\": 1018, \"stock_disponible\": 5, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Pro_8GB RAM/64GB ROM\", \"precio\": 699.99}', NULL, NULL, '2025-05-26 07:54:18'),
(27, 'almacen', 'DELETE', 1015, '{\"id_producto\": 1015, \"stock_disponible\": 9, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_12GB RAM/128GB ROM\", \"precio\": 399.99}', NULL, NULL, '2025-05-26 07:54:21'),
(28, 'almacen', 'DELETE', 1013, '{\"id_producto\": 1013, \"stock_disponible\": 5, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_8GB RAM/64GB ROM\", \"precio\": 599.99}', NULL, NULL, '2025-05-26 07:54:24'),
(29, 'almacen', 'INSERT', 1019, NULL, '{\"id_producto\": 1019, \"stock_disponible\": 3, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_8GB RAM/64GB ROM\", \"precio\": 599.99}', NULL, '2025-05-26 07:56:02'),
(30, 'almacen', 'INSERT', 1020, NULL, '{\"id_producto\": 1020, \"stock_disponible\": 4, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_12GB RAM/128GB ROM\", \"precio\": 699.99}', NULL, '2025-05-26 08:31:52'),
(31, 'almacen', 'INSERT', 1021, NULL, '{\"id_producto\": 1021, \"stock_disponible\": 3, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_16GB RAM/256GB ROM\", \"precio\": 799.99}', NULL, '2025-05-26 08:32:13'),
(32, 'almacen', 'INSERT', 1022, NULL, '{\"id_producto\": 1022, \"stock_disponible\": 1, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Pro_8GB RAM/64GB ROM\", \"precio\": 799.99}', NULL, '2025-05-26 08:32:43'),
(33, 'almacen', 'DELETE', 1019, '{\"id_producto\": 1019, \"stock_disponible\": 7, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_8GB RAM/64GB ROM\", \"precio\": 599.99}', NULL, NULL, '2025-05-26 08:33:17'),
(34, 'almacen', 'DELETE', 1017, '{\"id_producto\": 1017, \"stock_disponible\": 0, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Pro\", \"precio\": 699.99}', NULL, NULL, '2025-05-26 11:50:44'),
(35, 'almacen', 'DELETE', 1004, '{\"id_producto\": 1004, \"stock_disponible\": 0, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Lite\", \"precio\": 299.99}', NULL, NULL, '2025-05-26 11:50:50'),
(36, 'almacen', 'DELETE', 1002, '{\"id_producto\": 1002, \"stock_disponible\": 0, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1\", \"precio\": 599.00}', NULL, NULL, '2025-05-26 11:50:53'),
(37, 'almacen', 'DELETE', 1016, '{\"id_producto\": 1016, \"stock_disponible\": 7, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Lite_8GB RAM/64GB ROM\", \"precio\": 299.99}', NULL, NULL, '2025-05-26 12:02:35'),
(38, 'almacen', 'DELETE', 1022, '{\"id_producto\": 1022, \"stock_disponible\": 2, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Pro_8GB RAM/64GB ROM\", \"precio\": 899.99}', NULL, NULL, '2025-05-26 12:02:41'),
(39, 'almacen', 'DELETE', 1021, '{\"id_producto\": 1021, \"stock_disponible\": 4, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_16GB RAM/256GB ROM\", \"precio\": 999.99}', NULL, NULL, '2025-05-26 12:02:44'),
(40, 'almacen', 'DELETE', 1020, '{\"id_producto\": 1020, \"stock_disponible\": 4, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_12GB RAM/128GB ROM\", \"precio\": 699.99}', NULL, NULL, '2025-05-26 12:02:48'),
(41, 'almacen', 'INSERT', 1023, NULL, '{\"id_producto\": 1023, \"stock_disponible\": 0, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1\", \"precio\": 599.99}', NULL, '2025-05-26 12:03:06'),
(42, 'almacen', 'INSERT', 1024, NULL, '{\"id_producto\": 1024, \"stock_disponible\": 3, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_8GB RAM/64GB ROM\", \"precio\": 599.99}', NULL, '2025-05-26 12:03:06'),
(43, 'almacen', 'INSERT', 1025, NULL, '{\"id_producto\": 1025, \"stock_disponible\": 3, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_12GB RAM/128GB ROM\", \"precio\": 699.99}', NULL, '2025-05-26 12:03:33'),
(44, 'almacen', 'INSERT', 1026, NULL, '{\"id_producto\": 1026, \"stock_disponible\": 2, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1_16GB RAM/256GB ROM\", \"precio\": 799.99}', NULL, '2025-05-26 12:03:50'),
(45, 'almacen', 'INSERT', 1027, NULL, '{\"id_producto\": 1027, \"stock_disponible\": 0, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Lite\", \"precio\": 299.99}', NULL, '2025-05-26 12:04:07'),
(46, 'almacen', 'INSERT', 1028, NULL, '{\"id_producto\": 1028, \"stock_disponible\": 8, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Lite_8GB RAM/64GB ROM\", \"precio\": 299.99}', NULL, '2025-05-26 12:04:07'),
(47, 'almacen', 'INSERT', 1029, NULL, '{\"id_producto\": 1029, \"stock_disponible\": 0, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Pro\", \"precio\": 699.99}', NULL, '2025-05-26 12:05:23'),
(48, 'almacen', 'INSERT', 1030, NULL, '{\"id_producto\": 1030, \"stock_disponible\": 4, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Pro_8GB RAM/64GB ROM\", \"precio\": 699.99}', NULL, '2025-05-26 12:05:23'),
(49, 'almacen', 'DELETE', 1023, '{\"id_producto\": 1023, \"stock_disponible\": 0, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1\", \"precio\": 599.99}', NULL, NULL, '2025-05-26 12:06:30'),
(50, 'almacen', 'DELETE', 1027, '{\"id_producto\": 1027, \"stock_disponible\": 0, \"color\": null, \"variante\": null, \"modelo\": \"PixelDB 1 Lite\", \"precio\": 299.99}', NULL, NULL, '2025-05-26 12:06:49');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos`
--

CREATE TABLE `pedidos` (
  `id_pedido` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellidos` varchar(100) DEFAULT NULL,
  `fecha_pedido` date DEFAULT curdate(),
  `fecha_entrega` date DEFAULT NULL,
  `precio` decimal(10,2) DEFAULT NULL,
  `id_producto` int(11) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `modelo` varchar(100) DEFAULT NULL,
  `color` varchar(50) DEFAULT NULL,
  `stock_disponible` int(11) NOT NULL DEFAULT 0 COMMENT 'Cantidad disponible en stock'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pedidos`
--

INSERT INTO `pedidos` (`id_pedido`, `nombre`, `apellidos`, `fecha_pedido`, `fecha_entrega`, `precio`, `id_producto`, `direccion`, `modelo`, `color`, `stock_disponible`) VALUES
(2, 'pepdap', 'laldcxl', '2025-05-26', '2025-06-02', NULL, NULL, 'ñpdapdpa', '12GB RAM/128GB ROM', 'Gris Espacial', 0),
(3, 'ppojijpi', 'dytdyyd', '2025-05-26', '2025-06-02', NULL, NULL, 'dhchcch', '12GB RAM/128GB ROM', 'Negro', 0),
(4, 'seofskfskp', 'dpafsfsmp', '2025-05-26', '2025-06-02', NULL, NULL, 'fampfsfspm', '12GB RAM/128GB ROM', 'Negro', 0),
(5, 'jojo', 'ojojjo', '2025-05-26', '2025-06-02', NULL, NULL, 'jojjoojjo', 'PixelDB 1 Pro - 16GB RAM/256GB ROM', 'Gris Espacial', 0);

--
-- Disparadores `pedidos`
--
DELIMITER $$
CREATE TRIGGER `insertar_precio_en_pedido` BEFORE INSERT ON `pedidos` FOR EACH ROW BEGIN
  DECLARE precio_producto DECIMAL(10,2);

  -- Obtener el precio desde la tabla almacen
  SELECT precio INTO precio_producto
  FROM almacen
  WHERE id_producto = NEW.id_producto;

  -- Asignar ese precio al nuevo pedido
  SET NEW.precio = precio_producto;
END
$$
DELIMITER ;

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `almacen`
--
ALTER TABLE `almacen`
  ADD PRIMARY KEY (`id_producto`);

--
-- Indices de la tabla `logs`
--
ALTER TABLE `logs`
  ADD PRIMARY KEY (`id_log`);

--
-- Indices de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD PRIMARY KEY (`id_pedido`),
  ADD KEY `fk_pedidos_almacen` (`id_producto`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `almacen`
--
ALTER TABLE `almacen`
  MODIFY `id_producto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1031;

--
-- AUTO_INCREMENT de la tabla `logs`
--
ALTER TABLE `logs`
  MODIFY `id_log` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=51;

--
-- AUTO_INCREMENT de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  MODIFY `id_pedido` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD CONSTRAINT `fk_pedidos_almacen` FOREIGN KEY (`id_producto`) REFERENCES `almacen` (`id_producto`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
