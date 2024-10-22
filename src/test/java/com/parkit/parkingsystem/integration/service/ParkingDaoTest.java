package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import junit.framework.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParkingDaoTest {

    private ParkingSpotDAO parkingSpotDAO;

    @BeforeEach
    void setUp(){
        parkingSpotDAO = new ParkingSpotDAO();

    }




    @Test
    void shouldNextAvaibleSlot(){
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setParkingType(ParkingType.CAR);
        parkingSpot.setAvailable(true);
        parkingSpotDAO.saveParking(parkingSpot);
        int value = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        Assert.assertEquals(value, 1);

    }






}
