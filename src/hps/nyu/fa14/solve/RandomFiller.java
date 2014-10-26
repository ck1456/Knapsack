package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.IFiller;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.List;

public class RandomFiller implements IFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {
        
        List<Knapsack> sacks = new ArrayList<Knapsack>();
        int item = 0;
        
        // get a permutation of the input ids
        
//        // Generate as many knapsacks as necessary
//        for(int k = 0; k < c.knapsackCapacities.length; k++){
//            Knapsack newSack = new Knapsack(k, c.knapsackCapacities[k]);
//            
//            // while it is not full, add something new
//            while(newSack.currentWeight() < newSack.capacity){
//                newSack.items.add(c.items.get(++item));
//            }
//            // remove the last item, because that made it go over
//            newSack.items.remove(c.items.get(--item));
//            
//            sacks.add(newSack);
//        }
        
        return sacks;
    }

}
