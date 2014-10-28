package hps.nyu.fa14.solve;

import hps.nyu.fa14.Catalog;
import hps.nyu.fa14.IFiller;
import hps.nyu.fa14.ISolutionMonitor;
import hps.nyu.fa14.Knapsack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFiller implements IFiller {

	@Override
	public abstract List<Knapsack> fill(Catalog c);

	protected List<ISolutionMonitor> monitors = new ArrayList<ISolutionMonitor>();
	
	@Override
	public void addSolutionMonitor(ISolutionMonitor monitor) {
		monitors.add(monitor);
	}

	protected void notifyNewSolutiont(List<Knapsack> solution){
		for(ISolutionMonitor m : monitors){
			m.updateSolution(solution);
		}
	}
	
}
