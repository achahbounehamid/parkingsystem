package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.constants.ParkingType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class TicketDAOTest {

    private TicketDAO ticketDAO = new TicketDAO();
    private String vehicleRegNumber = "AB-123-CD";

    @BeforeEach
    public void setUp() {
        // Effacer les entrées de la base de données avant chaque test pour éviter les conflits
        DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();

        // Insérer des tickets pour préparer le test
        for (int i = 0; i < 3; i++) {
            Ticket ticket = new Ticket();
            ticket.setVehicleRegNumber(vehicleRegNumber);
            ticket.setPrice(0.0);
            ticket.setInTime(new Date(System.currentTimeMillis() - (i * 60 * 60 * 1000))); // Différentes heures pour simuler plusieurs entrées
            ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
            boolean result = ticketDAO.saveTicket(ticket);
             assertTrue(result, "Le ticket doit être inséré avec succès dans la base de données.");
        }
    }

    @Test
    public void testGetNbTicketWithExistingVehicle() {
        // Appeler la méthode pour obtenir le nombre de tickets pour ce véhicule
        int nbTickets = ticketDAO.getNbTicket(vehicleRegNumber);

        // Vérifier que le nombre de tickets correspond à la valeur attendue
        assertEquals(3, nbTickets, "Le nombre de tickets pour ce véhicule doit être 3.");
    }

    @Test
    public void testGetNbTicketWithNoTickets() {
        // Simuler le cas d'un véhicule sans aucun ticket enregistré
        String newVehicleRegNumber = "ZZ-999-XX";

        // Appeler la méthode pour obtenir le nombre de tickets pour ce véhicule
        int nbTickets = ticketDAO.getNbTicket(newVehicleRegNumber);

        // Dans ce cas, on s'attend à 0 tickets
        assertEquals(0, nbTickets, "Le nombre de tickets pour ce véhicule doit être 0.");
    }
}
