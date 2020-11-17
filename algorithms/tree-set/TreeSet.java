import java.util.ArrayList;

public class TreeSet implements IntSet {
    //private ArrayList<Integer> values;
    private final IntSet left, right;

    public TreeSet(IntSet left, IntSet right){
        this.left = left;
        this.right = right;
    }

    public IntSet add(int x){
        if (contains(x)) {
            return this;
        }
        else if (x % 2 == 0){
            return new TreeSet(left.add(x/2), right);
        }else{
            return new TreeSet(left, right.add(x/2));
        }
    }

    public boolean contains(int x){
        if (x % 2 == 0){
            return this.left.contains(x/2);
        }else{
            return this.right.contains(x/2);
        }
    }

    public IntSet union(IntSet other){
        if (other instanceof TreeSet){
            TreeSet otherTree = (TreeSet) other;
            return new TreeSet(left.union(otherTree.left), right.union(otherTree.right));
        }else{
            return other.union(this);
        }
        //this.left.union(other);
        //this.right.union(other);
    }

    public ArrayList<Integer> localMakeString(){
        ArrayList<Integer> leftString = left.localMakeString();
        for (int i=0; i<leftString.size(); i++){
            leftString.set(i, (leftString.get(i)*2));
        }
        ArrayList<Integer> rightString = right.localMakeString();
        for (int i=0; i<rightString.size(); i++){
            rightString.set(i, ((rightString.get(i)*2)+1));
        }
        leftString.addAll(rightString);
        return leftString;
    }
}
