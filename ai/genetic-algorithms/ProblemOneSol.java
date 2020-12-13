//import java.lang.Math;

/**
 * ProblemOneSol
 *
 * individual possible solutions
 */

public class ProblemOneSol {
    private double[] nums = new double[20];
    private double fitness = Double.MAX_VALUE;

    public ProblemOneSol() {
        for (int i=0; i<nums.length; i++) {
            //nums[i] = Math.random()*Math.round(5.12*(Math.random() - Math.random()));
            nums[i] = ProblemOne.genRandomNum();
            //nums[i] = ((double) Math.round((ProblemOne.genRandomNum()*100000))/100000);


        }
    }

    //getter all nums, for returning final solution contents
    public double[] getNums() {
        return nums;
    }

    //getter for a single num for mutation
    protected double getSingleNum(int index) {
        return nums[index];
    }

    //setter for a single num for mutation
    protected void setSingleNum(int index, double value) {
        nums[index] = value;
        fitness = Double.MAX_VALUE;
    }

    public double getFitness(){
        if (fitness == Double.MAX_VALUE) {
            fitness = ProblemOne.getFitness(nums);
        }
        return fitness;
    }

    //remove
    public String toString() {
        String numString = "";
        for (int i = 0; i < nums.length; i++) {
            numString += getSingleNum(i);
        }
        return numString;
    }

}
