package ea;

/***
 * This is an example of an EA used to solve the problem
 *  A chromosome consists of two arrays - the pacing strategy and the transition strategy
 * This algorithm is only provided as an example of how to use the code and is very simple - it ONLY evolves the transition strategy and simply sticks with the default
 * pacing strategy
 * The default settings in the parameters file make the EA work like a hillclimber:
 * 	the population size is set to 1, and there is no crossover, just mutation
 * The pacing strategy array is never altered in this version- mutation and crossover are only
 * applied to the transition strategy array
 * It uses a simple (and not very helpful) fitness function - if a strategy results in an
 * incomplete race, the fitness is set to 1000, regardless of how much of the race is completed
 * If the race is completed, the fitness is equal to the time taken
 * The idea is to minimise the fitness value
 */


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import teamPursuit.TeamPursuit;
import teamPursuit.WomensTeamPursuit;

public class EA implements Runnable{
	
	// create a new team with the default settings
	public static TeamPursuit teamPursuit = new WomensTeamPursuit(); 
	
	private ArrayList<Individual> population = new ArrayList<Individual>();
	private int iteration = 0;
	
	public EA() {
		
	}

	
	public static void main(String[] args) {
		EA ea = new EA();
		ea.run();
	}

	public void run() {
		initialisePopulation();	
		System.out.println("finished init pop");
		iteration = 0;
		while(iteration < Parameters.maxIterations){
			iteration++;
			Individual parent1 = rouletteSelection();
			Individual parent2 = rouletteSelection();
			Individual child = uniformCrossover(parent1, parent2);
			child = mutate(child);
			child = scrambleMutate(child);
			child.evaluate(teamPursuit);
			replace(child);
			printStats();
		}						
		Individual best = getBest(population);
		best.print();
		
	}

	private void printStats() {		
		System.out.println("" + iteration + "\t B:" + getBest(population) + "\t W:" + getWorst(population));		
	}


	private void replace(Individual child) {
		Individual worst = getWorst(population);
		if(child.getFitness() < worst.getFitness()){
			int idx = population.indexOf(worst);
			population.set(idx, child);
		}
	}

   /*
    * 
    * 
    * MUTATION
    * 
    * 
    */
	
	private Individual mutate(Individual child) {
		if(Parameters.rnd.nextDouble() > Parameters.mutationProbability){
			return child;
		}
		// choose how many elements to alter
		int mutationRate = 1 + Parameters.rnd.nextInt(Parameters.mutationRateMax);
		
		// mutate the transition strategy

			//mutate the transition strategy by flipping boolean value
			for(int i = 0; i < mutationRate; i++){
				int index = Parameters.rnd.nextInt(child.transitionStrategy.length);
				child.transitionStrategy[index] = !child.transitionStrategy[index];
			}
		//mutate pacing stratagey
			//mutate pacing by randomising a value
			for (int i = 0; i< mutationRate; i++) {
				int index = Parameters.rnd.nextInt(child.pacingStrategy.length);
				//assign the pacing strat to a number between 200 and 500
				child.pacingStrategy[index] = ThreadLocalRandom.current().nextInt(200,700);
			}
			
		
		return child;
	}
	
	
	/*
	 * Scramble Mutation
	 */
	
