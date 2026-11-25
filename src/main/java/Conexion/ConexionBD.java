/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Gemit
 */
public class ConexionBD {
    private static final String URL = "jdbc:oracle:thin:@//localhost:1521/XEPDB1";
    private static final String USUARIO = "videoclub";
    private static final String CLAVE = "videoclub123";

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
    
}
