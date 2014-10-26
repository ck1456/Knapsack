package hps.nyu.fa14;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

    public final int[] knapsackCapacities;

    public final int[][] profitMatrix;

    private Catalog(int type, int count, List<Integer> capacities) {
        problemType = type;
        objectCount = count;
        knapsackCapacities = new int[capacities.size()];
        for (int i = 0; i < capacities.size(); i++) {
            knapsackCapacities[i] = capacities.get(i);
        }
        profitMatrix = new int[objectCount][objectCount];
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
            for (int i = 0; i < objectCount; i++) {
                String row = br.readLine();
                String[] profits = row.split("\\s");
                if(profits.length != objectCount - i){
                    throw new RuntimeException(String.format("unexpected number of columns: %d", profits.length));
                }
                for(int j = 0; j < objectCount - i; j++){
                    c.profitMatrix[i][i+j] = Integer.parseInt(profits[j]);
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
        Item i = new Item();
        String[] tok = line.trim().split("\\s");
        i.id = Integer.parseInt(tok[0]);
        i.value = Integer.parseInt(tok[1]);
        i.weight = Integer.parseInt(tok[2]);
        return i;
    }

    private static List<Item> parseAllItems(String line) {
        // read:
        // <weight_1> <weight_2> ... <weight_n>

        List<Item> items = new ArrayList<Item>();
        int id = 1;
        for (String w : line.trim().split("\\s")) {
            Item i = new Item();
            i.id = id;
            i.value = 0; // no inherent value
            i.weight = Integer.parseInt(w);
            items.add(i);
            id++;
        }
        return items;
    }
}
