package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.IFiller;
import hps.nyu.fa14.Knapsack;

import java.util.List;

/**
 * Finds a solution to the knapsack problem by solving an LP relaxation problem
 * This is really just a greedy solution to the continuous knapsack problem
 */
public class LPRelaxFiller implements IFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {
        return new GreedyFiller().fill(c);
    }
}
