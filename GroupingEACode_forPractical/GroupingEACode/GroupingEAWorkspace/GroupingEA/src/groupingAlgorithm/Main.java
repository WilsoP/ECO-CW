package groupingAlgorithm;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		
		SolveProblem mySolver = new SolveProblem();
		
		for (int run = 0; run < Parameters.numberOfRuns; run++) {
			System.out.println("Started run: " + run);
			mySolver.run();
			mySolver.outputResult();
			System.out.println("Finished run: " + run);
		}
	}

}