	private Individual scrambleMutate(Individual child) {
		int a,b, i, x;
		List<Integer> subS = new ArrayList<Integer>();
		//pick two points
		a = ThreadLocalRandom.current().nextInt(0, child.pacingStrategy.length);
		b = ThreadLocalRandom.current().nextInt(0,child.pacingStrategy.length);
		//create sub list
		if (a < b) {
			
			for(i = a; i<b; i++) {
				subS.add(child.pacingStrategy[i]);
			}
			Collections.shuffle(subS);
			//reinsert to child
			x=0;
			for (i = a; i < b; i++) {
				child.pacingStrategy[i] = subS.get(x);
				x++;
			}
			
			
		}else {

			for(i = b; i < a; i++) {
				subS.add(child.pacingStrategy[i]);
			}
			//randomise the list
			Collections.shuffle(subS);
			//reinsert to child
			x = 0;
			for (i = b; i < a; i++) {
				child.pacingStrategy[i] = subS.get(x);
				x++;
			}
			
		}
		
		return child;
	}

		
	/*
	 * 
	 * 
	 * CROSSOVER
	 * 
	 * 
	 */

	
	/*
	 * one point crossover I think
	 */
	private Individual crossover(Individual parent1, Individual parent2) {
		if(Parameters.rnd.nextDouble() > Parameters.crossoverProbability){
			return parent1;
		}
		Individual child = new Individual();
		
		int crossoverPoint = Parameters.rnd.nextInt(parent1.transitionStrategy.length);
		
		// before crossover point use parent1
		for(int i = 0; i < crossoverPoint; i++) {
			child.pacingStrategy[i] = parent1.pacingStrategy[i];
		}
		//after crossover point use parent 2
		for (int i = crossoverPoint; i < parent2.pacingStrategy.length; i++) {
			child.pacingStrategy[i] = parent2.pacingStrategy[i];
		}
		
		//same as above
		for(int i = 0; i < crossoverPoint; i++){
			child.transitionStrategy[i] = parent1.transitionStrategy[i];
		}
		for(int i = crossoverPoint; i < parent2.transitionStrategy.length; i++){
			child.transitionStrategy[i] = parent2.transitionStrategy[i];
		}
		return child;
	}
	
	/*
	 * Uniform Crossover
	 */
	private Individual uniformCrossover(Individual parent1, Individual parent2) {
		
		Individual child = new Individual();
		int i;
		double r;
		
		for(i = 0; i < parent1.pacingStrategy.length; i++) {
			r = Math.random();
			if (r < 0.5) {
				child.pacingStrategy[i] = parent1.pacingStrategy[i];
			}else {
				child.pacingStrategy[i] = parent2.pacingStrategy[i];
			}
		}
		for(i = 0; i< parent1.transitionStrategy.length; i++) {
			r = Math.random();
			if (r < 0.5) {
				child.transitionStrategy[i] = parent1.transitionStrategy[i];
				
			}else {
				child.transitionStrategy[i] = parent2.transitionStrategy[i];
			}
		}
		
		return child;
	}
	


	
	
	/*
	 * 
	 * 
	 * SELECTION
	 * 
	 * 
	 */
	
	/**
	 * 
	 * Returns a COPY of the individual selected using tournament selection
	 * 
	 */
	private Individual tournamentSelection() {
		ArrayList<Individual> candidates = new ArrayList<Individual>();
		for(int i = 0; i < Parameters.tournamentSize; i++){
			candidates.add(population.get(Parameters.rnd.nextInt(population.size())));
		}
		return getBest(candidates).copy();
	}
	
	
	
	/*
	 * 
	 * Roulette wheel selection 
	 * 
	 */
	
	private Individual rouletteSelection() {
		
		int i, sumF, p;
		double rand = 0;
		sumF = 0;
		p = 0;
		
		for (i = 0;i < Parameters.popSize; i++) {
			sumF += population.get(i).getFitness();			
		}
		
		rand = Math.random()*sumF;
		i = 0;
		while(p <= rand) {
			
			p += population.get(i).getFitness();
			i++;
		}
		
		
		return population.get(i-1);
		
		
		
		
	}


	private Individual getBest(ArrayList<Individual> aPopulation) {
		double bestFitness = Double.MAX_VALUE;
		Individual best = null;
		for(Individual individual : aPopulation){
			if(individual.getFitness() < bestFitness || best == null){
				best = individual;
				bestFitness = best.getFitness();
			}
		}
		return best;
	}

	private Individual getWorst(ArrayList<Individual> aPopulation) {
		double worstFitness = 0;
		Individual worst = null;
		for(Individual individual : population){
			if(individual.getFitness() > worstFitness || worst == null){
				worst = individual;
				worstFitness = worst.getFitness();
			}
		}
		return worst;
	}
	
	private void printPopulation() {
		for(Individual individual : population){
			System.out.println(individual);
		}
	}

	private void initialisePopulation() {
		while(population.size() < Parameters.popSize){
			Individual individual = new Individual();
			individual.initialise();			
			individual.evaluate(teamPursuit);
			population.add(individual);
							
		}		
	}	
}
