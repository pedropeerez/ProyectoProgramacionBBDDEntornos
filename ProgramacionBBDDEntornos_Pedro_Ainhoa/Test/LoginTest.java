import PixelDB.Principal;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


public class LoginTest {
    private Principal principal;


    @BeforeEach
    void setUp() {
        principal = new Principal();
    }


    @Test
    @DisplayName("Test login exitoso")
    void testLoginSuccessful() {
        boolean result = principal.mostrarLoginDialog();
        assertTrue(result);
    }


    @Test
    @DisplayName("Test login fallido")
    void testLoginFailed() {
        boolean result = principal.mostrarLoginDialog();
        assertFalse(result);
    }
}
