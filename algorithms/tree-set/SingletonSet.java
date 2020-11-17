import java.util.ArrayList;

public class SingletonSet implements IntSet {
    private int value;
    private static SingletonSet[] singletonSets = new SingletonSet[7];
    private SingletonSet(int value){
        this.value = value;
    }
    
    public static SingletonSet singleton(int n){
        if (0 >= n && n <= 7){
            if (singletonSets[n] == null) {
                singletonSets[n] = new SingletonSet(n);
            }
            return singletonSets[n];
        }
        return (new SingletonSet(n));
    }

    public IntSet add(int x){
        //same value
        if (value == x){
            return this;
        }
        boolean xIsEven = x % 2 == 0;
        boolean thisIsEven = value % 2 == 0;

        //both even
        if (xIsEven && thisIsEven){
            return (new TreeSet(SingletonSet.singleton(value/2), EmptySet.emptySet())).add(x);
        //both odd
        }
        else if (!xIsEven && !thisIsEven){
            return (new TreeSet(EmptySet.emptySet(), SingletonSet.singleton(value/2))).add(x);
        // x even, this odd
        }else if (xIsEven && !thisIsEven) {
            return new TreeSet(SingletonSet.singleton(x/2), SingletonSet.singleton(value/2));
        // x odd, this even 
        }else {
            // if (!xIsEven && thisIsEven) {
            return new TreeSet(SingletonSet.singleton(value/2), SingletonSet.singleton(x/2));
        } 
    }

    public boolean contains(int x){
        if (value == x) return true;
        else return false;
    }

    public IntSet union(IntSet other){
        if (other.contains(value)){
            return other;
        }else{
            return other.add(value);
        }
    }

    public ArrayList<Integer> localMakeString(){
        ArrayList<Integer> intArr = new ArrayList<>();
        intArr.add(value);
        return intArr;
    }
}
