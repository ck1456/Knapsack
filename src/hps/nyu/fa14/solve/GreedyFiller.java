package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.IFiller;
import hps.nyu.fa14.Item;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Calculates the weight to value ratio for each item and adds them in
 * descending order until no more can be added
 */
public class GreedyFiller implements IFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {
        List<Knapsack> sacks = new ArrayList<Knapsack>();

        // get a list of all the items
        List<Item> items = new ArrayList<Item>();
        for (int i = 1; i <= c.objectCount; i++) {
            items.add(c.items.get(i));
        }
        // sort it by ratio value/weight in descending order
        Collections.sort(items, RANK_BY_RATIO);
        Collections.reverse(items);

        Item nextItem = items.get(0);
        // Fill as many knapsacks as necessary
        for (Knapsack k : c.getAllEmptyKnapsacks()) {
            // while it is not full, add something new
            while (k.currentWeight() + nextItem.weight <= k.capacity) {
                k.items.add(nextItem);
                items.remove(nextItem); // remove used items
                nextItem = items.get(0);
            }
            sacks.add(k);
        }
        
        // TODO: See if anything else will fit because it is small enough
        for (Knapsack k : sacks) {
            int item = 0;
            
            while(item < items.size()){
                nextItem = items.get(item);
                if(k.currentWeight() + nextItem.weight <= k.capacity){
                    // If smoe item will fit because it is small enough, include it
                    k.items.add(nextItem);
                    items.remove(nextItem);
                } else {
                    item++; // Otherwise, skip and move on
                }
            }
        }

        return sacks;
    }

    public static Comparator<Item> RANK_BY_RATIO = new Comparator<Item>() {
        @Override
        public int compare(Item i1, Item i2) {
            return (int) Math.signum((i1.value / i1.weight)
                    - (i2.value / i2.weight));
        }
    };

    // TODO: Need a different ordering for quadratic knapsack problem types

}
