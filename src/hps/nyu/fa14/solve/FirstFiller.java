package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.IFiller;
import hps.nyu.fa14.ISolutionMonitor;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FirstFiller extends AbstractFiller implements ISolutionMonitor {

	private final List<IFiller> fillers = new ArrayList<IFiller>();

	public void addFiller(IFiller filler) {
	    filler.addSolutionMonitor(this);
	    fillers.add(filler);
	}

	private Catalog currentCatalog;

	private final CountDownLatch firstLatch = new CountDownLatch(1);
	
	@Override
	public List<Knapsack> fill(Catalog c) {
	    currentCatalog = c;

		List<Thread> runningThreads = new ArrayList<Thread>();
		for (IFiller f : fillers) {
			SolutionRunner newRunner = new SolutionRunner(f);

			Thread t = new Thread(newRunner);
			runningThreads.add(t);
			t.start();
		}

		// Wait for first thread to complete
        try {
            firstLatch.await();
            
            for (Thread t : runningThreads) {
                try {
                    t.interrupt(); // kill remaining threads
                    t.join(2000); //Give it 2 seconds to actually finish
                } catch (Exception ex) {
                    //System.out.println("Thread exception");
                    //ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            //System.out.println("Latch exception");
            //ex.printStackTrace();
		}
		synchronized (currentCatalog) {
			return bestSolution;
		}
	}

	@Override
	public void updateSolution(List<Knapsack> s) {
		updateIfBest(s);
	}

	private List<Knapsack> bestSolution;
	private int bestValue;

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

	private class SolutionRunner implements Runnable {

		private IFiller filler;

		public SolutionRunner(IFiller s) {
		    filler = s;
		}

		@Override
		public void run() {
			List<Knapsack> s = filler.fill(currentCatalog);
			updateIfBest(s); // see if this is actually the best
			firstLatch.countDown(); // signal done
		}
	}
}
