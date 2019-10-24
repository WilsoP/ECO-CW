


public class onemaxsolver{	
		
 
	
	public static void main(String[] args) {	
		
		int iterations=1000;
		int lengthString = 50;   // number of bits in string
		int tnSize = 5;  // tournament size for selection
		int popSize= 100; // population size
		double mutationRate= 0.05;	 // mutation rate

	
		onemaxEA myEA = new onemaxEA( iterations, lengthString, popSize, tnSize, mutationRate);
		
		myEA.runEA();
		
	}

}