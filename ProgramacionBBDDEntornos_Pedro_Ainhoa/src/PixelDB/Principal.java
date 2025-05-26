package PixelDB;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Principal extends JFrame {

    private JButton btnVender;
    private JButton btnAlmacen;
    private JButton btnHistorialPedidos;
    private JButton btnVerCarrito;
    private JButton PixelDB1;
    private JButton PixelDB1Lite;
    private JButton PixelDB1Pro;
    private JPanel mainPanel;

    // Carrito de compra
    private Cesta shoppingCart;

    // Conexión a la base de datos
    private conexionBBDD dbConnection;

    // modelos de teléfonos
    private static final String[] MODELOS = {"PixelDB 1", "PixelDB 1 Lite", "PixelDB 1 Pro"};
    private static final String[] COLORES = {"Blanco", "Negro", "Gris Espacial"};
    private static final String[] OPCIONES_RAM_ROM = {
            "8GB RAM/64GB ROM",
            "12GB RAM/128GB ROM",
            "16GB RAM/256GB ROM"
    };

    /**
     * Interfaz para operaciones de consulta a la base de datos
     */
    private interface DatabaseOperation<T> {
        T execute(Connection conn) throws SQLException;
    }

    /**
     * Ejecuta una operación de base de datos con gestión adecuada de recursos
     *
     * @param operation La operación de base de datos a ejecutar
     * @param errorMessage El mensaje de error a mostrar si la operación falla
     * @param parent El componente padre para los diálogos de error
     * @return El resultado de la operación, o null si falla
     */
    private <T> T executeDbOperation(DatabaseOperation<T> operation, String errorMessage, Component parent) {
        if (!dbConnection.success()) {
            JOptionPane.showMessageDialog(parent,
                    "No se pudo conectar con la base de datos.",
                    "Error de conexión",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try {
            Connection conn = dbConnection.getConnection();
            return operation.execute(conn);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parent,
                    errorMessage + ": " + ex.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public Principal() {
        $$$setupUI$$$();
        setTitle("Pixel DB");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Inicializar carrito de compra
        shoppingCart = new Cesta();

        // Establecer imagen de fondo para el panel principal
        ImageIcon backgroundImage = new ImageIcon("src\\imagenes\\pixeldblogo.png");
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Dibujar la imagen de fondo
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 600);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Hacer que el panel principal sea transparente para que se vea la imagen de fondo
        mainPanel.setOpaque(false);

        backgroundPanel.add(mainPanel, BorderLayout.CENTER);

        // Establecer panel de contenido
        setContentPane(backgroundPanel);

        // Inicializar conexión a la base de datos
        dbConnection = new conexionBBDD("localhost", "3306", "root", "", "pixeldb");
        if (!dbConnection.success()) {
            JOptionPane.showMessageDialog(this,
                    "Error al conectar con la base de datos. Algunas funcionalidades pueden no estar disponibles.",
                    "Error de conexión",
                    JOptionPane.WARNING_MESSAGE);
        }

        // Añadir listener de ventana para cerrar la conexión a la base de datos cuando la aplicación se cierre
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (dbConnection != null) {
                    dbConnection.closeConnection();
                }
            }
        });

        // Añadir action listeners a los botones principales
        btnVender.addActionListener(e -> mostrarOpcionesVenta());
        btnAlmacen.addActionListener(e -> {
            if (mostrarLoginDialog()) {
                mostrarOpcionesAlmacen();
            }
        });
        btnHistorialPedidos.addActionListener(e -> {
            if (mostrarLoginDialog()) {
                mostrarHistorialPedidos();
            }
        });
        btnVerCarrito.addActionListener(e -> mostrarCarrito());

        setVisible(true);
    }

    // Crea un diálogo con el título, ancho y alto especificados
    private JDialog createDialog(String title, int width, int height) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        return dialog;
    }

    // Crea un panel de botones con el número de filas y columnas especificado
    private JPanel createButtonPanel(int rows, int cols) {
        JPanel buttonPanel = new JPanel(new GridLayout(rows, cols, 10, 10));
        JPanel paddingPanel = new JPanel(new BorderLayout());
        paddingPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddingPanel.add(buttonPanel, BorderLayout.CENTER);
        return buttonPanel;
    }

    // Crea un botón con el texto, diálogo y acción especificados
    private JButton createButton(String text, JDialog dialog, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> {
            dialog.dispose();
            if (action != null) {
                action.run();
            }
        });
        return button;
    }

    // Muestra un diálogo con opciones de venta
    private void mostrarOpcionesVenta() {
        JDialog dialog = createDialog("Seleccione un modelo", 400, 450);
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear botones con imágenes para cada modelo
        for (int i = 0; i < MODELOS.length; i++) {
            String modelo = MODELOS[i];
            // Cargar imagen del modelo (asegúrate de que exista el archivo)
            String nombreImagen = modelo.replace(" ", "").toLowerCase() + ".png";
            ImageIcon icono = new ImageIcon("src\\imagenes\\" + nombreImagen);

            // Redimensionar imagen (tamaño reducido para dejar espacio al texto)
            Image img = icono.getImage();
            Image imgRedimensionada = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            ImageIcon iconoRedimensionado = new ImageIcon(imgRedimensionada);

            // Crear botón con imagen y texto
            JButton button = new JButton(modelo, iconoRedimensionado);
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.BOTTOM);
            // Asegurar que hay suficiente espacio para el texto
            button.setIconTextGap(10);

            button.addActionListener(e -> {
                dialog.dispose();
                ventanaOpciones();
            });

            buttonPanel.add(button);
        }

        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // Muestra un diálogo con el historial de pedidos
    private void mostrarHistorialPedidos() {
        JDialog dialog = createDialog("Historial de Pedidos", 800, 500);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear tabla para mostrar los pedidos
        String[] columnNames = {"ID Pedido", "Nombre y Apellidos", "Fecha de Pedido", "Fecha de Entrega", "Precio"};
        Object[][] data = {};

        // Modelo de tabla no editable
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        // Configurar ancho de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(250); // Nombre y Apellidos
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Fecha Pedido
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Fecha Entrega
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Precio

        // Crear un TableRowSorter para filtrar la tabla
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Panel para los campos de texto de filtrado
        JPanel filterPanel = new JPanel(new GridLayout(2, 5, 10, 5));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Crear campos de texto para cada columna
        JTextField idField = new JTextField();
        JTextField nombreField = new JTextField();
        JTextField fechaPedidoField = new JTextField();
        JTextField fechaEntregaField = new JTextField();
        JTextField precioField = new JTextField();

        // Añadir etiquetas para cada campo
        filterPanel.add(new JLabel("ID Pedido"));
        filterPanel.add(new JLabel("Nombre y Apellidos"));
        filterPanel.add(new JLabel("Fecha de Pedido"));
        filterPanel.add(new JLabel("Fecha de Entrega"));
        filterPanel.add(new JLabel("Precio"));

        // Añadir campos de texto al panel
        filterPanel.add(idField);
        filterPanel.add(nombreField);
        filterPanel.add(fechaPedidoField);
        filterPanel.add(fechaEntregaField);
        filterPanel.add(precioField);

        // Añadir document listeners a los campos de texto para filtrar la tabla
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }

            private void filterTable() {
                String idText = idField.getText().trim();
                String nombreText = nombreField.getText().trim();
                String fechaPedidoText = fechaPedidoField.getText().trim();
                String fechaEntregaText = fechaEntregaField.getText().trim();
                String precioText = precioField.getText().trim();

                RowFilter<DefaultTableModel, Object> rf = null;
                try {
                    // Crear un filtro compuesto para todas las columnas
                    rf = RowFilter.andFilter(java.util.List.of(
                            RowFilter.regexFilter("(?i)" + idText, 0),
                            RowFilter.regexFilter("(?i)" + nombreText, 1),
                            RowFilter.regexFilter("(?i)" + fechaPedidoText, 2),
                            RowFilter.regexFilter("(?i)" + fechaEntregaText, 3),
                            RowFilter.regexFilter("(?i)" + precioText, 4)
                    ));
                } catch (java.util.regex.PatternSyntaxException e) {
                    return;
                }
                sorter.setRowFilter(rf);
            }
        };

        // Añadir el document listener a cada campo de texto
        idField.getDocument().addDocumentListener(documentListener);
        nombreField.getDocument().addDocumentListener(documentListener);
        fechaPedidoField.getDocument().addDocumentListener(documentListener);
        fechaEntregaField.getDocument().addDocumentListener(documentListener);
        precioField.getDocument().addDocumentListener(documentListener);

        // Panel para contener el título y los campos de filtrado
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Historial de Pedidos", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        // Añadir el panel superior al panel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Scroll pane para la tabla
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Cargar datos de la base de datos
        if (dbConnection.success()) {
            try {
                Connection conn = dbConnection.getConnection();
                // Intentar primero con 'id' como nombre de columna
                String sql = "";
                try {                    sql = "SELECT id_pedido, nombre, apellidos, fecha_pedido, fecha_entrega, precio FROM pedidos ORDER BY fecha_pedido DESC";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery();

                    // Limpiar tabla
                    tableModel.setRowCount(0);

                    // Añadir filas a la tabla
                    while (rs.next()) {
                        int id_pedido = rs.getInt("id_pedido");
                        String nombre = rs.getString("nombre");
                        String apellidos = rs.getString("apellidos");
                        String nombreCompleto = nombre + " " + apellidos;
                        String fechaPedido = rs.getString("fecha_pedido");
                        String fechaEntrega = rs.getString("fecha_entrega");
                        double precio = rs.getDouble("precio");

                        tableModel.addRow(new Object[]{id_pedido, nombreCompleto, fechaPedido, fechaEntrega, precio});
                    }

                    rs.close();
                    stmt.close();
                } catch (SQLException ex) {
                    // Si falla, intentar con 'id_pedido' como nombre de columna
                    try {
                        sql = "SELECT id_pedido, nombre, apellidos, fecha_pedido, fecha_entrega, precio FROM pedidos ORDER BY fecha_pedido DESC";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        ResultSet rs = stmt.executeQuery();

                        // Limpiar tabla
                        tableModel.setRowCount(0);

                        // Añadir filas a la tabla
                        while (rs.next()) {
                            int id_pedido = rs.getInt("id_pedido");
                            String nombre = rs.getString("nombre");
                            String apellidos = rs.getString("apellidos");
                            String nombreCompleto = nombre + " " + apellidos;
                            String fechaPedido = rs.getString("fecha_pedido");
                            String fechaEntrega = rs.getString("fecha_entrega");
                            double precio = rs.getDouble("precio");

                            tableModel.addRow(new Object[]{id_pedido, nombreCompleto, fechaPedido, fechaEntrega, precio});
                        }

                        rs.close();
                        stmt.close();
                    } catch (SQLException e) {
                        throw e; // Propagar la excepción para que sea capturada por el bloque catch exterior
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error al cargar el historial de pedidos: " + ex.getMessage(),
                        "Error de base de datos",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(dialog,
                    "No se pudo conectar con la base de datos.",
                    "Error de conexión",
                    JOptionPane.WARNING_MESSAGE);
        }

        // Botón para cerrar
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // Muestra las opciones de almacén
    private void mostrarOpcionesAlmacen() {
        mostrarAlmacen();
    }

    // Muestra el almacen en un diálogo
    private void mostrarAlmacen() {
        JDialog dialog = createDialog("Almacen", 800, 500);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear tabla para mostrar el almacen
        String[] columnNames = {"ID Producto", "Stock Disponible", "Color", "Variante", "Modelo", "Precio"};
        Object[][] data = {};

        // Modelo de tabla no editable
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        // Configurar ancho de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID Producto
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Stock Disponible
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Color
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Variante
        table.getColumnModel().getColumn(4).setPreferredWidth(150); // Modelo
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Precio

        // Crear un TableRowSorter para filtrar la tabla
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Panel para los campos de texto de filtrado
        JPanel filterPanel = new JPanel(new GridLayout(2, 6, 10, 5));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Crear campos de texto para cada columna
        JTextField idField = new JTextField();
        JTextField stockField = new JTextField();
        JTextField colorField = new JTextField();
        JTextField varianteField = new JTextField();
        JTextField modeloField = new JTextField();
        JTextField precioField = new JTextField();

        // Añadir etiquetas para cada campo
        filterPanel.add(new JLabel("ID Producto"));
        filterPanel.add(new JLabel("Stock Disponible"));
        filterPanel.add(new JLabel("Color"));
        filterPanel.add(new JLabel("Variante"));
        filterPanel.add(new JLabel("Modelo"));
        filterPanel.add(new JLabel("Precio"));

        // Añadir campos de texto al panel
        filterPanel.add(idField);
        filterPanel.add(stockField);
        filterPanel.add(colorField);
        filterPanel.add(varianteField);
        filterPanel.add(modeloField);
        filterPanel.add(precioField);

        // Añadir document listeners a los campos de texto para filtrar la tabla
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }

            private void filterTable() {
                String idText = idField.getText().trim();
                String stockText = stockField.getText().trim();
                String colorText = colorField.getText().trim();
                String varianteText = varianteField.getText().trim();
                String modeloText = modeloField.getText().trim();
                String precioText = precioField.getText().trim();

                RowFilter<DefaultTableModel, Object> rf = null;
                try {
                    // Crear un filtro compuesto para todas las columnas
                    rf = RowFilter.andFilter(java.util.List.of(
                            RowFilter.regexFilter("(?i)" + idText, 0),
                            RowFilter.regexFilter("(?i)" + stockText, 1),
                            RowFilter.regexFilter("(?i)" + colorText, 2),
                            RowFilter.regexFilter("(?i)" + varianteText, 3),
                            RowFilter.regexFilter("(?i)" + modeloText, 4),
                            RowFilter.regexFilter("(?i)" + precioText, 5)
                    ));
                } catch (java.util.regex.PatternSyntaxException e) {
                    return;
                }
                sorter.setRowFilter(rf);
            }
        };

        // Añadir el document listener a cada campo de texto
        idField.getDocument().addDocumentListener(documentListener);
        stockField.getDocument().addDocumentListener(documentListener);
        colorField.getDocument().addDocumentListener(documentListener);
        varianteField.getDocument().addDocumentListener(documentListener);
        modeloField.getDocument().addDocumentListener(documentListener);
        precioField.getDocument().addDocumentListener(documentListener);

        // Panel para contener el título y los campos de filtrado
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Almacen", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        // Añadir el panel superior al panel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Scroll pane para la tabla
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Cargar datos de la base de datos
        // Generar datos para la tabla
        tableModel.setRowCount(0);

        // Usar el método executeDbOperation para cargar los datos
        executeDbOperation(conn -> {
            // Obtener los modelos de la tabla almacen
            String sql = "SELECT id_producto, modelo, stock_disponible, precio FROM almacen";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                int id_producto = 1;

                // Para cada modelo en el almacen
                while (rs.next()) {
                    String modelo = rs.getString("modelo");
                    int stock_disponible = rs.getInt("stock_disponible");
                    double precio = rs.getDouble("precio");

                    // Comprobar si es una combinación de modelo-variante
                    boolean isModelVariant = false;
                    String baseModel = modelo;
                    String specificVariant = "";

                    for (String variante : OPCIONES_RAM_ROM) {
                        if (modelo.endsWith("_" + variante)) {
                            isModelVariant = true;
                            baseModel = modelo.substring(0, modelo.length() - variante.length() - 1);
                            specificVariant = variante;
                            break;
                        }
                    }

                    if (isModelVariant) {
                        // Esta es una combinación específica de modelo-variante
                        for (String color : COLORES) {
                            tableModel.addRow(new Object[]{
                                    id_producto++,
                                    stock_disponible, // Mostrar el stock completo sin dividir
                                    color,
                                    specificVariant,
                                    baseModel,
                                    String.format("%.2f €", precio)
                            });
                        }
                    } else {
                        // Este es un modelo base
                        // Para cada color
                        for (String color : COLORES) {
                            // Para cada variante
                            for (String variante : OPCIONES_RAM_ROM) {
                                // Comprobar si existe una combinación específica de modelo-variante
                                boolean skipVariant = false;
                                for (int i = 0; i < tableModel.getRowCount(); i++) {
                                    String existingModel = (String) tableModel.getValueAt(i, 4);
                                    String existingVariant = (String) tableModel.getValueAt(i, 3);
                                    if (existingModel.equals(baseModel) && existingVariant.equals(variante)) {
                                        skipVariant = true;
                                        break;
                                    }
                                }

                                if (!skipVariant) {
                                    // Añadir una fila por cada combinación
                                    tableModel.addRow(new Object[]{
                                            id_producto++,
                                            stock_disponible > 0 ? stock_disponible / (COLORES.length * OPCIONES_RAM_ROM.length) : 0,
                                            color,
                                            variante,
                                            modelo,
                                            String.format("%.2f €", precio)
                                    });
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }, "Error al cargar el almacen", dialog);


        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        // Botón para añadir producto
        JButton addButton = new JButton("Añadir Producto");
        addButton.addActionListener(e -> {
            mostrarDialogoAnadirMovilMejorado();
            // Actualizar la tabla de almacen después de añadir un producto
            dialog.dispose();
            mostrarAlmacen();
        });

        // Botón para editar producto
        JButton editButton = new JButton("Editar Producto");
        editButton.addActionListener(e -> {
            // Comprobar si hay una fila seleccionada
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog,
                        "Por favor, seleccione un producto para editar.",
                        "Selección requerida",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Obtener los datos del producto seleccionado
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String modelo = (String) tableModel.getValueAt(modelRow, 4); // Modelo column
            String color = (String) tableModel.getValueAt(modelRow, 2); // Color column
            String variante = (String) tableModel.getValueAt(modelRow, 3); // Variante column

            mostrarDialogoEditarMovil(modelo, color, variante);
            // Actualizar la tabla de almacen después de editar un producto
            dialog.dispose();
            mostrarAlmacen();
        });

        // Botón para eliminar producto
        JButton deleteButton = new JButton("Eliminar Producto");
        deleteButton.addActionListener(e -> {
            // Comprobar si hay una fila seleccionada
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog,
                        "Por favor, seleccione un producto para eliminar.",
                        "Selección requerida",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Obtener los datos del producto seleccionado
            int modelRow = table.convertRowIndexToModel(selectedRow);
            String modelo = (String) tableModel.getValueAt(modelRow, 4); // Modelo column
            String color = (String) tableModel.getValueAt(modelRow, 2); // Color column
            String variante = (String) tableModel.getValueAt(modelRow, 3); // Variante column

            // Mostrar diálogo de confirmación
            int option = JOptionPane.showConfirmDialog(dialog,
                    "¿Está seguro de que desea eliminar el producto " + modelo + " (" + color + ", " + variante + ")?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                // Eliminar el producto de la base de datos
                eliminarProducto(modelo, variante);
                // Actualizar la tabla de almacen
                dialog.dispose();
                mostrarAlmacen();
            }
        });


        // Botón para cerrar
        JButton closeButton = new JButton("Cerrar");
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // Crea un panel con un mensaje
    private JPanel createMessagePanel(String message) {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(messageLabel, BorderLayout.CENTER);

        return contentPanel;
    }

    // Muestra una ventana con opciones de RAM/ROM
    private void ventanaOpciones() {
        JDialog dialog = createDialog("Seleccione una opción", 500, 400);
        JPanel buttonPanel = createButtonPanel(3, 1);

        // Crear botones para cada opción de RAM/ROM
        for (String opcion : OPCIONES_RAM_ROM) {
            buttonPanel.add(createButton(opcion, dialog, () -> mostrarDialogoStock(opcion)));
        }

        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // Muestra un diálogo con información de stock para la opción seleccionada
    private void mostrarDialogoStock(String opcionSeleccionada) {
        JDialog dialogStock = createDialog("Información de Stock", 400, 200);

        // Crea el mensaje de Hay stock disponible
        JPanel contentPanel = createMessagePanel("Hay stock disponible para " + opcionSeleccionada);

        // Crea el boton continuar
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton("Continuar", dialogStock,
                () -> mostrarDialogoColor(opcionSeleccionada, true)));

        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialogStock.add(contentPanel, BorderLayout.CENTER);
        dialogStock.setVisible(true);
    }

    // Crea un área de texto con un mensaje
    private JTextArea createMessageArea(String message) {
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setBackground(UIManager.getColor("Panel.background"));
        messageArea.setFont(new JLabel().getFont());
        return messageArea;
    }

    //El diálogo de elegir el color
    private void mostrarDialogoColor(String opcionSeleccionada, boolean consultarStock) {
        JDialog dialogColor = createDialog("Seleccione un color", 400, 300);
        JPanel buttonPanel = createButtonPanel(3, 1);

        // Crea un boton por cada color
        for (String color : COLORES) {
            buttonPanel.add(createButton(color, dialogColor,
                    () -> mostrarResumenSeleccion(opcionSeleccionada, consultarStock, color)));
        }

        dialogColor.add(buttonPanel, BorderLayout.CENTER);
        dialogColor.setVisible(true);
    }

    //El resumen de lo que ha elegido el cliente
    private void mostrarResumenSeleccion(String opcion, boolean consultarStock, String color) {
        JDialog dialogResumen = createDialog("Resumen de selección", 400, 300);

        // Panel para el mensaje
        JPanel messagePanel = new JPanel(new BorderLayout());

        // Obtener el stock_disponible desde la base de datos
        final int[] stockArray = new int[1];
        if (dbConnection.success()) {
            try {
                Connection conn = dbConnection.getConnection();

                // Construir el modelo_variante para buscar en la tabla almacen
                String modeloBase = "";
                if (opcion.contains("8GB")) {
                    modeloBase = "PixelDB 1";
                } else if (opcion.contains("12GB")) {
                    modeloBase = "PixelDB 1 Lite";
                } else {
                    modeloBase = "PixelDB 1 Pro";
                }
                String modeloVariante = modeloBase + "_" + opcion;

                // Consulta SQL para obtener el stock_disponible directamente de la tabla almacen
                String sql = "SELECT stock_disponible FROM almacen WHERE modelo = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, modeloVariante);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    stockArray[0] = rs.getInt("stock_disponible");
                } else {
                    // Si no encuentra el stock, mostrar mensaje de error
                    JOptionPane.showMessageDialog(dialogResumen,
                        "Error: No se encontró el stock del producto en el almacén",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    dialogResumen.dispose();
                    return;
                }

                rs.close();
                stmt.close();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialogResumen,
                    "Error al consultar el stock: " + ex.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE);
                dialogResumen.dispose();
                return;
            }
        } else {
            JOptionPane.showMessageDialog(dialogResumen,
                "No se pudo conectar con la base de datos",
                "Error de conexión",
                JOptionPane.ERROR_MESSAGE);
            dialogResumen.dispose();
            return;
        }

        // Crear el mensaje con el stock obtenido de la base de datos
        String stockDisponible = stockArray[0] > 0 ? "Disponible (" + stockArray[0] + " unidades)" : "No disponible";
        String mensaje = "Ha seleccionado:\n" +
                "- Opción: " + opcion + "\n" +
                "- Stock: " + stockDisponible + "\n" +
                "- Color: " + color;

        messagePanel.add(createMessageArea(mensaje), BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        // Botón de añadir al carrito (solo habilitado si hay stock)
        JButton addToCartButton = new JButton("Añadir al Carrito");
        addToCartButton.setEnabled(stockArray[0] > 0);
        addToCartButton.addActionListener(e -> {
            String modelo = "";
            if (opcion.contains("8GB")) {
                modelo = "PixelDB 1";
            } else if (opcion.contains("12GB")) {
                modelo = "PixelDB 1 Lite";
            } else {
                modelo = "PixelDB 1 Pro";
            }

            Cesta.Objeto Objeto = new Cesta.Objeto(modelo, opcion, color, 0); // El precio se maneja en otro lugar
            shoppingCart.addItem(Objeto);

            JOptionPane.showMessageDialog(dialogResumen,
                    "Artículo añadido al carrito",
                    "Carrito actualizado",
                    JOptionPane.INFORMATION_MESSAGE);

            dialogResumen.dispose();
        });

        // Botón de comprar ahora (solo habilitado si hay stock)
        JButton buyNowButton = new JButton("Comprar Ahora");
        buyNowButton.setEnabled(stockArray[0] > 0);
        buyNowButton.addActionListener(e -> {
            dialogResumen.dispose();
            mostrarFormularioDatos(opcion, color);
        });

        // Botón de volver
        JButton backButton = new JButton("Volver");
        backButton.addActionListener(e -> {
            dialogResumen.dispose();
            ventanaOpciones();
        });

        buttonPanel.add(addToCartButton);
        buttonPanel.add(buyNowButton);
        buttonPanel.add(backButton);

        // Crear y añadir panel de contenido
        JPanel contentPanel = createContentPanel(messagePanel, buttonPanel);
        dialogResumen.add(contentPanel, BorderLayout.CENTER);
        dialogResumen.setVisible(true);
    }

    //
    private JPanel createFormPanel(int rows) {
        JPanel formPanel = new JPanel(new GridLayout(rows, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return formPanel;
    }

    //
    private void addFormField(JPanel panel, String labelText, JComponent component) {
        panel.add(new JLabel(labelText));
        panel.add(component);
    }

    //Mostrar Datos del cliente
    private void mostrarFormularioDatos(String opcion, String color) {
        JDialog dialogDatos = createDialog("Datos del cliente", 500, 300);

        //Panel de usuarios
        JPanel formPanel = createFormPanel(5);

        // Campos de formulario
        JTextField nombreField = new JTextField(20);
        JTextField apellidosField = new JTextField(20);
        JTextField direccionField = new JTextField(20);
        JLabel stockValor = new JLabel("Consultando...");
        JLabel precioValor = new JLabel("Consultando...");

        // Añadir campos al panel, nombre, apellido, etc
        addFormField(formPanel, "Nombre:", nombreField);
        addFormField(formPanel, "Apellidos:", apellidosField);
        addFormField(formPanel, "Dirección de entrega:", direccionField);
        addFormField(formPanel, "Stock disponible:", stockValor);
        addFormField(formPanel, "Precio a pagar:", precioValor);

        // Obtener stock y precio de la base de datos
        if (dbConnection.success()) {
            try {
                Connection conn = dbConnection.getConnection();

                // Construir el modelo_variante para buscar en almacen
                String modeloBase = "";
                if (opcion.contains("8GB")) {
                    modeloBase = "PixelDB 1";
                } else if (opcion.contains("12GB")) {
                    modeloBase = "PixelDB 1 Lite";
                } else {
                    modeloBase = "PixelDB 1 Pro";
                }
                String modeloVariante = modeloBase + "_" + opcion;

                // Consultar stock_disponible de almacen
                String stockSql = "SELECT stock_disponible FROM almacen WHERE modelo = ?";
                PreparedStatement stockStmt = conn.prepareStatement(stockSql);
                stockStmt.setString(1, modeloVariante);
                ResultSet stockRs = stockStmt.executeQuery();

                if (stockRs.next()) {
                    int stock_disponible = stockRs.getInt("stock_disponible");
                    stockValor.setText(String.valueOf(stock_disponible));
                } else {
                    stockValor.setText("0");
                }

                // Consultar precio de la tabla pedidos (usando el último precio registrado)
                String priceSql = "SELECT precio FROM pedidos WHERE modelo LIKE ? ORDER BY fecha_pedido DESC LIMIT 1";
                PreparedStatement priceStmt = conn.prepareStatement(priceSql);
                priceStmt.setString(1, "%" + modeloBase + "%");
                ResultSet priceRs = priceStmt.executeQuery();

                if (priceRs.next()) {
                    double precio = priceRs.getDouble("precio");
                    precioValor.setText(String.format("%.2f €", precio));
                } else {
                    // Precios por defecto si no hay registros en pedidos
                    double precio = 0.0;
                    if (opcion.contains("Pro")) {
                        precio = 999.99;
                    } else if (opcion.contains("Lite")) {
                        precio = 599.99;
                    } else {
                        precio = 799.99;
                    }
                    precioValor.setText(String.format("%.2f €", precio));
                }

                stockRs.close();
                stockStmt.close();
                priceRs.close();
                priceStmt.close();

            } catch (SQLException ex) {
                stockValor.setText("Error");
                precioValor.setText("Error");
                System.out.println("Error al consultar datos: " + ex.getMessage());
            }
        } else {
            stockValor.setText("N/A");
            precioValor.setText("N/A");
        }

        // Crear boton de finalizar y finaliza
        JButton finalizarButton = new JButton("Finalizar");
        finalizarButton.addActionListener(e -> {
            // Obtener datos de los campos
            String nombre = nombreField.getText();
            String apellidos = apellidosField.getText();
            String direccion = direccionField.getText();

            // Obtener el precio actual del label
            String precioText = precioValor.getText().replace(" €", "");
            double precio = Double.parseDouble(precioText);

            // Validar que los campos no estén vacíos
            if (nombre.isEmpty() || apellidos.isEmpty() || direccion.isEmpty()) {
                JOptionPane.showMessageDialog(dialogDatos,
                        "Por favor, complete todos los campos",
                        "Campos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Guardar datos en la base de datos
            boolean success = true;
            if (dbConnection.success()) {
                try {
                    Connection conn = dbConnection.getConnection();
                    String sql = "INSERT INTO pedidos (nombre, apellidos, direccion, modelo, color, precio, fecha_pedido, fecha_entrega) VALUES (?, ?, ?, ?, ?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY))";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, nombre);
                    stmt.setString(2, apellidos);
                    stmt.setString(3, direccion);
                    stmt.setString(4, opcion);
                    stmt.setString(5, color);
                    stmt.setDouble(6, precio);
                    stmt.executeUpdate();
                    stmt.close();
                } catch (SQLException ex) {
                    success = false;
                    JOptionPane.showMessageDialog(dialogDatos,
                            "Error al guardar los datos: " + ex.getMessage(),
                            "Error de base de datos",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                success = false;
                JOptionPane.showMessageDialog(dialogDatos,
                        "No se pudo conectar con la base de datos.",
                        "Error de conexión",
                        JOptionPane.WARNING_MESSAGE);
            }

            if (success) {
                JOptionPane.showMessageDialog(dialogDatos,
                        "¡Gracias por su compra!\n" +
                                "Los datos han sido registrados correctamente.",
                        "Compra finalizada",
                        JOptionPane.INFORMATION_MESSAGE);
                dialogDatos.dispose();
            }
        });

        // Crear botón de cancelar
        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(e -> dialogDatos.dispose());

        // Crear panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(finalizarButton);

        // Crear y agregar panel de contenido
        JPanel contentPanel = createContentPanel(formPanel, buttonPanel);
        dialogDatos.add(contentPanel, BorderLayout.CENTER);
        dialogDatos.setVisible(true);
    }

    public static void main(String[] args) {
        // Usar SwingUtilities.invokeLater para la seguridad de los hilos??
        SwingUtilities.invokeLater(() -> new Principal());
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     */
    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnVender = new JButton();
        btnVender.setText("Vender");
        mainPanel.add(btnVender);
        btnAlmacen = new JButton();
        btnAlmacen.setText("Almacén");
        mainPanel.add(btnAlmacen);
        btnHistorialPedidos = new JButton();
        btnHistorialPedidos.setText("Historial de Pedidos");
        mainPanel.add(btnHistorialPedidos);
        btnVerCarrito = new JButton();
        btnVerCarrito.setText("Ver Carrito");
        mainPanel.add(btnVerCarrito);
        PixelDB1 = new JButton();
        PixelDB1.setText("PixelDB 1");
        PixelDB1.setVisible(false);
        PixelDB1Lite = new JButton();
        PixelDB1Lite.setText("PixelDB 1 Lite");
        PixelDB1Lite.setVisible(false);
        PixelDB1Pro = new JButton();
        PixelDB1Pro.setText("PixelDB 1 Pro");
        PixelDB1Pro.setVisible(false);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     */
    private void LanzarUI() {
        $$$setupUI$$$();
    }

    private JPanel createContentPanel(JPanel formPanel, JPanel buttonPanel) {
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        return contentPanel;
    }


    // Muestra el contenido del carrito de compra y permite al usuario proceder al pago
    private void mostrarCarrito() {
        JDialog dialog = createDialog("Carrito de Compra", 600, 400);

        // Crear panel para el contenido del carrito
        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear tabla para los artículos del carrito
        String[] columnNames = {"Modelo", "Opción", "Color", "Precio"};
        Object[][] data = new Object[shoppingCart.getConteoObjetos()][4];


        // Llenar la tabla con los artículos del carrito
        int i = 0;
        for (Cesta.Objeto objeto : shoppingCart.Objetos()) {
            data[i][0] = objeto.getModelo();
            data[i][1] = objeto.getOpcion();
            data[i][2] = objeto.getColor();
            data[i][3] = String.format("%.2f €", objeto.getPrecio());
            i++;
        }

        // Crear modelo de tabla
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Crear tabla
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);

        // Configurar anchos de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(150);  // Modelo
        table.getColumnModel().getColumn(1).setPreferredWidth(150);  // Opción
        table.getColumnModel().getColumn(2).setPreferredWidth(100);  // Color
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Precio

        // Añadir tabla al panel de desplazamiento
        JScrollPane scrollPane = new JScrollPane(table);
        cartPanel.add(scrollPane, BorderLayout.CENTER);

        // Crear panel para el precio total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total: " + String.format("%.2f €", shoppingCart.getTotalPrecio()));

        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(totalLabel);
        cartPanel.add(totalPanel, BorderLayout.SOUTH);

        // Crear botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        // Si el carrito está vacío, mostrar mensaje y solo añadir botón de cerrar
        if (shoppingCart.isEmpty()) {
            JLabel emptyLabel = new JLabel("El carrito está vacío");
            emptyLabel.setFont(new Font("Arial", Font.BOLD, 16));
            cartPanel.add(emptyLabel, BorderLayout.CENTER);

            JButton closeButton = new JButton("Cerrar");
            closeButton.addActionListener(e -> dialog.dispose());
            buttonPanel.add(closeButton);
        } else {
            // Añadir botones para finalizar compra, eliminar artículo y continuar comprando
            JButton checkoutButton = new JButton("Finalizar Compra");
            checkoutButton.addActionListener(e -> {
                dialog.dispose();
                mostrarFormularioDatosCarrito();
            });

            JButton removeButton = new JButton("Eliminar Seleccionado");
            removeButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    shoppingCart.removeItem(selectedRow);
                    dialog.dispose();
                    mostrarCarrito();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Seleccione un artículo para eliminar",
                            "Ningún artículo seleccionado",
                            JOptionPane.WARNING_MESSAGE);
                }
            });

            JButton continueButton = new JButton("Seguir Comprando");
            continueButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(checkoutButton);
            buttonPanel.add(removeButton);
            buttonPanel.add(continueButton);
        }

        // Añadir paneles al diálogo
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.add(cartPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(contentPanel);
        dialog.setVisible(true);
    }


    //Muestra un formulario para introducir la información del cliente para todos los artículos en el carrito
    private void mostrarFormularioDatosCarrito() {
        JDialog dialogDatos = createDialog("Datos del cliente", 500, 300);

        // Crear panel de formulario
        JPanel formPanel = createFormPanel(5);

        // Crear campos de formulario
        JTextField nombreField = new JTextField(20);
        JTextField apellidosField = new JTextField(20);
        JTextField direccionField = new JTextField(20);
        JLabel totalItemsLabel = new JLabel(shoppingCart.getConteoObjetos() + " artículos");
        JLabel precioTotalLabel = new JLabel(String.format("%.2f €", shoppingCart.getTotalPrecio()));

        // Añadir campos al panel de formulario
        addFormField(formPanel, "Nombre:", nombreField);
        addFormField(formPanel, "Apellidos:", apellidosField);
        addFormField(formPanel, "Dirección de entrega:", direccionField);
        addFormField(formPanel, "Total artículos:", totalItemsLabel);
        addFormField(formPanel, "Precio total:", precioTotalLabel);

        // Crear botón con acción personalizada
        JButton finalizarButton = new JButton("Finalizar");
        finalizarButton.addActionListener(e -> {
            // Obtener datos de los campos del formulario
            String nombre = nombreField.getText();
            String apellidos = apellidosField.getText();
            String direccion = direccionField.getText();

            // Validar campos del formulario
            if (nombre.isEmpty() || apellidos.isEmpty() || direccion.isEmpty()) {
                JOptionPane.showMessageDialog(dialogDatos,
                        "Por favor, complete todos los campos",
                        "Campos incompletos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Guardar datos en la base de datos
            boolean success = true;
            if (dbConnection.success()) {
                try {
                    Connection conn = dbConnection.getConnection();

                    // Insertar un registro para cada artículo en el carrito
                    for (Cesta.Objeto objeto : shoppingCart.Objetos()) {
                        String sql = "INSERT INTO pedidos (nombre, apellidos, direccion, modelo, color, fecha_pedido, fecha_entrega) VALUES (?, ?, ?, ?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY))";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setString(1, nombre);
                        stmt.setString(2, apellidos);
                        stmt.setString(3, direccion);
                        stmt.setString(4, objeto.getModelo() + " - " + objeto.getOpcion());
                        stmt.setString(5, objeto.getColor());
                        stmt.executeUpdate();
                        stmt.close();
                    }
                } catch (SQLException ex) {
                    success = false;
                    JOptionPane.showMessageDialog(dialogDatos,
                            "Error al guardar los datos: " + ex.getMessage(),
                            "Error de base de datos",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                success = false;
                JOptionPane.showMessageDialog(dialogDatos,
                        "No se pudo conectar con la base de datos.",
                        "Error de conexión",
                        JOptionPane.WARNING_MESSAGE);
            }

            // Mostrar mensaje de confirmación si es exitoso
            if (success) {
                JOptionPane.showMessageDialog(dialogDatos,
                        "¡Gracias por su compra!\n" +
                                "Los datos han sido registrados correctamente.\n" +
                                "Total: " + String.format("%.2f €", shoppingCart.getTotalPrecio()),
                        "Compra finalizada",
                        JOptionPane.INFORMATION_MESSAGE);

                // Vaciar el carrito de compra
                shoppingCart.clear();

                dialogDatos.dispose();
            }
        });

        // Crear botón de cancelar
        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(e -> dialogDatos.dispose());

        // Crear panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(finalizarButton);
        buttonPanel.add(cancelarButton);

        // Crear y añadir panel de contenido
        JPanel contentPanel = createContentPanel(formPanel, buttonPanel);
        dialogDatos.add(contentPanel, BorderLayout.CENTER);
        dialogDatos.setVisible(true);
    }

    //Muestra un diálogo de inicio de sesión para autenticar al usuario

    private boolean mostrarLoginDialog() {
        JDialog dialog = createDialog("Login", 300, 200);

        // Crear panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Crear campos de formulario
        JTextField usuarioField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        // Añadir campos al panel de formulario
        formPanel.add(new JLabel("Usuario:"));
        formPanel.add(usuarioField);
        formPanel.add(new JLabel("Contraseña:"));
        formPanel.add(passwordField);

        // Crear panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        // Crear botón de inicio de sesión
        JButton loginButton = new JButton("Login");

        // Crear botón de cancelar
        JButton cancelButton = new JButton("Cancelar");

        // Añadir botones al panel
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        // Crear panel de contenido
        JPanel contentPanel = createContentPanel(formPanel, buttonPanel);
        dialog.add(contentPanel, BorderLayout.CENTER);

        // Crear un array final para almacenar el resultado (true si el inicio de sesión es exitoso, false en caso contrario)
        final boolean[] result = {false};

        // Añadir action listener al botón de inicio de sesión
        loginButton.addActionListener(e -> {
            String usuario = usuarioField.getText();
            String password = new String(passwordField.getPassword());

            if (usuario.equals("admin") && password.equals("1234")) {
                result[0] = true;
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Usuario o contraseña incorrecta",
                        "Error de autenticación",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Añadir action listener al botón de cancelar
        cancelButton.addActionListener(e -> dialog.dispose());

        // Mostrar diálogo
        dialog.setVisible(true);

        // Devolver resultado
        return result[0];
    }



    //Muestra un diálogo mejorado para añadir un nuevo teléfono al almacén con todos los detalles

    private void mostrarDialogoAnadirMovilMejorado() {
        JDialog dialog = createDialog("Añadir Producto", 500, 400);

        // Crear panel de formulario con más campos
        JPanel formPanel = createFormPanel(6);

        // Crear campos del formulario
        JComboBox<String> modeloCombo = new JComboBox<>(MODELOS);
        JComboBox<String> colorCombo = new JComboBox<>(COLORES);
        JComboBox<String> varianteCombo = new JComboBox<>(OPCIONES_RAM_ROM);
        JSpinner stock_disponibleSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JSpinner precioSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 10000.00, 0.01));
        JSpinner.NumberEditor precioEditor = new JSpinner.NumberEditor(precioSpinner, "0.00");
        precioSpinner.setEditor(precioEditor);
        JLabel stockValor = new JLabel("0"); // Se actualizará cuando se seleccione el modelo

        // Añadir campos al panel de formulario
        addFormField(formPanel, "Modelo:", modeloCombo);
        addFormField(formPanel, "Color:", colorCombo);
        addFormField(formPanel, "Variante:", varianteCombo);
        addFormField(formPanel, "Cantidad:", stock_disponibleSpinner);
        addFormField(formPanel, "Precio (€):", precioSpinner);
        addFormField(formPanel, "Stock actual:", stockValor);


        // Crear botón con acción personalizada
        JButton anadirButton = new JButton("Añadir");
        anadirButton.addActionListener(e -> {
            String modelo = (String) modeloCombo.getSelectedItem();
            String color = (String) colorCombo.getSelectedItem();
            String variante = (String) varianteCombo.getSelectedItem();
            int stock_disponible = (int) stock_disponibleSpinner.getValue();
            double precio = (double) precioSpinner.getValue();

            // Crear una clave compuesta para la combinación modelo-variante
            String modeloVarianteKey = modelo + "_" + variante;

            // Guardar datos en la base de datos
            boolean success = true;
            if (dbConnection.success()) {
                try {
                    Connection conn = dbConnection.getConnection();

                    // Primero comprobar si esta combinación específica de modelo-variante existe
                    String checkVariantSql = "SELECT stock_disponible, precio FROM almacen WHERE modelo = ?";
                    PreparedStatement checkVariantStmt = conn.prepareStatement(checkVariantSql);
                    checkVariantStmt.setString(1, modeloVarianteKey);
                    ResultSet rsVariant = checkVariantStmt.executeQuery();

                    if (rsVariant.next()) {
                        // Esta combinación específica de modelo-variante existe, actualizar su cantidad y precio
                        int currentQuantity = rsVariant.getInt("stock_disponible"); // Cambiar "cantidad" por "stock_disponible"
                        String updateSql = "UPDATE almacen SET stock_disponible = ?, precio = ? WHERE modelo = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setInt(1, currentQuantity + stock_disponible);
                        updateStmt.setDouble(2, precio);
                        updateStmt.setString(3, modeloVarianteKey);
                        updateStmt.executeUpdate();
                        updateStmt.close();
                    } else {
                        // Esta combinación específica de modelo-variante no existe todavía, comprobar si el modelo base existe
                        String checkModelSql = "SELECT stock_disponible, precio FROM almacen WHERE modelo = ?";
                        PreparedStatement checkModelStmt = conn.prepareStatement(checkModelSql);
                        checkModelStmt.setString(1, modelo);
                        ResultSet rsModel = checkModelStmt.executeQuery();

                        if (rsModel.next()) {
                            // El modelo base existe, crear una nueva entrada para esta variante específica
                            String insertSql = "INSERT INTO almacen (modelo, stock_disponible, precio) VALUES (?, ?, ?)";
                            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                            insertStmt.setString(1, modeloVarianteKey);
                            insertStmt.setInt(2, stock_disponible);
                            insertStmt.setDouble(3, precio);
                            insertStmt.executeUpdate();
                            insertStmt.close();
                        } else {
                            // Ni la variante específica ni el modelo base existen, insertar ambos
                            // Primero insertar el modelo base
                            String insertBaseSql = "INSERT INTO almacen (modelo, stock_disponible, precio) VALUES (?, ?, ?)";
                            PreparedStatement insertBaseStmt = conn.prepareStatement(insertBaseSql);
                            insertBaseStmt.setString(1, modelo);
                            insertBaseStmt.setInt(2, 0); // Sin cantidad para el modelo base
                            insertBaseStmt.setDouble(3, precio); // Usar el mismo precio inicialmente
                            insertBaseStmt.executeUpdate();
                            insertBaseStmt.close();

                            // Luego insertar la variante específica
                            String insertVariantSql = "INSERT INTO almacen (modelo, stock_disponible, precio) VALUES (?, ?, ?)";
                            PreparedStatement insertVariantStmt = conn.prepareStatement(insertVariantSql);
                            insertVariantStmt.setString(1, modeloVarianteKey);
                            insertVariantStmt.setInt(2, stock_disponible);
                            insertVariantStmt.setDouble(3, precio);
                            insertVariantStmt.executeUpdate();
                            insertVariantStmt.close();
                        }
                    }

                    rsVariant.close();
                    checkVariantStmt.close();
                } catch (SQLException ex) {
                    success = false;
                    JOptionPane.showMessageDialog(this,
                            "Error al actualizar el almacen: " + ex.getMessage(),
                            "Error de base de datos",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                success = false;
                JOptionPane.showMessageDialog(this,
                        "No se pudo conectar con la base de datos.",
                        "Error de conexión",
                        JOptionPane.WARNING_MESSAGE);
            }

            // Mostrar mensaje de confirmación si es exitoso
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Se han añadido " + stock_disponible + " unidades del modelo " + modelo +
                                " (" + color + ", " + variante + ") al almacén.",
                        "Operación completada",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        // Crear botón de cancelar
        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(e -> dialog.dispose());

        // Crear panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(anadirButton);
        buttonPanel.add(cancelarButton);

        // Crear y añadir panel de contenido
        JPanel contentPanel = createContentPanel(formPanel, buttonPanel);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    //Muestra un diálogo para editar un teléfono existente en el almacén
    private void mostrarDialogoEditarMovil(String modelo, String color, String variante) {
        JDialog dialog = createDialog("Editar Producto", 500, 400);
        JPanel formPanel = createFormPanel(6);

        // Crear campos del formulario con valores preseleccionados
        JComboBox<String> modeloCombo = new JComboBox<>(MODELOS);
        JComboBox<String> colorCombo = new JComboBox<>(COLORES);
        JComboBox<String> varianteCombo = new JComboBox<>(OPCIONES_RAM_ROM);
        JSpinner stockSpinner = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
        JSpinner precioSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 10000.00, 0.01));
        JSpinner.NumberEditor precioEditor = new JSpinner.NumberEditor(precioSpinner, "0.00");
        precioSpinner.setEditor(precioEditor);
        JLabel stockActualLabel = new JLabel("0");

        // Configurar valores iniciales
        modeloCombo.setSelectedItem(modelo);
        colorCombo.setSelectedItem(color);
        varianteCombo.setSelectedItem(variante);
        modeloCombo.setEnabled(false);
        colorCombo.setEnabled(false);
        varianteCombo.setEnabled(false);

        // Añadir campos al formulario
        addFormField(formPanel, "Modelo:", modeloCombo);
        addFormField(formPanel, "Color:", colorCombo);
        addFormField(formPanel, "Variante:", varianteCombo);
        addFormField(formPanel, "Modificar stock:", stockSpinner);
        addFormField(formPanel, "Nuevo precio (€):", precioSpinner);
        addFormField(formPanel, "Stock actual:", stockActualLabel);

        // Crear clave para búsqueda en base de datos
        String modeloVariante = modelo + "_" + variante;

        // Cargar datos actuales
        if (dbConnection.success()) {
            try {
                Connection conn = dbConnection.getConnection();
                String sql = "SELECT stock_disponible, precio FROM almacen WHERE modelo = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, modeloVariante);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int stockActual = rs.getInt("stock_disponible");
                    double precioActual = rs.getDouble("precio");
                    stockActualLabel.setText(String.valueOf(stockActual));
                    precioSpinner.setValue(precioActual);
                }

                rs.close();
                stmt.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error al cargar los datos del producto: " + ex.getMessage(),
                        "Error de base de datos",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        // Botón Guardar
        JButton guardarButton = new JButton("Guardar");
        guardarButton.addActionListener(e -> {
            int cambioStock = (int) stockSpinner.getValue();
            double nuevoPrecio = (double) precioSpinner.getValue();

            if (dbConnection.success()) {
                try {
                    Connection conn = dbConnection.getConnection();

                    // Actualizar stock y precio
                    String updateSql = "UPDATE almacen SET stock_disponible = stock_disponible + ?, precio = ? WHERE modelo = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setInt(1, cambioStock);
                    updateStmt.setDouble(2, nuevoPrecio);
                    updateStmt.setString(3, modeloVariante);

                    int rowsAffected = updateStmt.executeUpdate();
                    updateStmt.close();

                    if (rowsAffected > 0) {
                        String mensaje = "Producto actualizado: \n";
                        if (cambioStock != 0) {
                            mensaje += "Stock " + (cambioStock > 0 ? "aumentado" : "reducido") + " en " + Math.abs(cambioStock) + " unidades\n";
                        }
                        mensaje += "Precio actualizado a " + String.format("%.2f €", nuevoPrecio);

                        JOptionPane.showMessageDialog(dialog,
                                mensaje,
                                "Actualización exitosa",
                                JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                                "No se encontró el producto para actualizar",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Error al actualizar el producto: " + ex.getMessage(),
                            "Error de base de datos",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Botón Cancelar
        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(e -> dialog.dispose());

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(guardarButton);
        buttonPanel.add(cancelarButton);

        // Añadir paneles al diálogo
        JPanel contentPanel = createContentPanel(formPanel, buttonPanel);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    //Muestra un cuadro de diálogo para eliminar un teléfono del almacén por ID

    private void mostrarDialogoQuitarMovil() {
        JDialog dialog = createDialog("Eliminar Stock", 400, 250);

        // Crear un panel de formulario con menos campos
        JPanel formPanel = createFormPanel(2);

        // Creamos campos
        JTextField idField = new JTextField(10);
        JSpinner stock_disponibleSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        // Agregar campos al panel de formularios
        addFormField(formPanel, "ID del Producto:", idField);
        addFormField(formPanel, "Cantidad a eliminar:", stock_disponibleSpinner);

        // Creamos boton con la funcion de eliminar
        JButton quitarButton = new JButton("Eliminar");
        quitarButton.addActionListener(e -> {
            // Validar entrada de ID
            int productId;
            try {
                productId = Integer.parseInt(idField.getText().trim());
                if (productId <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Por favor, introduzca un ID de producto válido (número entero positivo).",
                        "ID inválido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int stock_disponible = (int) stock_disponibleSpinner.getValue();

            // Obtenemos el modelo del ID del producto
            String modelo = getModeloFromProductId(productId);
            if (modelo == null) {
                JOptionPane.showMessageDialog(dialog,
                        "No se encontró ningún producto con el ID " + productId + ".",
                        "Producto no encontrado",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }


        });

        // Crear botón de cancelación
        JButton cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(e -> dialog.dispose());

        // Creamos botones del panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(quitarButton);
        buttonPanel.add(cancelarButton);

        // Crear y agregar panel de contenido
        JPanel contentPanel = createContentPanel(formPanel, buttonPanel);
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    //Obtiene el nombre del modelo de un ID de producto
    private String getModeloFromProductId(int productId) {
        // Dado que los ID de producto se generan sobre la marcha en la UI, necesitamos simular
        // la misma lógica para mapear un ID de producto de nuevo a su modelo
        if (productId <= 0) {
            return null;
        }

        try {
            Connection conn = dbConnection.getConnection();
            String sql = "SELECT modelo FROM almacen";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            int currentId = 1;

            while (rs.next()) {
                String modelo = rs.getString("modelo");

                // Para cada modelo, generamos COLORES.length * OPCIONES_RAM_ROM.length IDs
                int idsForThisModel = COLORES.length * OPCIONES_RAM_ROM.length;

                // Comprueba si el productId cae dentro del rango para este modelo
                if (productId >= currentId && productId < currentId + idsForThisModel) {
                    rs.close();
                    stmt.close();
                    return modelo;
                }

                currentId += idsForThisModel;
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error al buscar el modelo: " + e.getMessage());
        }

        return null;
    }

    //Elimina un producto de la base de datos
    private boolean eliminarProducto(String modelo, String variante) {
        // Crea una clave compuesta para la combinación modelo variable
        String modeloVarianteKey = modelo + "_" + variante;

        // Usamos executeDbOperation como metodo auxiliar
        Boolean result = executeDbOperation(conn -> {
            // Comprobamos que la variante del modelo existe

            String checkVariantSql = "SELECT id_producto FROM almacen WHERE modelo = ?";
            try (PreparedStatement checkVariantStmt = conn.prepareStatement(checkVariantSql);) {
                checkVariantStmt.setString(1, modeloVarianteKey);

                try (ResultSet rsVariant = checkVariantStmt.executeQuery()) {
                    if (rsVariant.next()) {

                        String deleteSql = "DELETE FROM almacen WHERE modelo = ?";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                            deleteStmt.setString(1, modeloVarianteKey);
                            deleteStmt.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(this,
                                "El producto " + modelo + " (" + variante + ") ha sido eliminado correctamente.",
                                "Producto eliminado",
                                JOptionPane.INFORMATION_MESSAGE);

                        return true;
                    } else {
                        // Si esta variante del modelo no existe, mandar mensaje de error
                        JOptionPane.showMessageDialog(this,
                                "No se encontró el producto " + modelo + " (" + variante + ") en la base de datos.",
                                "Producto no encontrado",
                                JOptionPane.WARNING_MESSAGE);

                        return false;
                    }
                }
            }
        }, "Error al eliminar el producto", this);

        // Si la operacion falla
        return result != null && result;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     */
    public JComponent obtenerElementoRaiz() {
        return mainPanel;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     */
    public String getUIClassID() {
        return "PrincipalUI";
    }

}
