package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.IFiller;
import hps.nyu.fa14.ISolutionMonitor;
import hps.nyu.fa14.Knapsack;

import java.util.List;

public class TimedFiller extends AbstractFiller implements ISolutionMonitor,
		Runnable {

	private final int maxSeconds;
	private final IFiller filler;
	private Catalog currentCatalog;
	// Make sure to give yourself this much overhead for setting up the thread
	private final int SETUP_MILLIS = 150;
	
	public TimedFiller(IFiller solver, int seconds) {
		this.filler = solver;
		maxSeconds = seconds;
	}

	@SuppressWarnings("deprecation") // Yes, I know stop is deprecated, but that is silly
	@Override
	public List<Knapsack> fill(Catalog c) {

		// Set up the filler to report the best solution reported so far
	    currentCatalog = c;

		// run a thread.
		Thread solveThread = new Thread(this);
		solveThread.start();
		try {
			// Wait until the thread finishes or we time out
			solveThread.join((maxSeconds * 1000) - SETUP_MILLIS);
		} catch (Exception ex) {/* suppress */
		System.out.println();
		}

		if(solveThread.isAlive()){
			// Interrupt does not do what we need (or expect) it to do, so fail
			// solveThread.interrupt();
			solveThread.stop();
		}

		// Wait a certain amount of time, then kill and output
		return bestSolution;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		filler.addSolutionMonitor(this);
		try{
		List<Knapsack> s = filler.fill(currentCatalog);
		// If we get this far, assume the solver returned the best assignment
		bestSolution = s;
		} catch(Exception ex){
		    System.out.println("Can this catch OOM?");
		}
	}

	private List<Knapsack> bestSolution;
    private int bestValue;

	@Override
	public void updateSolution(List<Knapsack> a) {
	    updateIfBest(a);
	}
	
	// TODO: refactor this into base class
	private void updateIfBest(List<Knapsack> s) {
        synchronized (currentCatalog) {
            int newValue = 0;
            for(Knapsack k : s){
                newValue += k.totalValue();
            }
            if (newValue > bestValue) {
                bestSolution = s;
                bestValue = newValue;
                notifyNewSolution(bestSolution);
            }
        }
    }
}
