/**
 * ProblemTwo
 */
public class ProblemTwo {
    public final int solSize = 20;
    public final int popSize = 250;
    public final double maxFitness = 500.0;
    private final double crossOverChance = 0.5;
    private final double mutationChance = 0.1;
    private final int tournamentSize = 5;
    private final int numElites = 10;
    
    //driver method
    public static boolean[] main(){
        ProblemTwo problemTwo = new ProblemTwo();
        return problemTwo.run();
    }

    //run the GA
    public boolean[] run (){
        int numOfIterations = 0;
        ProblemTwoPopulation pop = new ProblemTwoPopulation(popSize, true);

        while (maxFitness>pop.getFittest().getFitness()[2]) {
            pop = evolvePopulation(pop);
            //System.out.println("fitness gen: " + numOfIterations);
            //System.out.println("fittest fitness " + pop.getFittest().getFitness() + "\n");
            numOfIterations+=1;
            if (numOfIterations == 50){
                break;
            }
        }
        //System.out.println("sol is: " + pop.getFittest());
        System.out.println("final sol fitness weight/util/fitness: " + pop.getFittest().getFitness()[0] + " : " + pop.getFittest().getFitness()[1] + " : " + pop.getFittest().getFitness()[2]);
        return pop.getFittest().getItems();
    }

    //fit[0]=weight, fit[1]=utility sum
    public static double[] getFitness(boolean[] sol){
        double[] fit = Assess.getTest2(sol);
        return fit;
    }

    public static boolean genRandomBool(){
        return (Math.random()>0.5);
    }

    //evolve population to next generation
    public ProblemTwoPopulation evolvePopulation(ProblemTwoPopulation pop) {
        ProblemTwoPopulation newPop = new ProblemTwoPopulation(popSize, false);
        //eliteism, requires a sorted population
        for (int i = 0; i < numElites; i++) {
            newPop.getSolutions().add(i, pop.getSolution(i));
        }

        //select vals and crossover/mutate
        for (int i=numElites; i<popSize; i++) {
            ProblemTwoSol sol1 = tournamentSelection(pop);
            ProblemTwoSol sol2 = tournamentSelection(pop);
            //ProblemTwoSol sol3 = new ProblemTwoSol();
            ProblemTwoSol newSol = crossover(sol1, sol2);
            newPop.getSolutions().add(i, newSol);
        }
        for (int i = 1; i < popSize; i++) {
            mutate(newPop.getSolution(i));
        }
        newPop.sortByFit();
        return newPop;
    }

    public ProblemTwoSol crossover(ProblemTwoSol sol1, ProblemTwoSol sol2) {
        ProblemTwoSol newSol = new ProblemTwoSol();
        for (int i=0; i<solSize; i++) {
            if (Math.random() <= crossOverChance) {
                newSol.setSingleItem(i, sol1.getSingleItem(i));
            } else {
                newSol.setSingleItem(i, sol2.getSingleItem(i));
            }
        }

        return newSol;
    }

    public void mutate(ProblemTwoSol sol) {
        for (int i=0; i<solSize; i++) {
            if (Math.random() <= mutationChance) {
                //byte num = (byte) Math.round(Math.random());
                boolean item = !sol.getSingleItem(i);
                sol.setSingleItem(i, item);
            }
        }
    }

    public ProblemTwoSol tournamentSelection(ProblemTwoPopulation pop) {
        ProblemTwoPopulation tournament = new ProblemTwoPopulation(tournamentSize, false);
        for (int i=0; i<tournamentSize; i++) {
            int randomId = (int) (Math.random() * solSize);
            tournament.getSolutions().add(i, pop.getSolution(randomId));
        }
        ProblemTwoSol fittest = tournament.getFittest();
        return fittest;
    }
}
