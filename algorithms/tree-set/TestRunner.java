/**
 * TestRunner
 */
public class TestRunner {
    public static void main(String[] args){
        IntSet set = EmptySet.emptySet().add(2).add(3).add(4).add(5).add(6).add(8);
        IntSet set2 = EmptySet.emptySet().add(1).add(3).add(4).add(7).add(9);
        System.out.println(set.makeString());
        System.out.println(set2.makeString());
        IntSet s3 = set.union(set2);
        System.out.println(s3.makeString());
    }
}
