package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;

public class ParkingSpotDAO {
    private static final Logger logger = LogManager.getLogger("ParkingSpotDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public int getNextAvailableSlot(ParkingType parkingType){
        Connection con = null;
        int result=-1;
        try {
            System.out.println("Connecting to database...");
            con = dataBaseConfig.getConnection();
            System.out.println("Connection successful!");
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
            ps.setString(1, parkingType.name());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt(1);;
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        }catch (Exception ex){
            logger.error("Error fetching next available slot",ex);
        }finally {
            dataBaseConfig.closeConnection(con);
        }
        return result;
    }
    public boolean updateParking(ParkingSpot parkingSpot){
        //update the availability fo that parking slot
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
            ps.setBoolean(1, parkingSpot.isAvailable());
            ps.setInt(2, parkingSpot.getId());
            int updateRowCount = ps.executeUpdate();
            dataBaseConfig.closePreparedStatement(ps);
            return (updateRowCount == 1);
        }catch (Exception ex){
            logger.error("Error updating parking info",ex);
            return false;
        }finally {
            dataBaseConfig.closeConnection(con);
        }
    }

    public boolean saveParking(ParkingSpot parkingSpot){
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_PARKING);
            ps.setString(1, parkingSpot.getParkingType().name());
            ps.setBoolean(2, parkingSpot.isAvailable());

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


    public ParkingSpot getParkingSpot(int parkingSpotId) {
        Connection con = null;
        ParkingSpot parkingSpot = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM parking WHERE ID = ?");
            ps.setInt(1, parkingSpotId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ParkingType parkingType = ParkingType.valueOf(rs.getString("PARKING_TYPE"));
                boolean isAvailable = rs.getBoolean("AVAILABLE");
                parkingSpot = new ParkingSpot(parkingSpotId, parkingType, isAvailable);
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        } catch (Exception ex) {
            logger.error("Error fetching parking spot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return parkingSpot;
    }
}
