package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import java.time.LocalDateTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
public class DataBasePrepareService {

    DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    public void clearDataBaseEntries(){
        Connection connection = null;
        try{
            connection = dataBaseTestConfig.getConnection();

            //set parking entries to available
            connection.prepareStatement("UPDATE parking_spot SET available = true").execute();

            //clear ticket entries;
            connection.prepareStatement("DELETE FROM ticket").execute();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }
    public void insertACar(int parkingNumber, String vehicleRegNumber, LocalDateTime inTime) {
        try (Connection con = dataBaseTestConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) VALUES (?, ?, ?, ?, ?)")) {
            ps.setInt(1, parkingNumber);
            ps.setString(2, vehicleRegNumber);
            ps.setDouble(3, 0.0); // Prix initial à 0
            ps.setTimestamp(4, Timestamp.valueOf(inTime));
            ps.setNull(5, Types.TIMESTAMP); // OUT_TIME est nul à l'entrée
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void updateACarExit(int ticketId, double price, LocalDateTime outTime) {
        try (Connection con = dataBaseTestConfig.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE ticket SET PRICE = ?, OUT_TIME = ? WHERE ID = ?")) {
            ps.setDouble(1, price);
            ps.setTimestamp(2, Timestamp.valueOf(outTime));
            ps.setInt(3, ticketId);
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
