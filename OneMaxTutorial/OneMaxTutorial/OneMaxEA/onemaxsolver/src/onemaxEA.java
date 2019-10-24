

/* a simple package to implement max 1s */

import java.io.*;
import java.text.*;
import java.util.Random;

public class onemaxEA{
	// parameters
    int maxIterations;
    int tnSize;
    double mutationRate;
    int popSize;
    
    int [][] population;
    int newSoln[];
    int length;
   
   
    Random randomNum;
    

    public onemaxEA( int i, int l, int p, int t, double  m){
	// initialise parameters

	maxIterations=i;
	length = l;
	popSize =p;
	mutationRate = m;
	tnSize =t;
	
	randomNum = new Random();
	System.out.println("Starting EA");
	
    }

    
  public void runEA(){

	int  fitness;
	int p1, p2;
	int [] child, mutatedChild;

	population = new int[popSize][length];
	
	// initialise  the population with random solutions
	initialise();

	// run the evolutionary algorithm 
	for (int i=0;i<maxIterations;i++){
		
		// select two parents	
		p1 = select();
		p2 = select();
		
		// crossover	
		child = one_pt_crossover(p1,p2);
		
    	// mutate
		mutate(child);
	   
	    // evaluate the new solution
	    fitness=getFitness(child);
	    
	   
	    // replacement
	    replace(child,fitness);
	    
	    System.out.print("Iteration " + i + " ");
	    printBest();
	}
	System.out.println("finished");
   } 
	     

    /*
     * This method initialises each string in the population with
     * a random bit string (0,1s)
     */
    public void initialise(){
        	
    	for (int i=0;i<popSize;i++)
    		for (int j=0;j<length;j++){
    		    			population[i][j] = randomNum.nextInt(2);
    		}
    }

    
  /*  tournament selection 
   *  returns an integer with the id of the parent selected
   *  the size of the tournament is set with the parameter tnSize
   */
    public int select(){
       	
    	int[] possibleParentList = new int[tnSize];
    	// pick some random parents; the array holds the ID of the chosen parent
    	for (int i = 0;i<tnSize;i++)
    		possibleParentList[i] = randomNum.nextInt(popSize);

    	// now choose the best of the chosen parents 
    	int bestParentID = possibleParentList[0];
    	int bestParentFitness = getFitness(population[bestParentID]);
    	
    	for (int i=1;i<tnSize;i++){
    		int id = possibleParentList[i];
    		int fitness = getFitness(population[id]);
    		if (fitness > bestParentFitness){
    			bestParentID = possibleParentList[i];
    			bestParentFitness = fitness;
    		}
    	}
    				
    	return bestParentID;
    }
    
    /*
     * One point crossover
     * Pick a random point along the length of the chromosome
     * Returns 1 new child
     */
    public int[] one_pt_crossover(int parent1ID, int parent2ID){
    	
    	int i;
    	
    	int[] child = new int[length];
    	
    	int crosspoint = randomNum.nextInt(length);
    	
    	for (i=0;i<crosspoint;i++)
    		child[i] = population[parent1ID][i];
    	
    	for (i=crosspoint;i<length;i++)
    		child[i] = population[parent2ID][i];
    	
    	return child;
    }
    
    /* Mutation: 
     * with probability defined by mutationRate, 
     * mutate each bit (flip it)
     * 
     */
 
    public void mutate(int [] child){
	
	
    	for (int i=0;i<length;i++)
    		if (randomNum.nextDouble() < mutationRate)
    			if (child[i] == 1)
    				child[i] = 0;
    			else
    				child[i]=1;

    }

   
 
/* Fitness calculation
 * Counts the numbers of 1s in the string and returns
 * the answer
 * 
 */
    public int getFitness(int []someSolution){
    	int fitness=0;

    	
    	for (int i =0;i<length;i++)
    		if (someSolution[i] == 1)
    			fitness++;
    	
    	return(fitness);
    }	
    
    /*
     * Replacement
     * Find the worst member of the population
     * If the child is better than the worst, then
     * copy the child into the population array 
     * in place of the worst member
     */

    public void replace(int[] child, int childFitness){
    	// find WORST in population
    	
    	int worstID = 0;
    	int worstFitness = length;
    	
    	for (int i=0;i<popSize;i++){
    		int fit = getFitness(population[i]);
    		if (fit < worstFitness){
    			worstID = i;
    			worstFitness = fit;
    		}
    	}
    	
    	// now copy child into population if it is better than the current worst
    	if (childFitness > worstFitness)
    		for (int i =0;i< length; i++)
    			population[worstID][i] = child[i];
    	
    }

    public void printBest(){
    
    	int bestID = 0;
    	int bestFitness = 0;
    	
    	for (int i=0;i<popSize;i++){
    		int fit = getFitness(population[i]);
    		if (fit >bestFitness){
    			bestID = i;
    			bestFitness = fit;
    		}
    	}
    	
    	System.out.println("best is " + bestFitness);
    }
    
}



