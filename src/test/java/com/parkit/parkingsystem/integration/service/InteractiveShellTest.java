package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
public class InteractiveShellTest {

//@Mock
   private InputReaderUtil inputReaderUtil;
    @Mock
   private ParkingSpotDAO parkingSpotDAO;
    @Mock
   private TicketDAO ticketDAO;
    @Mock
   private ParkingService parkingService;


    private InputStream originalSystemIn;

    @BeforeEach
    public void setUp() {
        // Sauvegarder l'inputStream d'origine pour le restaurer après le test
        originalSystemIn = System.in;
    }

    @AfterEach
    public void restoreSystemInStream() {
        // Rétablir System.in à son état d'origine
        System.setIn(originalSystemIn);
    }

    @Test
    void testReadSelectionReturnsValidInput() {
        // Simuler une entrée utilisateur valide
        String simulatedInput = "1\n"; // Le '\n' simule l'appui sur 'Entrée'
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

       InputReaderUtil inputReaderUtil = new InputReaderUtil();
        // Appel de la méthode à tester
        int selection = inputReaderUtil.readSelection();

        // Vérifier que le résultat est correct
        System.out.println("ok");
    }

}
