package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.IFiller;
import hps.nyu.fa14.ISolutionMonitor;
import hps.nyu.fa14.Knapsack;

import java.util.List;

// Makes sure to write a new solution to disk each time a new (better one) is available
public class IncrementalProgressFiller extends AbstractFiller implements
        ISolutionMonitor {

    private final String outputPath;

    private final IFiller filler;

    public IncrementalProgressFiller(IFiller filler, String output) {
        this.filler = filler;
        this.outputPath = output;
        filler.addSolutionMonitor(this);
    }

    @Override
    public List<Knapsack> fill(Catalog c) {
        return filler.fill(c);
    }

    @Override
    public void updateSolution(List<Knapsack> solution) {
        synchronized (filler) {
            // Make sure that it is valid before writing to disk
            for(Knapsack k : solution){
                if(!k.isWeightAcceptable()){
                    //System.out.println("Solution is not feasible...ignoring");
                    return;
                }
            }
            
            try {
                //System.out.println("Write acceptable solution");
                Knapsack.writeFile(solution, outputPath);
            } catch (Exception IOException) {
                System.err.println("Trouble writing incremental file");
            }
        }
    }
}
