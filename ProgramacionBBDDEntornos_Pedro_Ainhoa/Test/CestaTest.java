import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import PixelDB.Cesta;

class CestaTest {

    private Cesta cesta;

    @BeforeEach
    void setUp() {
        cesta = new Cesta();
    }

    @Test
    void testAddItem() {
        Cesta.Objeto obj = new Cesta.Objeto("Modelo1", "Opcion1", "Rojo", 100.0);
        cesta.addItem(obj);
        assertEquals(1, cesta.getConteoObjetos());
        assertFalse(cesta.isEmpty());
    }

    @Test
    void testRemoveItem() {
        Cesta.Objeto obj = new Cesta.Objeto("Modelo2", "Opcion2", "Azul", 200.0);
        cesta.addItem(obj);
        cesta.removeItem(0);
        assertTrue(cesta.isEmpty());
    }

    @Test
    void testGetTotalPrecio() {
        cesta.addItem(new Cesta.Objeto("M1", "Op1", "Negro", 50.0));
        cesta.addItem(new Cesta.Objeto("M2", "Op2", "Blanco", 75.5));
        assertEquals(125.5, cesta.getTotalPrecio(), 0.01);
    }

    @Test
    void testClear() {
        cesta.addItem(new Cesta.Objeto("M3", "Op3", "Verde", 30.0));
        cesta.clear();
        assertTrue(cesta.isEmpty());
    }

    @Test
    void testIsEmptyInitially() {
        assertTrue(cesta.isEmpty());
    }
}