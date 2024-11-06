package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.Date;

public class TicketDAOTest {

    private TicketDAO ticketDAO;
    private String vehicleRegNumber = "AB-123-CD";

    @BeforeEach
    public void setUp() {
        // Effacer les entrées de la base de données avant chaque test pour éviter les conflits
        DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();
        ticketDAO = new TicketDAO();
    }
    @Test
    void insertTicket(){
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(0.0);
        ticket.setInTime(Date.from(Instant.now()));
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
        boolean result = ticketDAO.saveTicket(ticket);
        assertTrue(result, "Le ticket doit être inséré avec succès dans la base de données.");
        Ticket fetchedTicket = ticketDAO.getTicket(vehicleRegNumber);
        assertNotNull(fetchedTicket, "Le ticket inséré devrait être récupérable de la base de données.");
    }
    @Test
    public void testGetNbTicketWithExistingVehicle() {
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber("ABCDE");
        ticket.setPrice(0.0);
        ticket.setInTime(Date.from(Instant.now()));
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
        boolean result = ticketDAO.saveTicket(ticket);
        assertTrue(result, "Le ticket doit être inséré avec succès dans la base de données.");
        // Appeler la méthode pour obtenir le nombre de tickets pour ce véhicule
        int nbTickets = ticketDAO.getNbTicket("ABCDE");

       // Vérifier que le nombre de tickets correspond à la valeur attendue
        assertEquals(1, nbTickets, "Le nombre de tickets pour ce véhicule doit être 1.");
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
    @Test
    public void testUpdateTicketDao() {
        Ticket ticket = new Ticket();
        ticket.setVehicleRegNumber(vehicleRegNumber);
        ticket.setPrice(0.0);
        ticket.setInTime(Date.from(Instant.now()));
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, true));
        boolean result = ticketDAO.saveTicket(ticket);
        assertTrue(result, "Le ticket doit être inséré avec succès dans la base de données.");
        Ticket fetchedTicket = ticketDAO.getTicket(vehicleRegNumber);
        fetchedTicket.setOutTime(new Date());
        Assertions.assertTrue(ticketDAO.updateTicket(fetchedTicket));
    }
}
