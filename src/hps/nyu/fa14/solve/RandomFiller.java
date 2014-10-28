package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomFiller extends AbstractFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {
        
        List<Knapsack> sacks = new ArrayList<Knapsack>();
        // create an array and then permute it
        Integer[] itemIds = new Integer[c.objectCount];
        for(int i = 0; i < itemIds.length; i++){
            itemIds[i] = i + 1;
        }
        // get a permutation of the input ids
        permute(itemIds);
        
        int item = 0;
        // Generate as many knapsacks as necessary
        for(Knapsack k : c.getAllEmptyKnapsacks()){            
            // while it is not full, add something new
            while(k.currentWeight() < k.capacity){
                k.items.add(c.items.get(itemIds[item++]));
            }
            // remove the last item, because that made it go over
            k.items.remove(c.items.get(itemIds[--item]));
            
            sacks.add(k);
        }
        return sacks;
    }
    
    // Implements Fisher-Yates:
    // http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
    private static <T> void permute(T[] input) {
        Random rand = new Random();
        for (int i = input.length - 1; i > 1; i--) {
            int swapIndex = rand.nextInt(i + 1);
            T swapValue = input[swapIndex];
            input[swapIndex] = input[i];
            input[i] = swapValue;
        }
    }
}
