package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.IFiller;
import hps.nyu.fa14.Item;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Constructs a small core problem centered around the break item
 * and solves smaller sub problems
 */
public class ExpandingCoreFiller extends AbstractFiller {
    @Override
    public List<Knapsack> fill(Catalog c) {
        List<Knapsack> sacks = new ArrayList<Knapsack>();

        Knapsack breakSolution = c.getEmptyKnapsack(1);
        
        // get a list of all the items
        List<Item> items = c.getItemsSortedByRatio();

        int breakItem = 0;
        Item nextItem = items.get(breakItem);

        // while it is not full, add something new
        while (breakSolution.currentWeight() + nextItem.weight <= breakSolution.capacity) {
            breakSolution.items.add(nextItem);
            breakItem++;
            nextItem = items.get(breakItem);
        }
        //System.out.println("breakItem:" + breakItem);
       
        // A good lower bound
        bestSolution = breakSolution;
        
        // TODO: Pick these a little better
        for(int core = 50; core < 250; core += 50){
            
            // Defines an interval to explore
            int iStart = Math.max(breakItem - core, 0);
            int iEnd = Math.min(breakItem + (2*core), items.size() - 1);
            int newCapacity = breakSolution.capacity - breakSolution.currentWeight();
            for(int i = iStart; i < breakItem; i++ ){
                newCapacity += items.get(i).weight;
            }
            Catalog newC = new Catalog(1, (iEnd - iStart) + 1, Arrays.asList(newCapacity));
            for(int i = iStart; i <= iEnd; i++ ){
                Item item = items.get(i);
                newC.items.put(item.id, item);
            }
            
            // Try the best of these two
            // Sometimes one works much better than the other
            FirstFiller firstToFinish = new FirstFiller();
            firstToFinish.addFiller(new DynamicProgrammingFiller());
            firstToFinish.addFiller(new BranchAndBoundFiller());
            IFiller subFill = firstToFinish;
            
            List<Knapsack> subSolutions = subFill.fill(newC);
            Knapsack subK = subSolutions.get(0);
            
            // Combine with non-core solution
            updateBest(makeSolution(breakSolution, core, subK));
            // TODO: Consider termination condition if fill ratio is 1.0
            if(subK.currentWeight() == subK.capacity){
                // Filled exactly, abort
                break;
            }
        }
        
        sacks.add(bestSolution);
        return sacks;
    }


    private int bestValue = 0;
    private Knapsack bestSolution;
    
    private void updateBest(Knapsack k){
        int value = k.totalValue();
        if(value > bestValue){
            bestValue = value;
            bestSolution = k;
            //System.out.println("Best: " + bestSolution);
            notifyNewSolution(Arrays.asList(k));
        }
    }
    
    private Knapsack makeSolution(Knapsack breakSolution, int removeCount, Knapsack subSolution){
        
        Knapsack newK = breakSolution.clone();
        for(int i = 0; i < removeCount; i++){
            newK.items.remove(newK.items.size()-1);
        }
        for(Item i : subSolution.items){
            newK.items.add(i);
        }
        return newK;
    }
}
