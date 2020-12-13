//import java.lang.Math;
//import java.util.Random;
import java.util.ArrayList;

public class ProblemTwoPopulation {
    private ArrayList<ProblemTwoSol> solutions;

    public ProblemTwoPopulation(int size, boolean genNew){
        solutions = new ArrayList<>();
        if (genNew){
            generateNew(size);
        }
    }

    public void generateNew(int size){
        for (int i=0; i<size; i++){
            ProblemTwoSol newSolution = new ProblemTwoSol();
            solutions.add(i, newSolution);
        }
        sortByFit();
    }

    public ProblemTwoSol getSolution(int index){
        return solutions.get(index);
    }

    public ArrayList<ProblemTwoSol> getSolutions(){
        return solutions;
    }

    public ProblemTwoSol getFittest(){
        ProblemTwoSol best = solutions.get(0);
        for (int i=0; i<solutions.size(); i++){
            if (best.getFitness()[2] <= getSolution(i).getFitness()[2]){
                best = getSolution(i);
            }
        }
        return best;
    }

    public void sortByFit(){
        int n = solutions.size();
        for (int j = 1; j < n; j++) {  
            ProblemTwoSol currentSol = solutions.get(j);  
            int i = j-1;  
            while ((i>-1) && (solutions.get(i).getFitness()[2] < currentSol.getFitness()[2])){
                solutions.set(i+1, solutions.get(i));
                i--;  
            }  
            solutions.set(i+1, currentSol);
        }   

        /*
        for (int i = 0; i < solutions.size(); i++) {
            System.out.println("iter: "+i);
            ProblemTwoSol pts =  solutions.get(i); 
            System.out.println(pts.getFitness()[1]);

        }
        */
    }
}
