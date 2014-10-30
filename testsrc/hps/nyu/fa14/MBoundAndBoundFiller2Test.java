package hps.nyu.fa14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hps.nyu.fa14.solve.MBoundAndBoundFiller2;

import java.util.List;

import org.junit.Test;

public class MBoundAndBoundFiller2Test {

    private int[] maxValues_1 = new int[] { 
            104361, 
            13383, 
            109422, 
            6567 };

    private int[] maxValues_2 = new int[] { 
            69715, 
            17974, 
            119683, 
            15651 };

    private int[] maxWeights_1 = new int[] { 
            36982, 
            10031, 
            48031, 
            2409 };

    private int[] maxWeights_2 = new int[] { 
            62971, 
            15763, 
            81283, 
            10034 };

    
    @Test
    public void test_2_all() throws Exception {
       for(int i = 0; i < 4; i++){
           testFile(i);
       }
    }
    
    private void testFile(int index) throws Exception {
        String filePath = String.format("data/sample_2_%d.in", index);

        Catalog c = Catalog.parseFile(filePath);
        IFiller f = new MBoundAndBoundFiller2();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(maxValues_1[index], k0.totalValue());
        assertEquals(maxWeights_1[index], k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
        
        Knapsack k1 = solution.get(1);
        assertEquals(maxValues_2[index], k1.totalValue());
        assertEquals(maxWeights_2[index], k1.currentWeight());
        assertTrue(k1.isWeightAcceptable());
    }
    
    //@Test
    public void test_2_0() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_2_0.in");
        IFiller f = new MBoundAndBoundFiller2();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        Knapsack k1 = solution.get(1);
        assertEquals(104361, k0.totalValue());
        assertEquals(36982, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
        
        assertEquals(69715, k1.totalValue());
        assertEquals(62971, k1.currentWeight());
        assertTrue(k1.isWeightAcceptable());
    }
    
    //@Test
    public void test_2_1() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_2_1.in");
        IFiller f = new MBoundAndBoundFiller2();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        Knapsack k1 = solution.get(1);
        assertEquals(13383, k0.totalValue());
        assertEquals(10031, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
        
        assertEquals(17974, k1.totalValue());
        assertEquals(15763, k1.currentWeight());
        assertTrue(k1.isWeightAcceptable());
    }
    
    @Test
    public void test_2_3() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_2_3.in");
        IFiller f = new MBoundAndBoundFiller2();
        List<Knapsack> solution = f.fill(c);
        // Ensure that this is feasible
        for(Knapsack k : solution){
            assertTrue(k.isWeightAcceptable());
        }
    }
}
