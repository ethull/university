//import java.lang.Math;
//import java.util.Random;
import java.util.ArrayList;

public class ProblemOnePopulation {
    private ArrayList<ProblemOneSol> solutions;

    public ProblemOnePopulation(int size, boolean genNew){
        solutions = new ArrayList<>();
        if (genNew){
            generateNew(size);
        }
    }

    public void generateNew(int size){
        for (int i=0; i<size; i++){
            ProblemOneSol newSolution = new ProblemOneSol();
            solutions.add(i, newSolution);
        }
        //sort solutions initialially to help select elites
        sortByFit();
    }

    public ProblemOneSol getSolution(int index){
        return solutions.get(index);
    }

    public ArrayList<ProblemOneSol> getSolutions(){
        return solutions;
    }

    public ProblemOneSol getFittest(){
        ProblemOneSol best = solutions.get(0);
        for (int i=0; i<solutions.size(); i++){
            if (best.getFitness() >= getSolution(i).getFitness()){
                best = getSolution(i);
            }
        }
        return best;
    }

    public void sortByFit(){
        //ProblemOneSol best = solutions.get(0);
        int n = solutions.size();
        for (int j = 1; j < n; j++) {  
            ProblemOneSol currentSol = solutions.get(j);  
            int i = j-1;  
            while ((i>-1) && (solutions.get(i).getFitness() > currentSol.getFitness())){
                solutions.set(i+1, solutions.get(i));
                i--;  
            }  
            solutions.set(i+1, currentSol);
        }   
    }
}
