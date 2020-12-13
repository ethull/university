/**
 * ProblemOne
 */

//add some randoms at start
public class ProblemOne {
    public final int solSize = 20;
    //highest: pop=60
    public final int popSize = 600;
    public final double maxFitness = 0.0;
    //%50 chance, half numbers crossed over
    private final double crossOverChance = 0.5;
    //1% (0.025) chance of 1% change
    private final double mutationChance = 0.1;
    //tn size, highest: tn= 2 or 3
    private final int trnmtSize = 2;
    //keep variety of good fitness values
    private final int numElites = 35;

    //fitness test with Assess.getTest1(double[])
    //EG sol: array of doubles of size 20 
    //Allowed vals -5 to +5
    //Lower fitness is better. optimal fitness=0
    
    //driver method
    public static double[] main(){
        ProblemOne problemOne = new ProblemOne();
        return problemOne.run();
    }
   
    //run the GA 
    public double[] run(){
        int numOfIterations = 0;
        ProblemOnePopulation pop = new ProblemOnePopulation(popSize, true);

        //System.out.println(pop.getSolutions());
        //System.out.println("fittest: "+pop.getFittest().getFitness());
        while (maxFitness<pop.getFittest().getFitness()) {
            pop = evolvePopulation(pop);
            //System.out.println("fitness gen: " + numOfIterations);
            //System.out.println("fittest fitness " + pop.getFittest().getFitness() + "\n");
            numOfIterations+=1;
            if (numOfIterations == 50){
                break;
            }
        }
        //System.out.println("sol is: " + pop.getFittest());
        System.out.println("final sol fitness: " + pop.getFittest().getFitness());
        return pop.getFittest().getNums();
    }

    //get the fitness for problem one, needs to be static so I can call it from my population
    public static double getFitness(double[] sol){
        double fit = Assess.getTest1(sol);
        //System.out.println("get fitness: " + fit + "\n");
        return fit;
    }

    //get a random num to use in a sol
    public static double genRandomNum(){
        return Math.random()*Math.round(5.12*(Math.random() - Math.random())); 
        //return -5 + (5 - -5) * Math.random();
    }

    //evolve population to next generation
    public ProblemOnePopulation evolvePopulation(ProblemOnePopulation pop) {
        ProblemOnePopulation newPop = new ProblemOnePopulation(popSize, false);
        //eliteism, requires a sorted population
        for (int i = 0; i < numElites; i++) {
            newPop.getSolutions().add(i, pop.getSolution(i));
        }

        //select vals and crossover/mutate
        for (int i=numElites; i<popSize; i++) {
            ProblemOneSol sol1 = trnmtSelection(pop);
            ProblemOneSol sol2 = trnmtSelection(pop);
            if (sol1.getFitness() == sol2.getFitness()) sol2=new ProblemOneSol();
            //ProblemOneSol sol3 = new ProblemOneSol();
            ProblemOneSol newSol = crossover(sol1, sol2);
            newPop.getSolutions().add(i, newSol);
        }
        for (int i = 1; i < popSize; i++) {
            mutate(newPop.getSolution(i));
        }
        //next pop will need to be sorted to select elites
        newPop.sortByFit();
        //for (ProblemOneSol sol : newPop.getSolutions()) {
        //    System.out.println(sol.getFitness());
        //}
        return newPop;
    }

    //as fitness get lower make difference smaller
    public void mutate(ProblemOneSol sol) {
        for (int i=0; i<solSize; i++) {
            if (Math.random() <= mutationChance) {
                //if (sol.getSingleNum(i) == 0) System.out.println("zero -----");
                //byte num = (byte) Math.round(Math.random());
                //System.out.println("fitness: "+sol.getFitness());
                //System.out.println("fitness item: "+sol.getSingleNum(i));
                if (sol.getFitness() < 5){
                    if (Math.random() <= 0.5){
                        double num = sol.getSingleNum(i) + sol.getFitness()*0.0001;
                        sol.setSingleNum(i, num);
                    }else{
                        double num = sol.getSingleNum(i) - sol.getFitness()*0.0001;
                        sol.setSingleNum(i, num);
                    }
                }else{
                    if (Math.random() <= 0.5){
                        double num = sol.getSingleNum(i) + sol.getFitness()*0.001;
                        sol.setSingleNum(i, num);
                    }else{
                        double num = sol.getSingleNum(i) - sol.getFitness()*0.001;
                        sol.setSingleNum(i, num);
                    }
                }
            }
        }
    }

    public ProblemOneSol crossover(ProblemOneSol sol1, ProblemOneSol sol2) {
        ProblemOneSol newSol = new ProblemOneSol();
        for (int i=0; i<solSize; i++) {
            if (Math.random() <= crossOverChance) {
                newSol.setSingleNum(i, sol1.getSingleNum(i));
            } else {
                newSol.setSingleNum(i, sol2.getSingleNum(i));
            }
        }
        return newSol;
    }

    public ProblemOneSol trnmtSelection(ProblemOnePopulation pop) {
        ProblemOnePopulation trnmt = new ProblemOnePopulation(trnmtSize, false);
        for (int i=0; i<trnmtSize; i++) {
            trnmt.getSolutions().add(i, pop.getSolution((int) (Math.random() * solSize)));
        }
        ProblemOneSol fittest = trnmt.getFittest();
        return fittest;
    }

    //fitness proportional selection
    public ProblemOneSol rouletteSelection(ProblemOnePopulation pop) {
        double fitnessSum = 0.0;
        double[] fitnessPercentages = new double[popSize];
        for (int i = 0; i < solSize; i++) {
            fitnessSum += pop.getSolution(i).getFitness();
        }
        double denom = popSize*fitnessSum;
        for (int i = 0; i < solSize; i++) {
            double nom = (fitnessSum-pop.getSolution(i).getFitness()) + (fitnessSum / popSize);
            fitnessPercentages[i] = nom/denom;
        }

        return new ProblemOneSol();

        //ProblemOnePopulation tournament = new ProblemOnePopulation(tournamentSize, false);

        //ProblemOneSol fittest = tournament.getFittest();
        //return fittest;
         
        //double denom = popSize*fitnessSum;
        //double nom = (fitnessSum-fitness[i]) + (fitnessSum / popSize);
        //double percent = nom / denom;

        //denom = popSize*sumFitness
        //nom = (sumFitness-fitness[i]) + (sumFitness / popSize)
        //nom / denom
    }
}
