package hps.nyu.fa14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class CatalogTest {

    @Test
    public void testParse1() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_1_0.in");
        assertNotNull(c);
        assertEquals(1, c.problemType);
        assertEquals(1, c.knapsackCount);
        assertEquals(165218, c.knapsackCapacities[1]);
        assertEquals(950, c.objectCount);
        assertEquals(c.objectCount, c.items.keySet().size());
    }

    @Test
    public void testParse2() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_2_0.in");
        assertNotNull(c);
        assertEquals(2, c.problemType);
        assertEquals(2, c.knapsackCount);
        assertEquals(36982, c.knapsackCapacities[1]);
        assertEquals(62971, c.knapsackCapacities[2]);
        assertEquals(1000, c.objectCount);
        assertEquals(c.objectCount, c.items.keySet().size());
    }
    
    @Test
    public void testParse3() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_3_0.in");
        assertNotNull(c);
        assertEquals(3, c.problemType);
        assertEquals(1, c.knapsackCount);
        assertEquals(2466, c.knapsackCapacities[1]);
        assertEquals(100, c.objectCount);
        assertEquals(c.objectCount, c.items.keySet().size());
        // Assert something about the profit matrix
        assertEquals(c.objectCount + 1, c.profitMatrix.length);
        assertEquals(c.profitMatrix.length, c.profitMatrix[0].length);
        assertEquals(55, c.profitMatrix[1][1]);
        assertEquals(15, c.profitMatrix[2][2]);
        assertEquals(75, c.profitMatrix[2][100]);
    }
    
    @Test
    public void testValue_3_0() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_3_0.in");

        String[] itemStrings = new String[] {
                "77 40 86 65 50 36 15 6 48 82 21 97 94 75 62 52 92 58 76 93 27 79 80 34 57 73 24 39 38 90 70 44 99 46 4 64 43 11 17 12 69 96 47 30 53 23 31 85 14 45 9 22 49 51 35 91 98 3 13 5 88 60 2 66 95 87 56 29 54 74 61 68 33 67 55 81 19 18 59 41 37 63 16 28 1 7 32 84 25 100 26 42 20 71 89",
                "77 40 86 65 50 36", "77 40 86", "77 40", "77", "1", "2" };

        int[] profitValues = new int[] { 173174, 750, 219, 194, 51, 55, 15 };

        for (int s = 0; s < itemStrings.length; s++) {
            Knapsack k = c.getEmptyKnapsack(1);
            for (String i : itemStrings[s].split("\\s")) {
                k.items.add(c.items.get(Integer.parseInt(i)));
            }
            // Assert something about the profit matrix
            assertEquals(profitValues[s], k.totalValue());

        }
    }
}
