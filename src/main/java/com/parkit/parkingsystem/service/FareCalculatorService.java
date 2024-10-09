package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount) {
        if (ticket.getOutTime() == null || ticket.getOutTime().before(ticket.getInTime())) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        // Calculer la durée en heures
        long inTimeMillis = ticket.getInTime().getTime();
        long outTimeMillis = ticket.getOutTime().getTime();

        double durationInHours = (double) (outTimeMillis - inTimeMillis) / (1000 * 60 * 60); // Convertir millisecondes en heures

        // Vérifier si la durée est inférieure ou égale à 30 minutes (0.5 heure)
        if (durationInHours <= 0.5) {
            ticket.setPrice(0.0);  // Gratuit si la durée est <= 30 minutes
            return;
        }

        if (durationInHours <= 0) {
            throw new IllegalArgumentException("Parking duration must be greater than zero.");
        }

        // Définir le tarif en fonction du type de véhicule
        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(durationInHours * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(durationInHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown Parking Type");
            }
        }
        // Appliquer une remise de 5% si le paramètre discount est vrai
        if (discount) {
            ticket.setPrice(ticket.getPrice() * 0.95);
        }
    }
    // Méthode surchargée pour calculer le tarif sans réduction
    public void calculateFare(Ticket ticket) {
        calculateFare(ticket, false);
    }

}
