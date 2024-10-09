package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );// heure de stationnement
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(1.5, ticket.getPrice());//Vérifiez le prix pour 1 heure de stationnement
    }

    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(1.0, ticket.getPrice());
    }

    @Test
    public void calculateFareUnkownType(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes de stationnement
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0.75, ticket.getPrice());// vérifier le prix pour 45 minutes de stationnement
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000));//45 minutes de stationnement
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( 1.125, ticket.getPrice());// vérifier le prix pour 45 minutes de stationnement
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  24 * 60 * 60 * 1000));//Une durée de stationnement de 24 heures devrait donner 24 * tarif de stationnement par heure
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals( 36.0, ticket.getPrice());// vérifier le prix pour 24 minutes de stationnement
    }
    @Test
    public void calculateFareCarWithLessThan30minutesParkingTime() {
        // Arrange : création d'un ticket pour une voiture stationnée 25 minutes
        Date inTime = new Date(System.currentTimeMillis() - (25* 60 * 1000)); // Entrée il y a 25 minutes
        Date outTime = new Date(); // Sortie maintenant
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
System.out.println(inTime);
        System.out.println(outTime);
        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act : appel de la méthode calculateFare
        fareCalculatorService.calculateFare(ticket);

        // Assert : vérification que le prix est 0
        assertEquals(0, ticket.getPrice(), "Le stationnement de moins de 30 minutes doit être gratuit pour une voiture.");
    }


    @Test
    public void calculateFareBikeWithLessThan30minutesParkingTime() {
        // Arrange : création d'un ticket pour une moto stationnée 20 minutes
        Date inTime = new Date(System.currentTimeMillis() - (20  * 60 * 1000)); // Entrée il y a 20 minutes
        Date outTime = new Date(); // Sortie maintenant
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act : appel de la méthode calculateFare
        fareCalculatorService.calculateFare(ticket);

        // Assert : vérification que le prix est 0
        assertEquals(0, ticket.getPrice(), "Le stationnement de moins de 30 minutes doit être gratuit pour une moto.");
    }
    @Test
    public void calculateFareCarWithDiscount() {
        // Arrange : création d'un ticket pour une voiture stationnée 2 heures avec remise
        String vehicleRegNumber = "AB-123-CD";
        Date inTime = new Date(System.currentTimeMillis() - (2 * 60 * 60 * 1000)); // Entrée il y a 2 heures
        Date outTime = new Date(); // Sortie maintenant
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);

        // Act : appel de la méthode calculateFare avec remise
        fareCalculatorService.calculateFare(ticket, true);

        // Appliquer la remise de 5%
        double expectedPrice = (2 * Fare.CAR_RATE_PER_HOUR) * 0.95; // Tarif avec remise

        // Assert : vérification que le prix est correct avec la remise de 5%
        assertEquals(expectedPrice, ticket.getPrice(), "Le prix doit être de 2.85€ avec une remise de 5% pour 2 heures de stationnement.");
    }

    @Test
    public void calculateFareBikeWithDiscount() {
        // Arrange : création d'un ticket pour une moto stationnée 1 heure avec remise
        String vehicleRegNumber = "AB-456-EF";
        Date inTime = new Date(System.currentTimeMillis() - (1 * 60 * 60 * 1000)); // Entrée il y a 1 heure
        Date outTime = new Date(); // Sortie maintenant
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);

        // Act : appel de la méthode calculateFare avec remise
        fareCalculatorService.calculateFare(ticket, true); // Passer true pour appliquer la remise

        // Appliquer la remise de 5%
        double expectedPrice = (1 * Fare.BIKE_RATE_PER_HOUR) * 0.95; // Tarif avec remise

        // Assert : vérification que le prix est correct avec la remise de 5%
        assertEquals(expectedPrice, ticket.getPrice(), "Le prix doit être de 0.95€ avec une remise de 5% pour 1 heure de stationnement.");
    }

}
