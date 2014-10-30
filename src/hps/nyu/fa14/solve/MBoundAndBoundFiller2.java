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

/**
 * Implements "bound-and-bound" solution as per Martello and Toth
 * 
 * Resources: http://www.or.deis.unibo.it/kp/Chapter6.pdf
 */
public class MBoundAndBoundFiller2 extends AbstractFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {

        int n = c.objectCount;
        int m = c.knapsackCount;

        // get a list of all the items ranked by ratio
        Item[] items = new Item[n + 1];
        int index = 1;
        for (Item i : c.getItemsSortedByRatio()) {
            items[index++] = i;
        }

        // sort the capacities
        int[] capacities = new int[m + 1];
        List<Integer> sortC = new ArrayList<Integer>();
        for (int k = 1; k <= m; k++) {
            sortC.add(c.knapsackCapacities[k]);
        }
        Collections.sort(sortC);
        for (int i = 0; i < sortC.size(); i++) {
            capacities[i + 1] = sortC.get(i);
        }

        mulknap(n, m, c.getItemsSortedByRatio(), capacities);
        
        // turn the solution back into knapsacks
        List<Knapsack> sacks = new ArrayList<Knapsack>();
        for(Knapsack k : c.getAllEmptyKnapsacks()){
            for(int j = 1; j <= n; j++){
                if(x[k.id][j]){
                    k.items.add(c.items.get(j));
                }
            }
            sacks.add(k);
        }
        
        return sacks;
    }

    private Item[] allItems;
    private Map<Item, Integer> indexMap = new HashMap<Item, Integer>();
    private int z;
    private int n;
    private int m;
    private boolean[][] x;
    private boolean[][] y;
    //private int[] d_j;
    private Map<Item, Integer> d = new HashMap<Item, Integer>();
    
    private void mulknap(int n, int m, List<Item> items, int[] capacities){
        
        this.n = n;
        this.m = m;
//        d_j = new int[n + 1];
//        for(int j = 1; j <= n; j++){
//            d_j[j] = 1;
//        }
        allItems = new Item[n+1];
        for(int j = 1; j <= n; j++){
            allItems[j] = items.get(j-1);
        }
        buildIndexMap();
        for(Item i : items){
            d.put(i, 1);
        }
        
        x = new boolean[m + 1][n + 1];
        y = new boolean[m + 1][n + 1];
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
        List<Item> x_items = surrogateRelaxed(items, sumCapacity, breakItem);
                
        int u = 0;
        for(Item i : x_items){
            u += i.value;
        }
        
        if(P + u > z){
            List<List<Item>> y_ = splitAcrossKnapsacks(x_items, capacities);
            
            int z_sum = 0;
            for(List<Item> knapsack : y_){
                for(Item i : knapsack){
                    z_sum += i.value;
                }
            }
            
            // TODO: Improve the heuristic with some more greedy filling
            
            if(P + z_sum > z){
                // clear out all values in y > h
                for(int i = 1; i <= m; i++){
                    for(int j = h + 1; j <= n; j++){
                        y[i][j] = false;
                    }
                }
                // Copy solution items to y
                for(int i = 1; i <= y_.size(); i++){
                    List<Item> y_i = y_.get(i-1);
                    for(Item j : y_i){
                        int j_id = indexMap.get(j);
                        // Need to set other things to false
                        y[i][j_id] = true;
                    }
                }
                
                // Copy y to x
                for(int i = 1; i <= m; i++){
                    for(int j = 1; j <= n; j++){
                        x[i][j] = y[i][j];
                    }
                }
                
                z = P + z_sum;
            }
        }

        // solution does not meet upper-bound
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
            y[i][j] = true; // assign item j to knapsack i
            int[] newCapacities = Arrays.copyOf(capacities, m+1);
            Item item_j = allItems[j];
            int p_j = item_j.value;
            int w_j = item_j.weight;
            newCapacities[i] = newCapacities[i] - w_j;
            mulbranch(h+1, P+p_j, W + w_j, newCapacities);
            y[i][j] = false; // exclude item j from knapsack i
            int d_ = d.get(item_j);
            d.put(item_j, i+1);
            mulbranch(h, P, W, capacities);
            // Find j again, and set d_j = d_
            j = indexMap.get(item_j);
            d.put(item_j, d_);
        }
        
        
    }
    
    private Item solveKPFor_l(int h, int capacity){
        
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
        return best;
    }
    
    // Reduce items by using some upper bound tests and swap the reduced items into
    // the first positions, increasing h; 
    private int reduceItems(int h, int c, Item breakItem){
        
        // find the breakItem
        int b = indexMap.get(breakItem);
        for(int j = h; j <= n; j++){
            
            // calculate u_1
            int p_sum = 0;
            int w_sum = 0;
            for(int i = h; i < b; i++){
                p_sum += allItems[i].value;
                w_sum += allItems[i].weight;
            }
            int p_j = allItems[j].value;
            int w_j = allItems[j].weight;
            int remaining = c - w_sum - w_j;
            
            int u_1 = (int)Math.floor(p_sum + p_j + ((double)remaining * ((double)breakItem.value / breakItem.weight)));
            if(u_1 <= z){
                // swap this item to the front
                Item temp = allItems[h];
                allItems[h] = allItems[j];
                allItems[j] = temp;
                buildIndexMap(); // keep in sync with allItems
                h++;
            }
        }
        
        return h;
    }
    
    // shrink ... and then reorder?
    private static int tightenCapacity(int c, List<Item> items) {

        int[] weights = new int[items.size()];
        for (int i = 0; i < items.size(); i++) {
            weights[i] = items.get(i).weight;
        }

        int newC = 0;
        for (int index : getMaxValue(c, weights, weights.length)) {
            newC += items.get(index).weight;
        }

        return newC;
    }
    
    // provides a lowerbound and feasible solution
    private List<Item> surrogateRelaxed(List<Item> items, int capacity, Item[] breakItem){
        // order and greedily fill to find a feasible solution 
        Catalog c = new Catalog(1, items.size(), Arrays.asList(capacity));
        for(Item i : items){
            c.items.put(i.id, i);
        }
        GreedyFiller f = new GreedyFiller();
        Knapsack k = f.fill(c).get(0);
        
        List<Item> sorted = c.getItemsSortedByRatio();
        for(Item i : k.items){
            sorted.remove(i);
        }
        breakItem[0] = sorted.get(0);
        return k.items;
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
            
            List<Integer> includedIndexes = getMaxValue(capacities[c], weight, weight.length);
            for(int index : includedIndexes){
                splits.get(c-1).add(items.get(index));
            }
            for(int index : includedIndexes){
                items.remove(index);
            }
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
