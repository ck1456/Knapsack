package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.Item;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates the weight to value ratio for each item and adds them in
 * descending order until no more can be added
 * 
 * Implements a greedy feasible solution as a lower bound for MKP Uses partial
 * solutions to CKP for each knapsack
 */
public class GreedyFiller extends AbstractFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {
        List<Knapsack> sacks = new ArrayList<Knapsack>();

        // get a list of all the items
        List<Item> items = c.getItemsSortedByRatio();

        // Fill as many knapsacks as necessary
        for (Knapsack k : c.getAllEmptyKnapsacks()) {
            // while it is not full, add something new
            if (items.size() > 0) {
                Item nextItem = items.get(0);
                while (k.currentWeight() + nextItem.weight <= k.capacity) {
                    k.items.add(nextItem);
                    items.remove(nextItem); // remove used items
                    if(items.size() == 0){ // ran out of items
                        break;
                    }
                    nextItem = items.get(0);
                }
            }
            sacks.add(k);
        }

        // TODO: See if anything else will fit because it is small enough
        for (Knapsack k : sacks) {
            int item = 0;

            while (item < items.size()) {
                Item nextItem = items.get(item);
                if (k.currentWeight() + nextItem.weight <= k.capacity) {
                    // If some item will fit because it is small enough, include
                    // it
                    k.items.add(nextItem);
                    items.remove(nextItem);
                } else {
                    item++; // Otherwise, skip and move on
                }
            }
        }

        return sacks;
    }
}
