package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.Item;
import hps.nyu.fa14.Knapsack;

import java.util.List;

/**
 * Finds a solution to the knapsack problem by using exact
 * polynomial time solution
 * 
 * Resources:
 * http://www.optimset.com/knapsack.pdf
 */
public class OptimsetFiller extends AbstractFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {

        long sum = sumCatalog(c);
        //int w = c.knapsackCapacities[1];
        System.out.println("Sum total " + sum);
        System.out.println("Runtime" + 2*sum*sum);
       return null;
    }
    
    private static long sumCatalog(Catalog c){
        long sum  = 0;
       
        for(Item i : c.items.values()){
            sum += i.weight;
        }
        return sum; 
    }
}
