import PixelDB.Principal;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;


public class VentaTest {
    private Principal principal;


    @BeforeEach
    void setUp() {
        principal = new Principal();
    }


    @Test
    @DisplayName("Test mostrar opciones venta")
    void testMostrarOpcionesVenta() {
        assertDoesNotThrow(() -> principal.mostrarOpcionesVenta());
    }


    @Test
    @DisplayName("Test mostrar carrito")
    void testMostrarCarrito() {
        assertDoesNotThrow(() -> principal.mostrarCarrito());
    }


    @Test
    @DisplayName("Test mostrar formulario datos")
    void testMostrarFormularioDatos() {
        assertDoesNotThrow(() ->
                principal.mostrarFormularioDatos("8GB RAM/64GB ROM", "Negro"));
    }
}
