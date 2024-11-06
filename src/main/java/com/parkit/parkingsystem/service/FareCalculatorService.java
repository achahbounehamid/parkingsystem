package com.parkit.parkingsystem.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {
    // Constante pour le taux de remise appliqué aux utilisateurs réguliers
    private static final double DISCOUNT_RATE = 0.95;
    /**
     * Cette méthode calcule le tarif du ticket en fonction de la durée de stationnement
     * et applique éventuellement une remise si l'utilisateur est un client régulier.
     *
     * @param ticket   le ticket contenant les informations de stationnement
     * @param discount si vrai, une remise de 5% est appliquée
     */
    public void calculateFare(Ticket ticket, boolean discount) {
        // Vérification des informations du ticket
        if (ticket == null || ticket.getParkingSpot() == null || ticket.getParkingSpot().getParkingType() == null) {
            throw new IllegalArgumentException("Ticket or Parking Type information is missing");
        }else if (ticket.getParkingSpot().getParkingType() == null) {
            throw new NullPointerException("Parking Type is missing");
        }
        // Vérification que l'heure de sortie est valide
        if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("Out time provided is incorrect: " + ticket.getOutTime());
        }
        // Calcul de la durée de stationnement en heures
        long inTimeMillis = ticket.getInTime().getTime();
        long outTimeMillis = ticket.getOutTime().getTime();
        double durationInHours = (double) (outTimeMillis - inTimeMillis) / (1000 * 60 * 60);
        // Arrondir la durée à 4 décimales pour éviter les petites différences
        BigDecimal durationRounded = BigDecimal.valueOf(durationInHours).setScale(4, RoundingMode.HALF_UP);
        durationInHours = durationRounded.doubleValue();
        // Si la durée est inférieure ou égale à 30 minutes, le stationnement est gratuit
        if (durationInHours <= 0.5) {
            ticket.setPrice(0.0);
            return;
        }
        // Calcul du tarif basé sur le type de véhicule
        BigDecimal price;
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                price = BigDecimal.valueOf(durationInHours).multiply(BigDecimal.valueOf(Fare.CAR_RATE_PER_HOUR))
                        .setScale(4, RoundingMode.HALF_UP);
                break;
            case BIKE:
                price = BigDecimal.valueOf(durationInHours).multiply(BigDecimal.valueOf(Fare.BIKE_RATE_PER_HOUR))
                        .setScale(4, RoundingMode.HALF_UP);
                break;
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
        // Application de la remise de 5% si l'utilisateur est un client régulier
        if (discount) {
            price = price.multiply(BigDecimal.valueOf(DISCOUNT_RATE)).setScale(4, RoundingMode.HALF_UP);
        }
        // Affectation du prix au ticket
        ticket.setPrice(price.doubleValue());
    }
    /**
     * Surcharge de la méthode calculateFare pour les cas où il n'y a pas de remise.
     * Par défaut, la remise n'est pas appliquée.
     *
     * @param ticket le ticket contenant les informations de stationnement
     */
    public void calculateFare(Ticket ticket) {
        // Appel de la méthode principale en passant 'false' pour la remise
        calculateFare(ticket, false);
    }

}
