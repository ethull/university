import java.util.ArrayList;

public interface IntSet {
    //adds x to set
    IntSet add(int x);
    //return true iff x in set
    boolean contains(int x);
    IntSet union(IntSet other);
    //String toString();
    ArrayList<Integer> localMakeString();
    default String makeString(){
        ArrayList<Integer> values = localMakeString();
        String outString = "{";
        outString += values.get(0);
        for (int i=1; i<values.size(); i++){
            outString += (", " + values.get(i));
        }
        outString += "}";
        return outString;
    }
}
