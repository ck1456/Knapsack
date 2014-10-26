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
import java.util.List;

public class Knapsack {

    public final int capacity;
    public final int id;

    public final List<Item> items = new ArrayList<Item>();

    public Knapsack(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
    }

    public int totalValue() {
        int value = 0;
        // TODO: Calculate the value
        // Needs to take into account profit matrix
        for(Item i : items){
            value += i.value;
        }
        
        return value;
    }
    
    public int currentWeight() {
        int weight = 0;
        for(Item i : items){
            weight += i.weight;
        }
        return weight;
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
}
