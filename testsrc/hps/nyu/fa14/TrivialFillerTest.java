package hps.nyu.fa14;

import static org.junit.Assert.*;
import hps.nyu.fa14.solve.TrivialFiller;

import java.util.List;

import org.junit.Test;

public class TrivialFillerTest {

    @Test
    public void testProblem1() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_1_0.in");
        IFiller f = new TrivialFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(196442, k0.totalValue());
        assertEquals(164492, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }

    @Test
    public void testProblem2() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_2_0.in");
        IFiller f = new TrivialFiller();
        List<Knapsack> solution = f.fill(c);
        {
            Knapsack k0 = solution.get(0);
            assertEquals(33904, k0.totalValue());
            assertEquals(36727, k0.currentWeight());
            assertTrue(k0.isWeightAcceptable());
        }
        {
            Knapsack k1 = solution.get(1);
            assertEquals(65634, k1.totalValue());
            assertEquals(62716, k1.currentWeight());
            assertTrue(k1.isWeightAcceptable());
        }
    }
    
    @Test
    public void testProblem3() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_3_0.in");
        IFiller f = new TrivialFiller();
        List<Knapsack> solution = f.fill(c);
        {
            Knapsack k0 = solution.get(0);
            assertEquals(180426, k0.totalValue());
            assertEquals(2449, k0.currentWeight());
            assertTrue(k0.isWeightAcceptable());
        }
    }

}
