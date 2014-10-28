package hps.nyu.fa14;

import java.util.List;

public interface IFiller {

    List<Knapsack> fill(Catalog c);
    void addSolutionMonitor(ISolutionMonitor monitor);
}
