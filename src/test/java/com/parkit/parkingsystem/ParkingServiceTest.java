package com.parkit.parkingsystem;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import com.parkit.parkingsystem.util.InputReaderUtil;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;




@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    @InjectMocks
    private  ParkingService parkingService;
    @Mock
    private  InputReaderUtil inputReaderUtil;
    @Mock
    private  ParkingSpotDAO parkingSpotDAO;
    @Mock
    private  TicketDAO ticketDAO;


@Test
public void testProcessIncomingVehicle() throws Exception {

    // Initialisation correcte de ParkingSpot
    ParkingSpot expectedParkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

    // Mock les appels nécessaires
    when(inputReaderUtil.readSelection()).thenReturn(1);
    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABC123");
    when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

    // S'assurer que `updateParking()` est stubbed avec un objet non null
    when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

    // Appel de la méthode à tester
    parkingService.processIncomingVehicle();

    // Vérifications des appels aux différentes méthodes
    verify(inputReaderUtil).readVehicleRegistrationNumber();
    verify(parkingSpotDAO).getNextAvailableSlot(any(ParkingType.class));

    // Utilisation d'ArgumentCaptor pour vérifier l'objet `ParkingSpot` passé
    ArgumentCaptor<ParkingSpot> parkingSpotCaptor = ArgumentCaptor.forClass(ParkingSpot.class);
    verify(parkingSpotDAO).updateParking(parkingSpotCaptor.capture());
    ParkingSpot capturedParkingSpot = parkingSpotCaptor.getValue();

    // Vérification que l'objet `ParkingSpot` passé est celui attendu
    assertNotNull(capturedParkingSpot);
    assertEquals(expectedParkingSpot.getId(), capturedParkingSpot.getId());
    assertEquals(expectedParkingSpot.getParkingType(), capturedParkingSpot.getParkingType());
    assertEquals(expectedParkingSpot.isAvailable(), capturedParkingSpot.isAvailable());

    // Vérification des autres appels
    verify(ticketDAO).saveTicket(any(Ticket.class));
    verify(ticketDAO).getNbTicket(anyString());
}


  @Test
  void processIncomingVehiculeTest(){
      when(inputReaderUtil.readSelection()).thenReturn(1);
      when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1);
      when(ticketDAO.getNbTicket(any())).thenReturn(1);
      when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);
      parkingService.processIncomingVehicle();
      verify(parkingSpotDAO, times(1)).updateParking(any(ParkingSpot.class));
      verify(ticketDAO, times(1)).saveTicket(any());
  }


  @Test
  void processExitingVehicleTest() throws Exception {

    when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("babar");

     Ticket ticket = new Ticket();
     ticket.setVehicleRegNumber("BMW");
     ParkingSpot parkingSpot = new ParkingSpot();
     parkingSpot.setParkingType(ParkingType.CAR);
     ticket.setParkingSpot(parkingSpot);

     // why not use LocalDateTime ??
      Date date = new Date();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date); // Mettre la date actuelle dans le calendrier
      calendar.add(Calendar.DAY_OF_MONTH, -1); // Retirer un jour
      ticket.setInTime(calendar.getTime());

      Date dateplus = new Date();
      Calendar calendarPlus = Calendar.getInstance();
      calendarPlus.setTime(dateplus); // Mettre la date actuelle dans le calendrier
      calendarPlus.add(Calendar.DAY_OF_MONTH, +1); // ajouter un jour
      ticket.setOutTime(calendarPlus.getTime());


      FareCalculatorService fareCalculatorService = new FareCalculatorService();
      fareCalculatorService.calculateFare(ticket, true);


       when(ticketDAO.getTicket(any())).thenReturn(ticket);

       when(ticketDAO.getNbTicket(any())).thenReturn(5);

      when(ticketDAO.updateTicket(any())).thenReturn(true);
      when(parkingSpotDAO.updateParking(any())).thenReturn(true);

      parkingService.processExitingVehicle();

      verify(ticketDAO, times(1)).updateTicket(any());
      verify(ticketDAO, times(1)).getTicket(any());
      verify(ticketDAO, times(1)).getNbTicket(any());

  }


    @Test
    void getNextParkingNumberIfAvailableTest() throws Exception {

        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);
        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertEquals(parkingSpot.getParkingType(), ParkingType.CAR);
        assertTrue(parkingSpot.isAvailable());
    }


    @Test
    void throwParkingNumberIfAvailableTest_noAvailableSlots() {
         assertThrows(Exception.class, () -> parkingService.getNextParkingNumberIfAvailable());

    }






}


