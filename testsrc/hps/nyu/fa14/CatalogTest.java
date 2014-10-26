package hps.nyu.fa14;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

public class CatalogTest {

    @Test
    public void testParse1() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_1_0.in");
        assertNotNull(c);
        assertEquals(1, c.problemType);
        assertEquals(1, c.knapsackCapacities.length);
        assertEquals(165218, c.knapsackCapacities[0]);
        assertEquals(950, c.objectCount);
        assertEquals(c.objectCount, c.items.keySet().size());
    }

    @Test
    public void testParse2() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_2_0.in");
        assertNotNull(c);
        assertEquals(2, c.problemType);
        assertEquals(2, c.knapsackCapacities.length);
        assertEquals(36982, c.knapsackCapacities[0]);
        assertEquals(62971, c.knapsackCapacities[1]);
        assertEquals(1000, c.objectCount);
        assertEquals(c.objectCount, c.items.keySet().size());
    }
    
    @Test
    public void testParse3() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_3_0.in");
        assertNotNull(c);
        assertEquals(3, c.problemType);
        assertEquals(1, c.knapsackCapacities.length);
        assertEquals(2466, c.knapsackCapacities[0]);
        assertEquals(100, c.objectCount);
        assertEquals(c.objectCount, c.items.keySet().size());
        // Assert something about the profit matrix
        assertEquals(c.objectCount, c.profitMatrix.length);
        assertEquals(c.profitMatrix.length, c.profitMatrix[0].length);
        assertEquals(55, c.profitMatrix[0][0]);
    }

}
