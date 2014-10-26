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
        assertEquals(203686, k0.totalValue());
        assertEquals(165166, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }

    @Test
    public void testProblem2() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_2_0.in");
        IFiller f = new GreedyFiller();
        List<Knapsack> solution = f.fill(c);
        {
            Knapsack k0 = solution.get(0);
            assertEquals(98809, k0.totalValue());
            assertEquals(36977, k0.currentWeight());
            assertTrue(k0.isWeightAcceptable());
        }
        {
            Knapsack k1 = solution.get(1);
            assertEquals(66538, k1.totalValue());
            assertEquals(62962, k1.currentWeight());
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
