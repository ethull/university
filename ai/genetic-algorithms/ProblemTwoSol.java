//import java.lang.Math;
import java.util.Arrays;

/**
 * ProblemTwoSol
 *
 * individual possible solutions
 */

public class ProblemTwoSol {
    private boolean[] items = new boolean[100];
    //weight,utility,fitness
    private double[] fitness = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};

    public ProblemTwoSol() {
        //initialise with no items
        Arrays.fill(items, false);
        //add item but make sure weight stays bellow 500
        for (int i=0; i<items.length; i++) {
            items[i] = ProblemTwo.genRandomBool();
            if (Assess.getTest2(items)[0] > 500){
                items[i] = !items[i];
            }
        }
        //System.out.println("weight: "+Assess.getTest2(items)[0]);
        //System.out.println("util: :"+Assess.getTest2(items)[1]);
    }

    //return internal items for final solution
    protected boolean[] getItems() {
        return items;
    }

    //getter for a single num for mutation
    protected boolean getSingleItem(int index) {
        return items[index];
    }

    //setter for a single num for mutation
    protected void setSingleItem(int index, boolean value) {
        items[index] = value;
        fitness[0] = Double.MAX_VALUE;
        fitness[1] = Double.MAX_VALUE;
        fitness[2] = Double.MAX_VALUE;
    }

    public double[] getFitness(){
        if (fitness[2] == Double.MAX_VALUE) {
            double[] res = ProblemTwo.getFitness(items);
            fitness[0] = res[0];
            fitness[1] = res[1];
            if(fitness[0] > 500){
                fitness[2] = res[1]/res[0]; //res[1]/res[0];
            }else{
                fitness[2] = res[1]; //res[1]/res[0];
            }
        }
        return fitness;
    }

    //remove
    public String toString() {
        String itemstring = "";
        for (int i = 0; i < items.length; i++) {
            itemstring += getSingleItem(i);
        }
        return itemstring;
    }

}
