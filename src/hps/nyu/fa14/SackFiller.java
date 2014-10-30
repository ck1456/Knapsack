package hps.nyu.fa14;

import hps.nyu.fa14.solve.ExpandingCoreFiller;
import hps.nyu.fa14.solve.IncrementalProgressFiller;
import hps.nyu.fa14.solve.MBoundAndBoundFiller2;
import hps.nyu.fa14.solve.QDynamicProgrammingFiller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implements a solution to filling a knapsack based on the spec here:
 * https://docs.google.com/a/nyu.edu/document/d/1cbpd0xcQ535BCva4dLAM-Zr6JJ-71
 * ykKASYkBHqXl10/edit
 * 
 * @author ck1456
 */
public class SackFiller {

    private static List<Knapsack> fill(Catalog c, String outputPath) {

        // Configure the desired filler based on the problem type
        IFiller f = new ExpandingCoreFiller();  // Default for SKP
        if(c.problemType == 2){
            // Multiple Knapsack Problem (MKP)
            f = new MBoundAndBoundFiller2();
        } else if(c.problemType == 3){
            // Quadratic Knapsack Problem (QKP)
            f = new QDynamicProgrammingFiller();
        }
        
        // Make sure to write incremental solutions as we get them
        IFiller pf = new IncrementalProgressFiller(f, outputPath);
        List<Knapsack> solution = pf.fill(c);
        //printSolution(solution);
        return solution;
    }
    
    @SuppressWarnings("unused")
    private static void printSolution(List<Knapsack> s){
        int totalValue = 0;
        List<Knapsack> ordered = new ArrayList<Knapsack>(s);
        Collections.sort(ordered, Knapsack.SORT_BY_ID);
        for(Knapsack k : ordered){
            totalValue += k.totalValue();
            System.out.println(k);
        }
        if(s.size() > 0){
            System.out.println("Total Value = " + totalValue);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            usage();
        }
        // first parameter is input
        String inputFile = args[0];
        String outputFile = args[1];

        Catalog c = Catalog.parseFile(inputFile);
        
        // Make directory for the output file if it does not exist
        File outFile = new File(outputFile);
        outFile.getAbsoluteFile().getParentFile().mkdirs();
        
        List<Knapsack> solution = fill(c, outputFile);
        // Only write if it is acceptable
        for(Knapsack k : solution){
            if(!k.isWeightAcceptable()){
                System.err.println("Achieved a non-feasible solution");
                return;
            }
        }
        Knapsack.writeFile(solution, outputFile);
    }

    private static void usage() {
        // How to use it
        System.out.println("java -jar SackFiller <input> <output>");
        System.exit(1);
    }
}
