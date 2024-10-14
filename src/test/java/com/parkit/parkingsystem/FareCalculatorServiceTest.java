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
    private static final double DISCOUNT_RATE = 0.95;// Constante pour le taux de remise
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
        // Création d'un ticket pour une voiture stationnée pendant 1 heure
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );// heure de stationnement
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Calcul du tarif sans remise
        fareCalculatorService.calculateFare(ticket);

        // Vérification que le tarif est bien de 1.5 € (tarif plein pour 1 heure)
        assertEquals(1.5, ticket.getPrice(), 0.001);
    }

    // Test pour calculer le tarif pour une moto (sans remise)
    @Test
    public void calculateFareBike(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );// 1 heure de stationnement
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Calcul du tarif sans remise
        fareCalculatorService.calculateFare(ticket);

        // Vérification que le tarif est bien de 1.0 € (tarif plein pour 1 heure)
        assertEquals(1.0, ticket.getPrice(), 0.001);
    }
    // Ajout du test pour une voiture stationnée pendant 30 minutes et 1 seconde (cas limite)
    @Test
    public void calculateFareCarWithJustAbove30MinutesParkingTime() {
        // Test pour une voiture stationnée pendant 30 minutes et 1 seconde
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000 + 1000)); // 30 minutes et 1 seconde
        Date outTime = new Date();
        outTime.setTime(inTime.getTime() + (31 * 60 * 1000)); // 31 minutes
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        // Le tarif pour 31 minutes  doit être calculé sur la base du tarif horaire
        double expectedPrice = (31.0 / 60.0) * Fare.CAR_RATE_PER_HOUR;// Calcul pour 31 minutes
        assertEquals(expectedPrice, ticket.getPrice(), 0.001, "Le prix doit être calculé correctement pour plus de 30 minutes.");
    }
    // Ajout du test pour une moto stationnée pendant 30 minutes et 1 seconde (cas limite)
    @Test
    public void calculateFareBikeWithJustAbove30MinutesParkingTime() {
        // Test pour une moto stationnée pendant 30 minutes et 1 seconde
        Date inTime = new Date();
        inTime.setTime(System.currentTimeMillis() - (30 * 60 * 1000 + 1000)); // 30 minutes et 1 seconde
        Date outTime = new Date();
        outTime.setTime(inTime.getTime() + (31 * 60 * 1000)); // 31 minutes
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        // Le tarif pour 31 minutes doit être calculé sur la base du tarif horaire
        double expectedPrice = (31.0 / 60.0) * Fare.BIKE_RATE_PER_HOUR;// Calcul pour 31 minutes
        assertEquals(expectedPrice, ticket.getPrice(), 0.001, "Le prix doit être calculé correctement pour plus de 30 minutes.");
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
        assertEquals(0.75, ticket.getPrice(), 0.001);// vérifier le prix pour 45 minutes de stationnement
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
        assertEquals( 1.125, ticket.getPrice(), 0.001);// vérifier le prix pour 45 minutes de stationnement
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
        assertEquals( 36.0, ticket.getPrice(), 0.001);// vérifier le prix pour 24 minutes de stationnement
    }
    @Test
    public void calculateFareCarWithLessThan30minutesParkingTime() {
        // Arrange : création d'un ticket pour une voiture stationnée 25 minutes
        Date inTime = new Date(System.currentTimeMillis() - (25* 60 * 1000)); // Entrée il y a 25 minutes
        Date outTime = new Date(); // Sortie maintenant
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        // Act : appel de la méthode calculateFare
        fareCalculatorService.calculateFare(ticket);

        // Assert : vérification que le prix est 0
        assertEquals(0, ticket.getPrice(), 0.001, "Le stationnement de moins de 30 minutes doit être gratuit pour une voiture.");
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
        assertEquals(0, ticket.getPrice(), 0.001, "Le stationnement de moins de 30 minutes doit être gratuit pour une moto.");
    }
    // Test pour calculer le tarif pour une voiture avec remise de 5%
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

        // Act : appel de la méthode calculateFare avec remise(5%)
        fareCalculatorService.calculateFare(ticket, true); // Remise appliquée

        // Tarif attendu avec remise (95% du tarif plein)
        double expectedPrice = (2 * Fare.CAR_RATE_PER_HOUR) * DISCOUNT_RATE; // Tarif avec remise

        // Assert : vérification que le prix est correct avec la remise de 5%
        assertEquals(expectedPrice, ticket.getPrice(), 0.001, "Le prix doit être de 2.85€ avec une remise de 5% pour 2 heures de stationnement.");
    }
    // Test pour calculer le tarif pour une moto avec remise de 5%
    @Test
    public void calculateFareBikeWithDiscount() {
        // Arrange : Création d'un ticket pour une moto stationnée pendant 1 heure avec remise
        String vehicleRegNumber = "AB-456-EF";
        Date inTime = new Date(System.currentTimeMillis() - (1 * 60 * 60 * 1000)); // 1 heure de stationnement
        Date outTime = new Date(); // Sortie maintenant
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(vehicleRegNumber);

        // Act : appel de la méthode calculateFare avec remise(5%)
        fareCalculatorService.calculateFare(ticket, true); // Remise appliquée

        // Tarif attendu avec remise (95% du tarif plein)
        double expectedPrice = (1 * Fare.BIKE_RATE_PER_HOUR) * DISCOUNT_RATE; // Tarif avec remise

        // Assert : vérification que le prix est correct avec la remise de 5%
        assertEquals(expectedPrice, ticket.getPrice(), 0.001, "Le prix doit être de 0.95€ avec une remise de 5% pour 1 heure de stationnement.");
    }

}
