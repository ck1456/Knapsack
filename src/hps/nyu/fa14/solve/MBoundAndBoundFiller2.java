package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.IFiller;
import hps.nyu.fa14.Item;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

/**
 * Implements "bound-and-bound" solution as per Martello and Toth
 * 
 * Resources: http://www.or.deis.unibo.it/kp/Chapter6.pdf
 *            http://www.diku.dk/~pisinger/95-1.pdf
 */
public class MBoundAndBoundFiller2 extends AbstractFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {

        this.c = c;
        int n = c.objectCount;
        int m = c.knapsackCount;

        // get a list of all the items ranked by ratio
        Item[] items = new Item[n + 1];
        int index = 1;
        for (Item i : c.getItemsSortedByRatio()) {
            items[index++] = i;
        }

        // sort the capacities
        sortedKnapsacks = c.getAllEmptyKnapsacks();
        Collections.sort(sortedKnapsacks, Knapsack.SORT_BY_CAPACITY);
        int[] capacities = new int[m + 1];
        for (int k = 1; k <= m; k++) {
            capacities[k] = sortedKnapsacks.get(k-1).capacity;
        }

        mulknap(n, m, c.getItemsSortedByRatio(), capacities);
        
        // turn the solution back into knapsacks
        List<Knapsack> solution = getCurrentSolution();
        updateIfBest(solution);
        return solution;
    }
    
    private List<Knapsack> getCurrentSolution(){
        List<Knapsack> sacks = new ArrayList<Knapsack>();
        for(int k = 1; k <= m; k++){
            sacks.add(sortedKnapsacks.get(k-1).clone());
            for(int j = 1; j <= n; j++){
                if(x.get(k).get(allItems[j])){
                    sacks.get(k-1).items.add(allItems[j]);
                }
            }
        }
        return sacks;
    }
    
    private List<Knapsack> bestSolution;
    private int bestValue;
    
 // TODO: refactor this into base class
    private void updateIfBest(List<Knapsack> s) {
        synchronized (c) {
            int newValue = 0;
            for(Knapsack k : s){
                newValue += k.totalValue();
            }
            if (newValue > bestValue) {
                bestSolution = s;
                bestValue = newValue;
                notifyNewSolution(bestSolution);
            }
        }
    }

    private Catalog c;
    private List<Knapsack> sortedKnapsacks;
    private Item[] allItems;
    private Map<Item, Integer> indexMap = new HashMap<Item, Integer>();
    private int z;
    private int n;
    private int m;

    private Map<Integer, Map<Item, Boolean>> x = new HashMap<Integer, Map<Item, Boolean>>();
    private Map<Integer, Map<Item, Boolean>> y = new HashMap<Integer, Map<Item, Boolean>>();
    
    private Map<Item, Integer> d = new HashMap<Item, Integer>();
    
    private void mulknap(int n, int m, List<Item> items, int[] capacities){
        
        this.n = n;
        this.m = m;
        allItems = new Item[n+1];
        for(int j = 1; j <= n; j++){
            allItems[j] = items.get(j-1);
        }
        buildIndexMap();
        for(Item j : items){
            d.put(j, 1);
        }
        
        for(int i = 1; i <= m; i++){
            x.put(i, new HashMap<Item, Boolean>());
            for(Item j : items){
                x.get(i).put(j, false);
            }
        }
        
        for(int i = 1; i <= m; i++){
            y.put(i, new HashMap<Item, Boolean>());
            for(Item j : items){
                y.get(i).put(j, false);
            }
        }
        
        z = 0;
        
        mulbranch(0, 0, 0, capacities);
    }
    
    private void buildIndexMap(){
        indexMap.clear();
        for(int i = 1; i <= n; i++){
            indexMap.put(allItems[i], i);
        }
    }
    
    private void mulbranch(int h, int P, int W, int[] capacities){
        //System.out.println(String.format("mulbranch(%d, %d, %d, ...)", h, P, W));
        for (int i = 1; i <= m; i++) {
            // get free variables
            List<Item> items = new ArrayList<Item>();
            for (int j = h + 1; j <= n; j++) {
                Item item_j = allItems[j];
                if (d.get(item_j) <= i) {
                    items.add(item_j);
                }
            }
            capacities[i] = tightenCapacity(capacities[i], items);
        }
        
        int sumCapacity = 0;
        for(int c: capacities){
            sumCapacity += c;
        }
        
        List<Item> items = new ArrayList<Item>();
        for(int j = h+1; j <= n; j++){
            items.add(allItems[j]);
        }
        
        Item[] breakItem = new Item[1];
        List<Item> x_items = new ArrayList<Item>();
                
        int u = surrogateRelaxed(items, sumCapacity, x_items, breakItem);
        //System.out.println("UpperBound " + u);
        
        if(P + u > z){
            List<List<Item>> y_ = splitAcrossKnapsacks(x_items, capacities);
            
            int z_sum = 0;
            for(List<Item> knapsack : y_){
                for(Item i : knapsack){
                    z_sum += i.value;
                }
            }
            //System.out.println("Optimal profit sum " + z_sum);
            // TODO: Improve the heuristic with some more greedy filling

            //System.out.println(String.format("Update new solution? %d + %d = %d > %d", P , z_sum, P + z_sum, z));
            if(P + z_sum > z){
                // clear out all values in y > h
                for(int i = 1; i <= m; i++){
                    for(int j = h + 1; j <= n; j++){
                        y.get(i).put(allItems[j], false);
                    }
                }
                // Copy solution items to y
                for(int i = 1; i <= y_.size(); i++){
                    List<Item> y_i = y_.get(i-1);
                    for(Item j : y_i){
                        y.get(i).put(j, true);
                    }
                }
                // check that this matches the solution above
                //evaluateSolution(y);
                
                // Copy y to x
                // **************** NEW SOLUTION ***************
                for(int i = 1; i <= m; i++){
                    for(int j = 1; j <= n; j++){
                        Item item_j = allItems[j];
                        x.get(i).put(item_j, y.get(i).get(item_j));
                    }
                }
                //System.out.println("Update z: " + z + " --> " + (P + z_sum));
                z = P + z_sum;
                updateIfBest(getCurrentSolution());
            }
        }

        // solution does not meet upper-bound
        //System.out.println(String.format("Solution meets upper bound? %d + %d = %d > %d", P , u, P + u, z));
        if(P + u > z){
            // Reduce items
            h = reduceItems(h, sumCapacity, breakItem[0]);
            // Find the smallest knapsack
            int i = -1;
            int c_i = Integer.MAX_VALUE;
            for(int c = 1; c <= m; c++){
                if(capacities[c] > 0 && capacities[c] < c_i){
                    c_i = capacities[c];
                    i = c;
                }
            }
            
            // Solve ordinary knapsack with c_i
            Item item_l = solveKPFor_l(h, c_i);
            
            // swap el to position h+1
            int el_index = indexMap.get(item_l);
            Item temp = allItems[h+1];
            allItems[h+1] = allItems[el_index];
            allItems[el_index] = temp;
            buildIndexMap();  // make sure you can find these items later
            
            int j = h+1;
            Item item_j = allItems[j];
            //System.out.println(">>> Branch: Include " + j);
            y.get(i).put(item_j, true); // assign item j to knapsack i
            int[] newCapacities = Arrays.copyOf(capacities, m+1);
            int p_j = item_j.value;
            int w_j = item_j.weight;
            newCapacities[i] = newCapacities[i] - w_j;
            mulbranch(h+1, P+p_j, W + w_j, newCapacities);
            
            //System.out.println("<<< Branch: Exclude " + j);
            y.get(i).put(item_j, false); // exclude item j from knapsack i
            int d_ = d.get(item_j);
            d.put(item_j, i+1);
            mulbranch(h, P, W, capacities);
            // Find j again, and set d_j = d_
            j = indexMap.get(item_j);
            d.put(item_j, d_);
        }
        
        
    }
    
    private void evaluateSolution(Map<Integer, Map<Item, Boolean>> y){
        List<List<Item>> sol = new ArrayList<List<Item>>();
        for(int i = 1; i <= m; i++){
            List<Item> k = new ArrayList<Item>();
            for(int j = 1; j <= n; j++){
                Item item_j = allItems[j];
                if(y.get(i).get(item_j)){
                    k.add(item_j);
                }
            }
            sol.add(k);
        }
        for(int i = 1; i <=m; i++){
            int w_k = 0;
            int p_k = 0;
            for(Item j : sol.get(i-1)){
                w_k += j.weight;
                p_k += j.value;
            }
            System.out.println(String.format("[%d] Weight %d Value: %d", i, w_k, p_k));
        }
    }
    
    private Item solveKPFor_l(int h, int capacity){
        
        //System.out.println("SolveKP: " + capacity + " for h=" + h);
        //Get free items
        List<Item> items = new ArrayList<Item>();
        for(int j = h; j <= n; j++){
            items.add(allItems[j]);
        }
        
        Catalog c = new Catalog(1, items.size(), Arrays.asList(capacity));
        for(Item i : items){
            c.items.put(i.id, i);
        }
        IFiller f = new ExpandingCoreFiller();
        Knapsack k = f.fill(c).get(0);
        // get the item with the highest p/w ratio
        Item best = null;
        double bestRatio = 0;
        for(Item i : k.items){
            double ratio = (double)i.value / i.weight;
            if(ratio > bestRatio){
                bestRatio = ratio;
                best = i;
            }
        }
        //System.out.println("Best " + best);
        return best;
    }
    
    // Reduce items by using some upper bound tests and swap the reduced items into
    // the first positions, increasing h; 
    private int reduceItems(int h, int c, Item breakItem){
        //System.out.println(String.format("reduceItems(%d, %d, [%s])", h, c, breakItem));
        //int priorH = h;
        // find the breakItem
        int p_b = 0;
        int w_b = 0;
        
        for(int j = h + 1; j <= n; j++){
            
            int b = n+1;
            if(breakItem != null){
                b = indexMap.get(breakItem);
                p_b = breakItem.value;
                w_b = breakItem.weight;
            }
            
            // calculate u_1
            int p_sum = 0;
            int w_sum = 0;
            for(int i = h+1; i < b; i++){
                p_sum += allItems[i].value;
                w_sum += allItems[i].weight;
            }
            int p_j = allItems[j].value;
            int w_j = allItems[j].weight;
            int remaining = c - w_sum - w_j;
            
            int u_1 = p_sum + p_j;
            if(p_b > 0){
                u_1 += ((double)remaining * ((double)p_b / w_b));
            }
            
            u_1 = (int)Math.floor(u_1);
            
            if(u_1 <= z){
                //System.out.println(String.format("%d <= %d so swap j: %d <--> %d", u_1, z, j, h+1));
                // swap this item to the front
                Item temp = allItems[h+1];
                allItems[h+1] = allItems[j];
                allItems[j] = temp;
                buildIndexMap(); // keep in sync with allItems
                h++;
                j++;
            }
        }
        
        //System.out.println(String.format("Reduced items: %d --> %d", priorH, h));
        return h;
    }
    
    // shrink
    private static int tightenCapacity(int c, List<Item> items) {

        int[] weights = new int[items.size()];
        for (int i = 0; i < items.size(); i++) {
            weights[i] = items.get(i).weight;
        }

        //System.out.println(String.format("%d x %d", c, weights.length));
        if((double)c * weights.length > 1e8 ){
            return c; // Cannot tighten in memory
        }
        int newC = 0;
        for (int index : getMaxValue(c, weights, weights.length)) {
            newC += items.get(index).weight;
        }

        //System.out.println(String.format("Tighten capacity: %d -> %d", c, newC));
        return newC;
    }
    
    // provides an upperbound and returns the objective value
    private int surrogateRelaxed(List<Item> items, int capacity, List<Item> x_items, Item[] breakItem){
        // order and greedily fill to find a feasible solution 
        Catalog c = new Catalog(1, items.size(), Arrays.asList(capacity));
        for(Item i : items){
            c.items.put(i.id, i);
        }
        GreedyFiller f = new GreedyFiller();
        Knapsack k = f.fill(c).get(0);
        
        //System.out.println("Surrogate relaxed upper bound: " + k.totalValue() + " on " + items.size() + " items");
        List<Item> sorted = c.getItemsSortedByRatio();
        for(Item i : k.items){
            sorted.remove(i);
        }
        int u = k.totalValue();
        if(sorted.size() > 0){
            breakItem[0] = sorted.get(0);
            int capacityRemaining = capacity - k.currentWeight();
            u += (int)((double)breakItem[0].value * ((double)capacityRemaining / breakItem[0].weight));
            //System.out.println(String.format("Surrogate relaxed breakItem : %d | %d", breakItem[0].value, breakItem[0].weight));
            //System.out.println("Object value (upper Bound u): " + u);
            k.items.add(breakItem[0]);
        } else {
            //System.out.println("Out of objects, breakItem will be null");
        }
        x_items.addAll(k.items);
        return u;
    }
    
    
    // capacities must be sorted
    private static List<List<Item>> splitAcrossKnapsacks(List<Item> items,
            int[] capacities) {

        List<List<Item>> splits = new ArrayList<List<Item>>();
        
        for (int c = 1; c < capacities.length; c++) {
            splits.add(new ArrayList<Item>());
            int weight[] = new int[items.size()];
            for (int i = 0; i < items.size(); i++) {
                weight[i] = items.get(i).weight;
            }
            
            //System.out.println(String.format("%d x %d", capacities[c], weight.length));
            
            List<Integer> includedIndexes = getMaxValue(capacities[c], weight, weight.length);
            for(int index : includedIndexes){
                splits.get(c-1).add(items.get(index));
            }
            for(int index : includedIndexes){
                items.remove(index);
            }
        }
        for(int c = 1; c < capacities.length; c++){
            int total = 0;
            for(Item i : splits.get(c-1)){
                total += i.weight;
            }
            //System.out.println(String.format("Split [%d] Items: %d Total: %d", capacities[c], splits.get(c-1).size(), total));
        }
        
        return splits;
    }

    private static List<Integer> getMaxValue(int W, int weight[], int n) {

        // returns a list of included indexes

        // Stores optimal values for partial solutions to the knapsack
        // subproblems
        int[][] K = new int[n + 1][W + 1];

        // In order to reconstruct the picked items we need to keep track of
        // whether it was chosen or not at each position
        // 0 -> not considered
        // 1 -> selected
        // -1 -> not selected
        byte[][] picked = new byte[n + 1][W + 1];

        for (int i = 0; i <= n; i++) {
            for (int w = 0; w <= W; w++) {
                if (i == 0 || w == 0) {
                    // If there are no objects or the capacity is 0,
                    // then you can't put anything in the knapsack, initialize
                    // to 0
                    K[i][w] = 0;
                } else if (weight[i - 1] <= w) {
                    // If the i-th item is less than the total capacity, choose
                    // the best of
                    // including i-th item
                    // excluding i-th item
                    int includeValue = weight[i - 1]
                            + K[i - 1][w - weight[i - 1]];
                    int excludeValue = K[i - 1][w];
                    if (includeValue > excludeValue) {
                        K[i][w] = includeValue;
                        picked[i][w] = 1;
                    } else {
                        K[i][w] = excludeValue;
                        picked[i][w] = -1;
                    }
                } else {
                    // otherwise the i-th item doesn't fit, don't include it
                    K[i][w] = K[i - 1][w];
                    picked[i][w] = -1;
                }
            }
        }

        // print the max value
        // System.out.println("Max Value: " + K[n][W]);

        // work backwards to reconstruct which elements are in the solution
        List<Integer> indexes = new ArrayList<Integer>();
        int itemP = n;
        int size = W;
        while (itemP > 0) {
            if (picked[itemP][size] == 1) {
                indexes.add(itemP - 1);
                itemP--; // decrement the item to make a decision about it
                size -= weight[itemP];
            } else {
                itemP--;
            }
        }

        return indexes;
    }

}
