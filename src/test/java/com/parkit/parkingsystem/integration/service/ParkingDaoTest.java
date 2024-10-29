package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import junit.framework.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParkingDaoTest {

    private ParkingSpotDAO parkingSpotDAO;
    private DataBasePrepareService dataBasePrepareService;

    @BeforeEach
    void setUp(){
        parkingSpotDAO = new ParkingSpotDAO();
        dataBasePrepareService = new DataBasePrepareService();
    }

    @Test
    void shouldNextAvaibleSlot() {
// Réinitialisation de la base de données pour éviter les conflits avec des données existantes
        dataBasePrepareService.clearDataBaseEntries();
        // Création d'un objet ParkingSpot
        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setParkingType(ParkingType.CAR);
        parkingSpot.setAvailable(true);

        // Enregistrement de la place de parking dans la base de données
        parkingSpotDAO.saveParking(parkingSpot);

        // Récupération de la prochaine place de parking disponible
        int value = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);

        // Vérification que l'ID de la place de parking retourné est bien 1
        Assert.assertEquals(value, 1);
    }

}
