package hps.nyu.fa14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Catalog {

    public final Map<Integer, Item> items = new HashMap<Integer, Item>();

    /**
     * Specifies the problem type: 1 -> Standard 0-1 knapsack problem 2 ->
     * Multiple knapsack problem 3 -> Quadratic knapsack problem
     */
    public final int problemType;

    public final int objectCount;

    public final int knapsackCount;
    
    /**
     * Knapsack ids are 1-indexed, so this array contains one more element than absolutely necessary
     */
    public final int[] knapsackCapacities;

    /**
     * Object ids are 1-indexed, so this matrix contains one more row and column than absolutely necessary
     */
    public final int[][] profitMatrix;

    public Catalog(int type, int count, List<Integer> capacities) {
        problemType = type;
        objectCount = count;
        knapsackCount = capacities.size();
        knapsackCapacities = new int[knapsackCount + 1];
        for (int i = 0; i < knapsackCount; i++) {
            knapsackCapacities[i + 1] = capacities.get(i);
        }
        profitMatrix = new int[objectCount + 1][objectCount + 1];
    }

    /**
     * Returns a new instance of a knapsack with the specified id
     * that reflects the capacity and constraints of the problem 
     */
    public Knapsack getEmptyKnapsack(int id){
        return new Knapsack(this, id, knapsackCapacities[id]);
    }
    
    public List<Knapsack> getAllEmptyKnapsacks(){
        List<Knapsack> allSacks = new ArrayList<Knapsack>();
        for(int i = 1; i <= knapsackCount; i++){
            allSacks.add(getEmptyKnapsack(i));
        }
        return allSacks;
    }
    
    public List<Item> getItemsSortedByRatio(){
        // get a list of all the items
        List<Item> items = new ArrayList<Item>();
        for (int i : this.items.keySet()) {
            items.add(this.items.get(i));
        }
        // sort it by ratio value/weight in descending order
        Collections.sort(items, Item.RANK_BY_RATIO);
        Collections.reverse(items);
        return items;
    }
    
    public static Catalog parseFile(String filepath) throws IOException {
        return parse(new FileInputStream(new File(filepath)));
    }

    public static Catalog parse(InputStream input) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(input));

        // read the first line as the problem type
        int problemType = Integer.parseInt(br.readLine());
        // read the second line as the total number of objects / knapsacks
        String line2 = br.readLine();
        String[] counts = line2.split("\\s");
        // int knapsackCount = 1; // by default

        int objectCount = Integer.parseInt(counts[0]);
        // if(problemType == 2){
        // knapsackCount = Integer.parseInt(counts[1]);
        // }

        // parse knapsack capacities (knapsack count is given implicitly
        String line3 = br.readLine();
        List<Integer> capacities = new ArrayList<Integer>();
        for (String cap : line3.split("\\s")) {
            capacities.add(Integer.parseInt(cap));
        }

        Catalog c = new Catalog(problemType, objectCount, capacities);

        switch (c.problemType) {
        case 1:
        case 2: {
            // parse all of the objects
            String line;
            while ((line = br.readLine()) != null) {
                Item i = parseItem(line);
                c.items.put(i.id, i);
            }
            break;
        }
        case 3: {
            // parse weights of all items
            for (Item i : parseAllItems(br.readLine())) {
                c.items.put(i.id, i);
            }
            // read blank line
            String blank = br.readLine();
            if(!blank.trim().equals("")){
                throw new RuntimeException("Expected blank line in input");
            }
            // parse matrix of combination profits
            for (int i = 1; i <= objectCount; i++) {
                String row = br.readLine();
                String[] profits = row.split("\\s");
                if(profits.length != (objectCount + 1) - i){
                    throw new RuntimeException(String.format("unexpected number of columns: %d", profits.length));
                }
                // NOTE: This stores the matrix internally in a different representation than in the file
                // but this follows the literature better
                if(i == 1){ // Rotate the first row down to the diagonal
                    for (int j = 0; j < objectCount - i + 1; j++) {
                        c.profitMatrix[j + 1][j + 1] = Integer.parseInt(profits[j]);
                    }
                } else {
                    for (int j = 0; j < objectCount - i + 1; j++) {
                        c.profitMatrix[i - 1][i + j] = Integer.parseInt(profits[j]);
                    }
                }
            }
            break;
        }
        default:
            throw new IndexOutOfBoundsException("Unknown problem type");
        }
        return c;
    }

    private static Item parseItem(String line) {
        // read:
        // <id> <value> <weight>
        String[] tok = line.trim().split("\\s");
        Item i = new Item(Integer.parseInt(tok[0]), Integer.parseInt(tok[1]), Integer.parseInt(tok[2]));
        return i;
    }

    private static List<Item> parseAllItems(String line) {
        // read:
        // <weight_1> <weight_2> ... <weight_n>
        List<Item> items = new ArrayList<Item>();
        int id = 1;
        for (String w : line.trim().split("\\s")) {
            Item i = new Item(id, 0, Integer.parseInt(w)); // no inherent value
            items.add(i);
            id++;
        }
        return items;
    }
}
