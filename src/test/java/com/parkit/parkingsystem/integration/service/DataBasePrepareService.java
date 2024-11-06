package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import java.sql.*;

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
            // Reset auto-increment for parking_spot table
            connection.prepareStatement("ALTER TABLE parking_spot AUTO_INCREMENT = 1").execute();

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }
}
