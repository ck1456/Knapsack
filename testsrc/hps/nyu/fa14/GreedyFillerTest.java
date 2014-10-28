package hps.nyu.fa14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hps.nyu.fa14.solve.GreedyFiller;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class GreedyFillerTest {

    @Test
    public void testProblem1() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_1_0.in");
        IFiller f = new GreedyFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        System.out.println(k0);
        assertEquals(217687, k0.totalValue());
        assertEquals(165127, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }

    @Test
    public void testProblem2() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_2_0.in");
        IFiller f = new GreedyFiller();
        List<Knapsack> solution = f.fill(c);
        {
            Knapsack k0 = solution.get(0);
            System.out.println(k0);
            assertEquals(104657, k0.totalValue());
            assertEquals(36963, k0.currentWeight());
            assertTrue(k0.isWeightAcceptable());
        }
        {
            Knapsack k1 = solution.get(1);
            System.out.println(k1);
            assertEquals(69523, k1.totalValue());
            assertEquals(62941, k1.currentWeight());
            assertTrue(k1.isWeightAcceptable());
        }
    }

    @Ignore
    @Test
    public void testProblem3() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_3_0.in");
        IFiller f = new GreedyFiller();
        List<Knapsack> solution = f.fill(c);
        {
            Knapsack k0 = solution.get(0);
            assertEquals(180426, k0.totalValue());
            assertEquals(2449, k0.currentWeight());
            assertTrue(k0.isWeightAcceptable());
        }
    }
}
