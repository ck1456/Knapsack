package hps.nyu.fa14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hps.nyu.fa14.solve.QDynamicProgrammingFiller;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class QDynamicProgrammingFillerTest {

    private int[] maxValues = new int[] { 
            173431, 
            2017, 
            0, 
            26385,
            92708 };
    
    private int[] maxWeights = new int[] { 
            2466, 
            154, 
            0, 
            568,
            1220 };

    @Test
    public void test_3_all() throws Exception {
       for(int i : new int[] {0,1,3,4}){
           testFile(i);
       }
    }
    
    private void testFile(int index) throws Exception {
        String filePath = String.format("data/sample_3_%d.in", index);

        Catalog c = Catalog.parseFile(filePath);
        IFiller f = new QDynamicProgrammingFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(maxValues[index], k0.totalValue());
        assertEquals(maxWeights[index], k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }
    
    @Test
    public void test_3_0() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_3_0.in");
        IFiller f = new QDynamicProgrammingFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(173431, k0.totalValue());
        assertEquals(2466, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }
    
    @Test
    public void test_small_3() throws Exception {
        Catalog c = new Catalog(3, 3, Arrays.asList(2));
        c.items.put(1, new Item(1, 0, 1));
        c.items.put(2, new Item(2, 0, 1));
        c.items.put(3, new Item(3, 0, 1));

        c.profitMatrix[1][1] = 10;
        c.profitMatrix[2][2] = 1;
        c.profitMatrix[3][3] = 1;
        c.profitMatrix[2][3] = 20;
        
        IFiller f = new QDynamicProgrammingFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(22, k0.totalValue());
    }
}
