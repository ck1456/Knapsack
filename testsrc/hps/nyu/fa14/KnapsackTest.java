package hps.nyu.fa14;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class KnapsackTest {

    @Test
    public void testCalculateValue() {

        Catalog c = new Catalog(3, 2, Arrays.asList(7));
        c.items.put(1, new Item(1, 0, 2));
        c.items.put(2, new Item(2, 0, 3));

        c.profitMatrix[1][1] = 1;
        c.profitMatrix[1][2] = 2;
        c.profitMatrix[2][2] = 6;
        
        Knapsack k = c.getEmptyKnapsack(1);
        k.items.add(c.items.get(1));
        assertEquals(1, k.totalValue());
        k.items.add(c.items.get(2));
        assertEquals(9, k.totalValue());
        k.items.remove(c.items.get(1));
        assertEquals(6, k.totalValue());
    }
}
