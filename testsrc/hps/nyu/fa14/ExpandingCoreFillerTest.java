package hps.nyu.fa14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hps.nyu.fa14.solve.ExpandingCoreFiller;

import java.util.List;

import org.junit.Test;

public class ExpandingCoreFillerTest {

    private int[] maxValues = new int[] { 
            217867, 
            1221140, 
            2533603, 
            1073052,
            1019957 };
    
    private int[] maxWeights = new int[] { 
            165217, 
            894740, 
            1378079, 
            961795,
            768457 };

    @Test
    public void test_1_all() throws Exception {
       for(int i = 0; i < 5; i++){
           testFile(i);
       }
    }
    
    private void testFile(int index) throws Exception {
        String filePath = String.format("data/sample_1_%d.in", index);

        Catalog c = Catalog.parseFile(filePath);
        IFiller f = new ExpandingCoreFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(maxValues[index], k0.totalValue());
        assertEquals(maxWeights[index], k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }
    
    @Test
    public void test_1_0() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_1_0.in");
        IFiller f = new ExpandingCoreFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(217867, k0.totalValue());
        assertEquals(165217, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }
}
