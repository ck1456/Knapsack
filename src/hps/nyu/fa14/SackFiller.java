package hps.nyu.fa14;

import hps.nyu.fa14.solve.TrivialFiller;

import java.io.File;
import java.util.List;

/**
 * Implements a solution to filling a knapsack based on the spec here:
 * https://docs.google.com/a/nyu.edu/document/d/1cbpd0xcQ535BCva4dLAM-Zr6JJ-71
 * ykKASYkBHqXl10/edit
 * 
 * @author ck1456
 */
public class SackFiller {

    private static List<Knapsack> fill(Catalog c) {

        // TODO: Implement this better
        IFiller f = new TrivialFiller();

        List<Knapsack> solution = f.fill(c);
        return solution;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            usage();
        }
        // first parameter is input
        String inputFile = args[0];
        String outputFile = args[1];

        Catalog c = Catalog.parseFile(inputFile);
        List<Knapsack> solution = fill(c);

        // Make directory for the output file if it does not exist
        File outFile = new File(outputFile);
        outFile.getAbsoluteFile().getParentFile().mkdirs();
        Knapsack.writeFile(solution, outputFile);
    }

    private static void usage() {
        // How to use it
        System.out.println("java -jar SackFiller <input> <output>");
        System.exit(1);
    }
}
