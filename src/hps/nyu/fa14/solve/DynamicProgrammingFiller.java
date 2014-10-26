package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.IFiller;
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
public class DynamicProgrammingFiller implements IFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {

        // use DP to get max value
        // build memory efficient datastructure
        int[] weight = new int[c.objectCount];
        int[] value = new int[c.objectCount];
        for (int i = 0; i < c.objectCount; i++) {
            weight[i] = c.items.get(i + 1).weight;
            value[i] = c.items.get(i + 1).value;
        }

        List<Integer> picked = getMaxValue(c.getEmptyKnapsack(1).capacity,
                weight, value, c.objectCount);

        // figure out how to derive solution from that
        Knapsack k = c.getEmptyKnapsack(1);
        for(Integer id : picked){
            k.items.add(c.items.get(id + 1));
        }
        
        // Non-solution for now
        return Arrays.asList(k);
    }

    /**
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
    private List<Integer> getMaxValue(int W, int weight[], int value[], int n) {

        // returns a list of included indexes

        // Stores optimal values for partial solutions to the knapsack
        // subproblems
        int[][] K = new int[n + 1][W + 1];

        // In order to reconstruct the picked items we need to keep track of
        // whether it was chosen or not at each position
        // 0 -> not considered
        // 1 -> selected
        // -1 -> not selected
        int[][] picked = new int[n + 1][W + 1];

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
                    int includeValue = value[i - 1] + K[i - 1][w - weight[i - 1]];
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
        System.out.println("Max Value: " + K[n][W]);

        // work backwards to reconstruct which elements are in the solution
        List<Integer> indexes = new ArrayList<Integer>();
        int itemP = n;
        int size = W;
        while (itemP > 0) {
            if (picked[itemP][size] == 1) {
                indexes.add(itemP-1);
                itemP--; //decrement the item to make a decision about it
                size -= weight[itemP];
            } else {
                itemP--;
            }
        }

        return indexes;
    }
}
