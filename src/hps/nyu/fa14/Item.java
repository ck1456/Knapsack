package hps.nyu.fa14;

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
}
