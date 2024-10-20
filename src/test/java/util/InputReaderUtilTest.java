package util;

import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class InputReaderUtilTest {

    private InputStream originalIn;

    @BeforeEach
    public void setUp() {
        // Sauvegarder l'entrée standard d'origine pour la restaurer après chaque test
        originalIn = System.in;
    }

    //@Test
    public void testReadSelection_ValidInput() {
        try {
            // Simuler une entrée valide "3" comme si l'utilisateur avait entré cette valeur
            ByteArrayInputStream in = new ByteArrayInputStream("3\n".getBytes());
            System.setIn(in); // Rediriger l'entrée standard vers notre entrée simulée

            // Créer une instance de InputReaderUtil
            InputReaderUtil inputReaderUtil = new InputReaderUtil();

            // Appeler la méthode pour lire la sélection
            int selection = inputReaderUtil.readSelection();

            // Vérifier que la valeur retournée est bien "3"
            assertEquals(3, selection);
        } finally {
            // Restaurer l'entrée standard d'origine après le test
            System.setIn(originalIn);
        }
    }
}
