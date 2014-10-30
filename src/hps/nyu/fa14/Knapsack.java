package hps.nyu.fa14;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a collection of items from a given catalog that
 * satisfies the constraint that the total weigh of all items is not more than capacity
 *
 * Knapsack ids are 1-indexed
 */
public class Knapsack {

    public final Catalog catalog;
    public final int capacity;
    public final int id;

    public final List<Item> items = new ArrayList<Item>();

    Knapsack(Catalog catalog, int id, int capacity) {
        this.catalog = catalog;
        this.id = id;
        this.capacity = capacity;
    }

    public int totalValue() {
        int value = 0;
        for(Item i : items){
            value += i.value;
        }
        if(catalog.problemType == 3){
            // Needs to take into account profit matrix
            int[] itemIds = new int[items.size()];
            for(int i = 0; i < itemIds.length; i++){
                itemIds[i] = items.get(i).id;
            }
            
            for(int i = 0; i < itemIds.length; i++ ){
                for(int j = i; j < itemIds.length; j++){
                    int itemId1 = Math.min(itemIds[i], itemIds[j]);
                    int itemId2 = Math.max(itemIds[i], itemIds[j]);
                    
                    // add the value of i,j profit
                    value += catalog.profitMatrix[itemId1][itemId2];
                }
            }
        }
        return value;
    }
    
    public boolean isWeightAcceptable(){
        return currentWeight() <= capacity;
    }
    
    public int currentWeight() {
        int weight = 0;
        for(Item i : items){
            weight += i.weight;
        }
        return weight;
    }
    
    public Knapsack clone(){
        Knapsack newK = new Knapsack(catalog, id, capacity);
        for(Item i : items){
            newK.items.add(i);
        }
        return newK;
    }
    
    @Override
    public String toString(){
        return String.format("[%d] Value: %d Weight: %d Fill: %f", id, totalValue(), currentWeight(), (double)currentWeight() / capacity);
    }
    
    public static void write(List<Knapsack> knapsacks, OutputStream output) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(output));
        boolean writeIds = knapsacks.size() > 1; // Only write ids if there are multiple knapsacks
        for(Knapsack k : knapsacks){
            for(Item i : k.items){
                bw.write(String.format("%d", i.id));
                if(writeIds){
                    bw.write(String.format(" %d", k.id));
                }
                bw.newLine();
            }
        }
        bw.close();
    }

    public static void writeFile(List<Knapsack> knapsacks, String filepath) throws FileNotFoundException, IOException{
        write(knapsacks, new FileOutputStream(new File(filepath)));
    }

    public static void writeFile(Knapsack knapsack, String filepath) throws FileNotFoundException, IOException{
        writeFile(Arrays.asList(knapsack), filepath);
    }
    
    public static Comparator<Knapsack> SORT_BY_CAPACITY = new Comparator<Knapsack>() {
        @Override
        public int compare(Knapsack k1, Knapsack k2) {
            return (int) Math.signum(k1.capacity - k2.capacity);
        }
    };
    
    public static Comparator<Knapsack> SORT_BY_ID = new Comparator<Knapsack>() {
        @Override
        public int compare(Knapsack k1, Knapsack k2) {
            return (int) Math.signum(k1.id - k2.id);
        }
    };
}
