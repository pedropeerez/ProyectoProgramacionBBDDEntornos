import PixelDB.Principal;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class GetModeloTest {

    private Principal principal;

    @BeforeEach
    void setUp() {
        principal = new Principal();
    }

    @Test
    @DisplayName("getModeloFromProductId retorna null para ID inválido")
    void testGetModeloFromProductIdInvalido() {
        assertNull(principal.getModeloFromProductId(-1));
        assertNull(principal.getModeloFromProductId(0));
    }
}