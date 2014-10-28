package hps.nyu.fa14;

import java.util.List;

public interface ISolutionMonitor {

	/**
	 * Notify a monitor that a new (better) assignment has been produced
	 * @param a
	 */
	void updateSolution(List<Knapsack> solution);
}
