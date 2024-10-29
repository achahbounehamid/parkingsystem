
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;


import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.parkit.parkingsystem.util.InputReaderUtil;

import java.time.LocalDateTime;
import java.util.Date;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {
    private static final double DISCOUNT_RATE = 0.95;

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    private final String vehicleRegNumber = "ABCDEF";
    @BeforeAll
    static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }


@Test
public void testParkingACar() throws Exception {
    ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    parkingService.processIncomingVehicle();
//TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
    // Récupérer le ticket de la base de données
    Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);

    // Vérifier que le ticket est bien enregistré dans la base de données
    assertNotNull(ticket);
    assertEquals(vehicleRegNumber, ticket.getVehicleRegNumber());

    assertNotNull(ticket.getInTime());
    assertNull(ticket.getOutTime());
    assertEquals(0, ticket.getPrice());
    ParkingSpot parkingSpot =  parkingService.getNextParkingNumberIfAvailable();
    assertEquals(parkingSpot.getParkingType(), ParkingType.CAR);
    assertTrue(parkingSpot.isAvailable());

}



    @Test
    public void testParkingLotExit() throws Exception {


        //TODO save ticket
        Ticket ticket = new Ticket();
        ticket.setOutTime(new Date());
        ticket.setPrice(20.00);
        ticket.setVehicleRegNumber("ABCDE");
        ticket.setInTime(new Date());
        ticketDAO.saveTicket(ticket);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database

         // Vérifier le tarif et l'heure de sortie
        Ticket getTicket = ticketDAO.getTicket("ABCDEF");
        assertNotNull(getTicket);
        assertNotNull(getTicket.getOutTime());
        assertTrue(getTicket.getPrice() > 0);

        // Vérifier que la place de parking est de nouveau disponible
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertNotNull(parkingSpot);
        assertTrue(parkingSpot.isAvailable()); //disponible après la sortie du véhicule");
    }
    @Test
    public void testParkingLotExitRecurringUser() throws Exception {
        // Crée un ticket pour simuler un utilisateur récurrent
        Ticket recurringTicket = new Ticket();
        recurringTicket.setVehicleRegNumber(vehicleRegNumber);
        recurringTicket.setInTime(new Date());
        recurringTicket.setOutTime(null);
        recurringTicket.setPrice(0.0);
        ticketDAO.saveTicket(recurringTicket);

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // Entrée du véhicule
        parkingService.processIncomingVehicle();

        // Sortie du véhicule avec remise
        parkingService.processExitingVehicle();

        // Pause pour permettre la synchronisation avec la base de données
        Thread.sleep(200);

        // Vérifier que la place de parking est à nouveau disponible
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertNotNull(parkingSpot);
        assertTrue(parkingSpot.isAvailable()); // La place devrait être disponible
    }



}