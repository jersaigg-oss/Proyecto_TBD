/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Conexion.BD;
import Conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Gemit
 */
public class AlquilerDAO {
    
     private static final int MAX_ALQUILERES_ACTIVOS = 10;

    public List<Object[]> listar() throws SQLException {
        return BD.consultar("SELECT a.idAlquiler, c.nombre || ' ' || c.apellido, p.titulo, a.idPelicula, "
                + "TO_CHAR(a.fecha_Alquiler, 'DD/MM/YYYY HH24:MI'), "
                + "TO_CHAR(a.fecha_Devolucion, 'DD/MM/YYYY HH24:MI') "
                + "FROM Alquiler a "
                + "JOIN Cliente c ON c.idCliente = a.idCliente "
                + "JOIN Copia_Pelicula cp ON cp.idPelicula = a.idPelicula "
                + "JOIN Pelicula p ON p.idCatalogo = cp.idCatalogo "
                + "ORDER BY a.idAlquiler DESC");
    }
    
    public void alquilar(int idCliente, int idCopia) throws SQLException {
        try (Connection con = ConexionBD.get()) {
            con.setAutoCommit(false);
            try {
                // 1) Regla del caso de estudio: máximo 10 películas simultáneas por cliente
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT COUNT(*) FROM Alquiler WHERE idCliente = ? AND fecha_Devolucion IS NULL")) {
                    ps.setInt(1, idCliente);
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) >= MAX_ALQUILERES_ACTIVOS) {
                            throw new SQLException("El cliente ya tiene " + MAX_ALQUILERES_ACTIVOS
                                    + " alquileres activos.");
                        }
                    }
                }

                // 2) La copia debe estar disponible (FOR UPDATE la bloquea mientras se alquila)
                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT estado FROM Copia_Pelicula WHERE idPelicula = ? FOR UPDATE")) {
                    ps.setInt(1, idCopia);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("La copia no existe.");
                        }
                        if (!"disponible".equals(rs.getString(1))) {
                            throw new SQLException("La copia no está disponible.");
                        }
                    }
                }

                // 3) Registrar el alquiler
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO Alquiler (idCliente, idPelicula) VALUES (?, ?)")) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, idCopia);
                    ps.executeUpdate();
                }

                // 4) Marcar la copia como rentada
                cambiarEstadoCopia(con, idCopia, "rentada");
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
    
    
    
    
}
