package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.List;

public class TrivialFiller extends AbstractFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {
        List<Knapsack> sacks = new ArrayList<Knapsack>();
        int item = 1;
        // Generate as many knapsacks as necessary
        for(Knapsack k : c.getAllEmptyKnapsacks()){
            // while it is not full, add something new
            while(k.currentWeight() + c.items.get(item).weight <= k.capacity){
                k.items.add(c.items.get(item));
                item++;
            }            
            sacks.add(k);
        }
        return sacks;
    }
}
