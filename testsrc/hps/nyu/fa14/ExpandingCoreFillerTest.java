package hps.nyu.fa14;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import hps.nyu.fa14.solve.ExpandingCoreFiller;

import java.util.List;

import org.junit.Test;

public class ExpandingCoreFillerTest {

    @Test
    public void test_1_0() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_1_0.in");
        IFiller f = new ExpandingCoreFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(217778, k0.totalValue());
        assertEquals(165218, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }

    @Test
    public void test_1_1() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_1_1.in");
        IFiller f = new ExpandingCoreFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(1219701, k0.totalValue());
        assertEquals(894741, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }
    
    //@Test
    public void test_1_2() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_1_2.in");
        IFiller f = new ExpandingCoreFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(1219701, k0.totalValue());
        assertEquals(894741, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }
    
    @Test
    public void test_1_3() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_1_3.in");
        IFiller f = new ExpandingCoreFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(1072651, k0.totalValue());
        assertEquals(961796, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }
    
    @Test
    public void test_1_4() throws Exception {
        Catalog c = Catalog.parseFile("data/sample_1_4.in");
        IFiller f = new ExpandingCoreFiller();
        List<Knapsack> solution = f.fill(c);
        Knapsack k0 = solution.get(0);
        assertEquals(1019957, k0.totalValue());
        assertEquals(768457, k0.currentWeight());
        assertTrue(k0.isWeightAcceptable());
    }

}
