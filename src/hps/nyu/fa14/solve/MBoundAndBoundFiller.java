package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.IFiller;
import hps.nyu.fa14.Item;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implements "bound-and-bound" solution as per Martello and Toth
 * 
 * Resources: http://www.or.deis.unibo.it/kp/Chapter6.pdf
 */
public class MBoundAndBoundFiller extends AbstractFiller {

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
        for (int k_c : c.knapsackCapacities) {
            sortC.add(k_c);
        }
        Collections.sort(sortC);
        for (int i = 0; i < sortC.size(); i++) {
            capacities[i + 1] = sortC.get(i);
        }

        boolean[][] solution = new boolean[m + 1][n + 1];

        mtm(c.objectCount, c.knapsackCount, items, capacities, solution);

        // turn the solution back into knapsacks
        return null;
    }
    

    private static int upper(int n, int m, Item[] items, int[] capacities,
            boolean[][] x_, List<List<Integer>> S, int i) {
        // calculate c_bar
        int c_bar = 0;
        int filled = 0;
        List<Integer> S_i = S.get(i - 1);
        for (int j : S_i) {
            if (x_[i][j]) {
                filled += items[j].weight;
            }
        }

        int other_c = 0;
        for (int k = i + 1; k <= m; k++) {
            other_c += capacities[k];
        }
        c_bar = capacities[i] - filled + other_c;

        // calculate N_bar
        List<Integer> N_bar = new ArrayList<Integer>();
        for (int j = 1; j <= n; j++) {
            boolean assigned = false;
            for (int k = 1; k <= i; k++) {
                // get totally unassigned items
                assigned = (assigned || x_[k][j]);
            }
            if (!assigned) {
                N_bar.add(j);
            }
        }

        int U = 0;
        for (int k = 1; k <= i; k++) {
            List<Integer> S_k = S.get(k - 1);
            for (int j : S_k) {
                if (x_[k][j]) {
                    U += items[j].value;
                }
            }
        }
        // Solve KP for N_bar and c_bar
        boolean[] soln = new boolean[n + 1];
        int z_ = solveSKP(N_bar, c_bar, items, soln);
        U += z_;

        return U;
    }

    private static int lower(int n, int m, Item[] items, int[] capacities,
            boolean[][] x_, List<List<Integer>> S, int i, boolean[][] x_new) {

        // Initialize L
        int L = 0;
        for (int k = 1; k <= i; k++) {
            List<Integer> S_k = S.get(k - 1);
            for (int j : S_k) {
                if (x_[k][j]) {
                    L += items[j].value;
                }
            }
        }

        List<Integer> N_p = new ArrayList<Integer>();
        for (int j = 1; j <= n; j++) {
            boolean assigned = false;
            for (int k = 1; k <= i; k++) {
                // get totally unassigned items
                assigned = (assigned || x_[k][j]);
            }
            if (!assigned) {
                N_p.add(j);
            }
        }

        List<Integer> N_bar = new ArrayList<Integer>(N_p);
        List<Integer> S_i = S.get(i - 1);
        for (int j : S_i) {
            N_bar.remove(j);
        }

        // calculate first capacity
        int weight_S_i = 0;
        for (int j : S_i) {
            if (x_[i][j]) {
                weight_S_i += items[j].weight;
            }
        }
        int c_bar = capacities[i] - weight_S_i;

        int k = 1;
        do {
            int z_ = solveSKP(N_bar, c_bar, items, x_new[k]);
            L += z_;
            // remove items assigned to k
            for (int j = 0; j <= n; j++) {
                if (x_new[k][j]) {
                    // remove this item (as an object because of the index overload)
                    N_p.remove(Integer.valueOf(j));
                }
            }

            N_bar = N_p;
            k++;
            c_bar = capacities[k];
        } while (k <= m);

        return L;
    }

    private static int mtm(int n, int m, Item[] items, int[] capacities,
            boolean[][] x) {

        // initialize
        List<List<Integer>> S = new ArrayList<List<Integer>>();
        for (int k = 1; k <= m; k++) {
            S.add(new ArrayList<Integer>());
        }
        boolean[][] x_ = new boolean[x.length][x[0].length];
        int z = 0;
        int i = 1;

        int U = upper(n, m, items, capacities, x_, S, i);
        int UB = U;

        // heuristic
        boolean[][] x_new = new boolean[x.length][x[0].length];
        int L = lower(n, m, items, capacities, x_, S, i, x_new);
        if (L > z) {
            z = L;
            for (int k = 1; k <= m; k++) {
                for (int j = 1; j <= n; j++) {
                    x[k][j] = x_[k][j];
                }
            }

            for (int k = i; k <= m; k++) {
                for (int j = 1; j <= n; j++) {
                    if (x_new[k][j]) {
                        x[k][j] = true;
                    }
                }
            }
            if (z == UB) {
                return z;
            }
            if (z == U) {
                // go to 4

            }
        }

        // define a new current solution
        do {
            List<Integer> I = new ArrayList<Integer>();
            while (I.size() > 0) {
                // get the minimum element and push to a stack
                Collections.sort(I);
                int j = I.get(0);
                I.remove(0);
                S.get(i - 1).add(0, j);
                x_[i][j] = true;
                U = upper(n, m, items, capacities, x_, S, i);
                if (U <= z) {
                    // go to 4
                }
            }
            i++;
        } while (i < m);
        i = m - 1;
       
        // backtrack
        do {
            List<Integer> S_i = S.get(i - 1);
            while (S_i.size() > 0) {
                int j = S_i.get(0);
                if (!x_[i][j]) {
                    S_i.remove(0); // pop
                } else {
                    x_[i][j] = false;
                    U = upper(n, m, items, capacities, x_, S, i);
                    if (U > z) {
                        // goto 2
                    }
                }
            }
            i--;
        } while (i > 0);

        throw new RuntimeException("No solution");
    }

   
    private static int solveSKP(List<Integer> item_ids, int capacity,
            Item[] items, boolean[] solution) {
        Catalog newC = new Catalog(1, item_ids.size(), Arrays.asList(capacity));
        for (int i : item_ids) {
            newC.items.put(items[i].id, items[i]);
        }
        IFiller filler = new ExpandingCoreFiller();
        List<Knapsack> solutions = filler.fill(newC);
        Knapsack k0 = solutions.get(0);
        for (Item i : k0.items) {
            solution[i.id] = true;
        }
        return k0.totalValue();
    }

//    private class BranchNode {
//        public int level;
//        public int currentWeight;
//        public int currentValue;
//        public int upperBound;
//        public boolean[] constraints;
//    }
}
