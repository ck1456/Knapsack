package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.Item;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Implements combination of heuristics plus local search
 * 
 * Resources: http://www.lancaster.ac.uk/staff/letchfoa/articles/qkp1.pdf
 */
public class QDynamicProgrammingFiller extends AbstractFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {

        List<Item> orderedItems = orderUpperPlane3_W(c);
        Knapsack k = algo3(c, orderedItems);

//        System.out.println(k);
        return Arrays.asList(k);
    }

    // calculates upper plane defined by pi_i^3
    private static List<Item> orderUpperPlane3_W(Catalog c) {

        int n = c.objectCount;
        int capacity = c.knapsackCapacities[1];

        PItem[] pItems = new PItem[n];
        int index = 0;
        for (Item i : c.items.values()) {
            pItems[index++] = new PItem(i);
        }
        // Calculate the upper plane
        for (int i = 0; i < n; i++) {
            int i_id = pItems[i].item.id;
            int p_i = c.profitMatrix[i_id][i_id];

            // Construct an appropriate CKP and then solve for upper bound
            List<Item> subItems = new ArrayList<Item>();
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    int j_id = pItems[j].item.id;
                    int j_value = c.profitMatrix[i_id][j_id]
                            + c.profitMatrix[j_id][i_id];
                    subItems.add(new Item(j_id, j_value, pItems[j].item.weight));
                }
            }
            // Sort sub items by non-increasing v/w
            Collections.sort(subItems, Item.RANK_BY_RATIO);
            Collections.reverse(subItems);

            // get upper bound
            p_i += getUpperBound_CKP(subItems, capacity);
            pItems[i].p = p_i;
            pItems[i].p_w = (double) pItems[i].p / pItems[i].item.weight;
        }

        List<Item> sortedItems = new ArrayList<Item>();
        // sort these items by p_w
        Arrays.sort(pItems, PItem.RANK_BY_P_W);
        for (int i = 0; i < n; i++) {
            sortedItems.add(pItems[i].item);
        }
        return sortedItems;
    }

    // calculates a feasible solution with a good lower bound
    private Knapsack algo3(Catalog c, List<Item> orderedItems) {

        Knapsack knapsack = c.getEmptyKnapsack(1);
        int capacity = knapsack.capacity;
        int n = c.objectCount;

        int[] f = new int[capacity + 1];
        boolean[][] B = new boolean[capacity + 1][n + 1];

        for (int k = 1; k <= n; k++) {
            int k_id = orderedItems.get(k - 1).id;
            int w_k = orderedItems.get(k - 1).weight;
            for (int r = capacity; r >= 0; r--) {
                if (r >= w_k) {
                    int q_kk = c.profitMatrix[k_id][k_id];
                    int b_sum = 0;
                    for (int i = 1; i < k; i++) {
                        if (B[r - w_k][i]) {
                            int i_id = orderedItems.get(i - 1).id;
                            // half the matrix is 0 so this is OK
                            b_sum += (c.profitMatrix[i_id][k_id] + c.profitMatrix[k_id][i_id]);
                        }
                    }
                    // profit
                    int beta = f[r - w_k] + q_kk + (b_sum);
                    if (beta > f[r]) {
                        f[r] = beta;
                        B[r][k] = true;
                    }
                }
            }
        }
        // Find r_star
        int maxValue = 0;
        int r_star = -1;
        for (int r = 0; r < f.length; r++) {
            if (f[r] > maxValue) {
                maxValue = f[r];
                r_star = r;
            }
        }

        // Construct set of included items
        int r = r_star;
        for (int i = n; i >= 1; i--) {
            if (B[r][i]) {
                int i_id = orderedItems.get(i - 1).id;
                int weight = orderedItems.get(i - 1).weight;
                knapsack.items.add(c.items.get(i_id));
                r -= weight;
            } else {
                
            }
        }
        return knapsack;
    }

    private static int getUpperBound_CKP(List<Item> rankedItems, int capacity) {
        int upperBound = 0;
        int i = 0;
        while (capacity > 0 && i < rankedItems.size()) {
            Item item_i = rankedItems.get(i);
            // add the portion of item_i that will fit (and round up)
            int amount = (int) Math.ceil(item_i.value
                    * Math.min(1.0, (double) capacity / item_i.weight));
            capacity -= item_i.weight;
            upperBound += amount;
            i++;
        }
        return upperBound;
    }

    private static class PItem {
        public final Item item;
        public int p;
        public double p_w;

        public PItem(Item i) {
            item = i;
        }

        public static Comparator<PItem> RANK_BY_P_W = new Comparator<PItem>() {
            @Override
            public int compare(PItem i1, PItem i2) {
                return (int) Math.signum(i2.p_w - i1.p_w);
            }
        };
    }
}
