/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gemit
 */
public class BD {
    
    // SELECT y regresa cada fila como arreglo de textos
    public static List<Object[]> consultar(String sql, Object... params) throws SQLException {
        try (Connection c = ConexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                int n = rs.getMetaData().getColumnCount();
                List<Object[]> filas = new ArrayList<>();
                while (rs.next()) {
                    Object[] f = new Object[n];
                    for (int i = 0; i < n; i++) {
                        String s = rs.getString(i + 1);
                        f[i] = (s == null) ? "" : s;
                    }
                    filas.add(f);
                }
                return filas;
            }
        }
    }
    
    // Ejecuta INSERT / UPDATE / DELETE.
    public static int ejecutar(String sql, Object... params) throws SQLException {
        try (Connection c = ConexionBD.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, params);
            return ps.executeUpdate();
        }
    }
    
    
    //SELECT de dos columnas (id, texto) para llenar combos
    public static List<Item> items(String sql, Object... params) throws SQLException {
        List<Item> lista = new ArrayList<>();
        for (Object[] f : consultar(sql, params)) {
            lista.add(new Item(Integer.parseInt((String) f[0]), (String) f[1]));
        }
        return lista;
    }

    
    
    

    private static void asignar(PreparedStatement ps, Object[] params) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
