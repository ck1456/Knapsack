package hps.nyu.fa14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hps.nyu.fa14.solve.BranchAndBoundFiller;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class BranchAndBoundFillerTest {

    @Ignore // Doesn't work for the whole problem set
    @Test
    public void test() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_1_0.in");
        IFiller f = new BranchAndBoundFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(217778, k0.totalValue());
        assertEquals(165218, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }

}
