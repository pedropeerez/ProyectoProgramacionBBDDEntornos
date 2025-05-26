package PixelDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexionBBDD {

    private Connection mconnection;

    public boolean success() {
        return this.mconnection != null;
    }

    public conexionBBDD(String host, String port, String user, String pass, String bd) {
        String thost = host.isEmpty() ? "localhost" : host;
        String tport = port.isEmpty() ? "3306" : port;
        String tuser = user.isEmpty() ? "root" : user;
        String tpass = pass; // <-- Aquí usas la contraseña recibida
        String tbd = bd.isEmpty() ? "pixeldb" : bd;
        String turl = "jdbc:mysql://" + thost + ":" + tport + "/" + tbd + "?useSSL=false&serverTimezone=UTC";
        try {
            // Cargar el driver JDBC (opcional con JDBC 4.0+ pero recomendado)
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.mconnection = DriverManager.getConnection(turl, tuser, tpass);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
            this.mconnection = null;
        }
    }

    public Connection getConnection() {
        return this.mconnection;
    }

    /**
     * Closes the database connection.
     * This method should be called when the connection is no longer needed
     * to free up database resources.
     */
    public void closeConnection() {
        if (this.mconnection != null) {
            try {
                this.mconnection.close();
                this.mconnection = null;
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
