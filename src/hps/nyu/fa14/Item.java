package hps.nyu.fa14;

import java.util.Comparator;

/**
 * An item that can be put into a knapsack. Will always have integral weight and
 * value
 * Item ids are 1-indexed
 */
public class Item {

    public final int id;
    public final int value;
    public final int weight;

    public Item(int id, int value, int weight){
        this.id = id;
        this.value = value;
        this.weight = weight;
    }
    
    public static Comparator<Item> RANK_BY_RATIO = new Comparator<Item>() {
        @Override
        public int compare(Item i1, Item i2) {
            return (int) Math.signum(((double)i1.value / i1.weight)
                    - ((double)i2.value / i2.weight));
        }
    };
    
    public static Comparator<Item> RANK_BY_WEIGHT = new Comparator<Item>() {
        @Override
        public int compare(Item i1, Item i2) {
            return (int) Math.signum(i2.weight - i1.weight);
        }
    };
    
    @Override
    public String toString(){
        return String.format("[%d] V: %d W: %d V/W:%2f", id, value, weight, (double)value/weight);
    }
}
