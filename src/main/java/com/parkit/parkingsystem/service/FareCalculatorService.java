package com.parkit.parkingsystem.service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
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
        // Vérification que l'heure de sortie est valide
        if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        // Calcul de la durée de stationnement en millisecondes, puis conversion en heures
        long inTimeMillis = ticket.getInTime().getTime();
        long outTimeMillis = ticket.getOutTime().getTime();

        double durationInHours = (double) (outTimeMillis - inTimeMillis) / (1000 * 60 * 60);

        // Arrondir la durée à 4 décimales pour éviter les petites différences
        BigDecimal durationRounded = new BigDecimal(durationInHours).setScale(4, RoundingMode.HALF_UP);
        durationInHours = durationRounded.doubleValue();

        // Si la durée est inférieure ou égale à 30 minutes, le stationnement est gratuit
        if (durationInHours <= 0.5) {
            ticket.setPrice(0.0);  // Gratuit si la durée est <= 30 minutes
            return;
        }

        // Vérification supplémentaire que la durée de stationnement est bien positive
        if (durationInHours <= 0) {
            throw new IllegalArgumentException("Parking duration must be greater than zero.");
        }

        // Détermination du tarif en fonction du type de véhicule (voiture ou moto)
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                // Calcul du tarif arrondi à 4 décimales
                BigDecimal price = new BigDecimal(durationInHours * Fare.CAR_RATE_PER_HOUR).setScale(4, RoundingMode.HALF_UP);
                ticket.setPrice(durationInHours * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                // Calcul du tarif arrondi à 4 décimales
                BigDecimal price = new BigDecimal(durationInHours * Fare.BIKE_RATE_PER_HOUR).setScale(4, RoundingMode.HALF_UP);
                ticket.setPrice(durationInHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown Parking Type");// Si le type de véhicule est inconnu, lever une exception
            }
        }
        // Application de la remise de 5% si l'utilisateur est un client régulier
        if (discount) {
            // Calcul du tarif arrondi à 4 décimales
            BigDecimal discountedPrice = new BigDecimal(ticket.getPrice() * DISCOUNT_RATE).setScale(4, RoundingMode.HALF_UP);
            ticket.setPrice(ticket.getPrice() * DISCOUNT_RATE);
        }
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
