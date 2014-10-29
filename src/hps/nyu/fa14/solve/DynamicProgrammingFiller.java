package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Finds a solution to the knapsack problem by using Dynamic Programming We will
 * see if it is fast enough
 * 
 * Currently only works for small instances of problem 1
 * 
 * Resources:
 * http://www.geeksforgeeks.org/dynamic-programming-set-10-0-1-knapsack-problem/
 * http
 * ://www.programminglogic.com/knapsack-problem-dynamic-programming-algorithm/
 */
public class DynamicProgrammingFiller extends AbstractFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {

        // use DP to get max value
        // build memory efficient datastructure
        int[] weight = new int[c.objectCount];
        int[] value = new int[c.objectCount];
        int[] indexLookup = new int[c.objectCount];
        int index = 0;
        for (int i : c.items.keySet()) {
            weight[index] = c.items.get(i).weight;
            value[index] = c.items.get(i).value;
            indexLookup[index] = i;
            index++;
        }

        Knapsack k = c.getEmptyKnapsack(1);
        try {
            List<Integer> picked = getMaxValue(c.getEmptyKnapsack(1).capacity,
                    weight, value, c.objectCount);

            // figure out how to derive solution from that
            for (Integer id : picked) {
                k.items.add(c.items.get(indexLookup[id]));
            }
        } catch (Exception ex) {
            // Can happen if thread bails
            // TODO: Is this necessary
            // System.out.println("Thread bail");
        }

        return Arrays.asList(k);
    }

    /**
     * This implementation is time efficient, but not memory efficient and is
     * still exponential
     * 
     * @param W
     *            total weight capacity of the knapsack
     * @param weight
     *            array of weights of objects
     * @param val
     *            array of values of objects
     * @param n
     *            total number of objects
     */
    private List<Integer> getMaxValue(int W, int weight[], int value[], int n)
            throws Exception {

        // returns a list of included indexes
        // Do a sanity to check to make sure this will work for with a
        // reasonable amount of memory
        double memCost = (5.0 * n * W);
        if (memCost > 1e9) {
            Thread.sleep(10000); // Wait 10 seconds
            throw new Exception("Not enough memory");
        }

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
                if (Thread.currentThread().isInterrupted()) {
                    throw new Exception("Threadbail");
                }
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
                    int includeValue = value[i - 1]
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
