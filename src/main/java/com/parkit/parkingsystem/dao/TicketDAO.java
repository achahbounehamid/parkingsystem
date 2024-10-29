package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.sql.*;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public boolean saveTicket(Ticket ticket){
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)

            ps.setInt(1, ticket.getId());
            //ps.setInt(1,ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
            ps.setTimestamp(5, (ticket.getOutTime() == null)?null: (new Timestamp(ticket.getOutTime().getTime())) );
//            return ps.execute();
            int rowsAffected = ps.executeUpdate();  // Use executeUpdate() to get the number of rows affected

            if (rowsAffected > 0) {
                logger.info("Ticket inserted successfully");
                return true;
            } else {
                logger.error("Failed to insert ticket: No rows affected");
                return false;
            }

        } catch (SQLException | ClassNotFoundException ex) {
            logger.error("Error saving ticket info", ex);
            return false;
        }
    }

    public Ticket getTicket(String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            ps.setString(1,  vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                ticket = new Ticket();
//                logger.info("Ticket trouvé avec ID: " + rs.getInt("id"));
                ticket.setId(rs.getInt("id"));
                ticket.setVehicleRegNumber(rs.getString("vehicle_reg_number"));
                ticket.setPrice(rs.getDouble("price"));
                ticket.setOutTime(rs.getDate("out_time"));
                ticket.setInTime(rs.getDate("in_time"));
                // Créer et associer le ParkingSpot au ticket
                ParkingSpot parkingSpot = new ParkingSpot(
                        rs.getInt("parking_number"),
                        ParkingType.valueOf(rs.getString("PARKING_TYPE")),
                        rs.getBoolean("AVAILABLE")
                );
                ticket.setParkingSpot(parkingSpot);
            }

            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);

        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return ticket;
    }

    public boolean updateTicket(Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
            ps.setInt(3,ticket.getId());
            ps.execute();
            return true;
        }catch (Exception ex){
            logger.error("Error saving ticket info",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }
    /**
     * Méthode pour compter combien de tickets sont enregistrés pour un véhicule.
     *
     * @param vehicleRegNumber numéro d'immatriculation du véhicule
     * @return le nombre de tickets associés au véhicule
     */
    public int getNbTicket(String vehicleRegNumber) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int nbTickets = 0;

        try {
            con = dataBaseConfig.getConnection();
            String query = "SELECT COUNT(*) FROM ticket WHERE VEHICLE_REG_NUMBER = ?";
            ps = con.prepareStatement(query);
            ps.setString(1, vehicleRegNumber);
            rs = ps.executeQuery();

            if (rs.next()) {
                nbTickets = rs.getInt(1); // Récupérer le nombre de tickets
            }
        } catch (Exception ex) {
            logger.error("Erreur lors de la récupération du nombre de tickets pour le véhicule : " + vehicleRegNumber, ex);
        } finally {
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        }

        return nbTickets;
    }

}
