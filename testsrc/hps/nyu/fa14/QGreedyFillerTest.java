package hps.nyu.fa14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hps.nyu.fa14.solve.QGreedyFiller;

import java.util.List;

import org.junit.Test;

public class QGreedyFillerTest {

    @Test
    public void test_3_0() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_3_0.in");
        IFiller f = new QGreedyFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(187955, k0.totalValue());
        assertEquals(2459, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }
}
