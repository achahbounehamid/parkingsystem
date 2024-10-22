package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class InteractiveShellTest {


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

//    @Test
//    void interactiveShellTest(){
//
//        //TODO je ne comprend pas en mockant le la classe il excute le code à l'interieur
//         Mockito.when(inputReaderUtil.readSelection()).thenReturn(1);
//
//        verify(parkingService, times(1)).processIncomingVehicle();
//        InteractiveShell.loadInterface();
//
//
//    }




}
