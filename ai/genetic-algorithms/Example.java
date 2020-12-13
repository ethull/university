import java.lang.Math;
import java.util.ArrayList; 

/*
 larger pop: less convergence, less performance (more time)
 more crossover: more convergence
 more mutation: more exploration
*/


class Example {
		private static final String name = "ethull";
		private static final String login = "ethull";

		public static double[] problemOne(){
			//double sum = 0;
			//double[] sol1 = 
			//for (int i = 0; i < 5; i++) {
			//	double res = ProblemOne.main();
			//	System.out.println("result: " + res);
			//	sum+=res;
			//}
			//System.out.println(sum/5);
			return ProblemOne.main(); 
		}
		
		public static boolean[] problemTwo(){
			/*
			//EG sol: boolean array of size 100
			//create a random sample solution and get the weight and utility
			//The higher the fitness, the better, but be careful of  the weight constraint!

			//get sample sol
			boolean[] sol2 = new boolean[100];
			for(int i=0;i< sol2.length; i++){
				sol2[i]= (Math.random()>0.5);
			}

			//Now checking the fitness of the candidate solution
			double[] tmp =(Assess.getTest2(sol2));

			//The index 0 of tmp gives the weight. Index 1 gives the utility
			System.out.println("The weight is: " + tmp[0]);
			System.out.println("The utility is: " + tmp[1]);
			return sol2;
			*/
			return ProblemTwo.main();
		}

		public static void writeResult(){}

		public static void main(String[] args){
			//Do not delete/alter the next line
		    long startT=System.currentTimeMillis();
			double[] sol1 = ProblemOne.main();
			boolean[] sol2 = ProblemTwo.main();
			System.out.println("final result for problem1: " + sol1);
			System.out.println("final result for problem2: " + sol2);

			//submit the results you generated, with your name and login: 
		    long endT = System.currentTimeMillis();
			System.out.println("Total execution time was: " +  ((endT - startT)/1000.0) + " seconds");
	  }
}
