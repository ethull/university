import java.util.ArrayList;

public class EmptySet implements IntSet {
    private static EmptySet EmptySetObj;
    private EmptySet(){
    }
    
    public static EmptySet emptySet(){
        if (EmptySetObj == null){
            EmptySetObj = new EmptySet();
        }
        return (EmptySetObj);
    }
    
    public IntSet add(int x){
        return SingletonSet.singleton(x);
    }

    public boolean contains(int x){
        return false;
    }

    public IntSet union(IntSet other){
        return other;
    }

    public ArrayList<Integer> localMakeString(){
        return new ArrayList<>();
    }
}
