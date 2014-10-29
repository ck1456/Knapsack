package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.Item;
import hps.nyu.fa14.Knapsack;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Implements best first branch and bound solution
 * 
 * Resources: http://www.youtube.com/watch?v=R6BQ3gBrfjQ
 */
public class BranchAndBoundFiller extends AbstractFiller {

    @Override
    public List<Knapsack> fill(Catalog c) {

        // get a list of all the items ranked by ratio
        rankedItems = c.getItemsSortedByRatio();
        totalLevels = c.objectCount;

        // Only fill the first knapsack
        k = c.getEmptyKnapsack(1);

        // Construct the root node
        BranchNode root = new BranchNode();
        root.level = 0;
        root.currentWeight = 0;
        root.currentValue = 0;
        root.constraints = new boolean[0];
        root.upperBound = updateUpperBound(0, k.capacity, 0);

        nodesToExplore.add(root);

        while (nodesToExplore.size() > 0) {
            if (Thread.currentThread().isInterrupted()) {
                //throw new Exception("Threadbail");
                break;
            }
            // get the best node (by upperBound) and explore
            BranchNode best = popBest();
            if (best.level < totalLevels) {
                exploreNode(best);
            }
        }

        // construct a solution, based on the constraints
        for (int i = 0; i < bestConstraints.length; i++) {
            if (bestConstraints[i]) {
                k.items.add(rankedItems.get(i));
            }
        }
        return Arrays.asList(k);
    }

    private Knapsack k;
    private int totalLevels = 0;

    private boolean[] bestConstraints = null;
    private int bestValue = 0;

    private List<Item> rankedItems;

    private final Queue<BranchNode> nodesToExplore = new PriorityQueue<BranchNode>(
            20000, RANK_BY_UPPER_BOUND_DESC);

    private BranchNode popBest() {
        return nodesToExplore.remove();
    }

    private int explored = 0;

    // Generate up to two children and add if worth exploring (possibly best)
    // Never explores child that:
    // - exceeds capacity
    // - has upper bound < best known so far (lower bound)
    // -
    private void exploreNode(BranchNode p) {
        if (++explored % 10000000 == 0) {
            System.out.println("Explored " + explored);
        }

        // prune solutions that are not worth exploring
        Item item = rankedItems.get(p.level);
        { // left include
            BranchNode c = new BranchNode();
            c.level = p.level + 1;
            c.currentWeight = p.currentWeight + item.weight;
            if (c.currentWeight <= k.capacity) { // only go any further if
                                                 // feasible
                c.currentValue = p.currentValue + item.value;
                c.constraints = Arrays.copyOf(p.constraints, c.level);
                c.constraints[c.level - 1] = true; // include this element
                c.upperBound = updateUpperBound(c.currentValue, k.capacity
                        - c.currentWeight, c.level);
                if (c.upperBound > bestValue) { // Only add if the upperbound is
                                                // feasibly better
                    updateBestConstraints(c);
                    nodesToExplore.add(c);
                }
            }
        }
        { // right exclude
            BranchNode c = new BranchNode();
            c.level = p.level + 1;
            c.currentWeight = p.currentWeight;
            c.currentValue = p.currentValue;
            // don't include by default
            c.constraints = Arrays.copyOf(p.constraints, c.level);

            c.upperBound = updateUpperBound(c.currentValue, k.capacity
                    - c.currentWeight, c.level);
            if (c.upperBound > bestValue) { // Only add if the upperbound is
                                            // feasibly better
                nodesToExplore.add(c);
            }
        }
    }

    // store any indicated solution if it is better than the best so far
    private void updateBestConstraints(BranchNode c) {
        if (c.currentValue > bestValue) {
            bestValue = c.currentValue;
            bestConstraints = c.constraints;
            /*
            System.out.println(String.format(
                    "Best Value: %d Constraints: %d Queue: %d UpperBound: %d",
                    bestValue, c.constraints.length, nodesToExplore.size(),
                    c.upperBound));
                    */
        }
    }

    // calculate upper bound based on Greedy solution
    private int updateUpperBound(int currentValue, int remainingCapacity,
            int index) {
        int upperBound = currentValue;

        int i = index;
        while (remainingCapacity > 0 && i < rankedItems.size()) {
            Item item_i = rankedItems.get(i);
            // add the portion of item_i that will fit (and round up)
            int amount = (int) Math
                    .ceil(item_i.value
                            * Math.min(1.0, (double) remainingCapacity
                                    / item_i.weight));
            remainingCapacity -= item_i.weight;
            upperBound += amount;
            i++;
        }
        return upperBound;
    }

    private static Comparator<BranchNode> RANK_BY_UPPER_BOUND_DESC = new Comparator<BranchNode>() {
        @Override
        public int compare(BranchNode b1, BranchNode b2) {
            int order = (int) Math.signum(b2.upperBound - b1.upperBound);
            if (order != 0) {
                return order;
            }
            order = (int) Math.signum(b2.level - b1.level);
            if (order != 0) {
                return order;
            }
            order = (int) Math.signum(b2.currentValue - b1.currentValue);
            if (order != 0) {
                return order;
            }
            return (int) Math.signum(b2.currentWeight - b1.currentWeight);
        }
    };

    private class BranchNode {
        public int level;
        public int currentWeight;
        public int currentValue;
        public int upperBound;
        public boolean[] constraints;
    }
}
