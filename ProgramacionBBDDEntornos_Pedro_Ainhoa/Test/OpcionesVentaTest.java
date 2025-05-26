import PixelDB.Principal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OpcionesVentaTest {

    Principal principal = new Principal();
    @Test
    @DisplayName("Test mostrarOpcionesVenta no lanza excepción")
    void testMostrarOpcionesVenta() {
        assertDoesNotThrow(() -> principal.mostrarOpcionesVenta());
    }

}
