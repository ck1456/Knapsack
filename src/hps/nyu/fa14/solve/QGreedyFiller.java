package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.Item;
import hps.nyu.fa14.Knapsack;

import java.util.Arrays;
import java.util.List;

/**
 * Implements combination of heuristics plus local search
 * 
 * Resources: http://www.lancaster.ac.uk/staff/letchfoa/articles/qkp1.pdf
 */
public class QGreedyFiller extends AbstractFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {

        // Add everything to the knapsack
        Knapsack k = c.getEmptyKnapsack(1);
        for (Item i : c.items.values()) {
            k.items.add(i);
        }

        // While the weight constraint is not met
        while (!k.isWeightAcceptable()) {
            double min_l_w = Double.MAX_VALUE;
            int indexToRemove = -1;
            int value = k.totalValue();
            // calculate removing the item that will minimize the loss/weight
            for(int i = 0; i < k.items.size(); i++){
                Item item = k.items.remove(i);
                double l_w = ((double)(value - k.totalValue())) / item.weight;  
                if(l_w < min_l_w){
                    indexToRemove = i;
                }
                k.items.add(i, item); // add it back in
            }
            k.items.remove(indexToRemove);
        }

        System.out.println(k);
        return Arrays.asList(k);
    }
}
