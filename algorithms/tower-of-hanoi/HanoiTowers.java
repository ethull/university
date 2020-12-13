/**
 * HanoiTowers
 * @author ethull
*/

import java.util.Scanner;
import java.io.*;
import java.util.Stack;

public class HanoiTowers {

	public HanoiTowers (){}

	//used to remember the last few buffer towers in the second part of each iteration
	//could be replaced with internal arrays but this would be memory inefficient
	private Stack<Integer> currentBufferTowers = new Stack<Integer>();

	public void pushTower(int n){
		currentBufferTowers.push(n);
	}

	public int popTower(){
		return currentBufferTowers.pop();
	}

	//will use all towers if there are enougth towers
	public int getNewTower(int t, int source_tower, int destination_tower, int lastBufferTower){
		int nextTower = lastBufferTower+1;
		while (true){
			//check if next tower is a tower
			if (nextTower <=t){
				//check if next tower is a src or dest tower
				if (nextTower != source_tower && nextTower != destination_tower){
					//if not return
					return nextTower;
				} else {
					//otherwise check the next tower
					nextTower++;
				}
			} else{
				nextTower = 1;
			}
		
		}
	}
	
	public int print_move (int disc, int source_tower, int destination_tower, FileWriter writer){
		try {
			System.out.printf("\nMove disk %d from T%d to T%d", disc, source_tower, destination_tower);
			writer.write("\n" + disc + "\t" +  source_tower + "\t" + destination_tower);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	// The recursive function for moving n discs 
	//           from s to d with t-2 buffer towers.
	//           It prints all the moves with disk numbers.
	//           n: number_of_discs, t: number_of_towers
	public int move_t (int n, int t, int source_tower, int destination_tower, FileWriter writer, int lastBufferTower, int recBufferTower){
		
		if (n-1 < (t-2)){
			// mv towers from 0...n-(t-2)
			//	on first top-lv recursive run to buffer node
			//  on second top-lv recursive run to dest node
			
			//lowest bt -> recursive pt, iteration starts at right goes left
			for (int i = 1; i <= n; i++) {
				int dest_tower = 0;

				//for number n, last one is always the dest tower
				if (i==n){
					dest_tower=destination_tower;
				}else {
				//otherwise generate buffer tower
					dest_tower=getNewTower(t, source_tower, destination_tower, lastBufferTower);
					lastBufferTower=dest_tower;
					currentBufferTowers.push(lastBufferTower);
				}
				//for the first numbers (counting up), first one is always the src tower
				print_move(i, source_tower, dest_tower, writer);
			}
			for (int i = 1; i < n; i++) {
				//for the second numbers (counting down), last one is always the dest tower
				print_move((n-i), currentBufferTowers.pop(), destination_tower, writer);
			}

			return 0;
		}

		//if its the first time being run, use destination_tower as the lastBufferTower
		if (lastBufferTower == 0){
			lastBufferTower = getNewTower(t, source_tower, destination_tower, destination_tower);
			recBufferTower=lastBufferTower;
		} else {
			lastBufferTower=getNewTower(t, source_tower, destination_tower, lastBufferTower);
			//set the recursionBufferTower, this is for the second recursive calls src tower
			recBufferTower=lastBufferTower;
		}

		//mv 1...n-(t-2) disks to last buffer tower (as many disks that can be moved at once as possible)
		move_t(n-(t-2), t, source_tower, lastBufferTower, writer, lastBufferTower, recBufferTower);

		//mv n-(t-2)+1...n towers to buffer towers not used yet	
		//this will cover towers from the last n (used in iteration) to the current
		for (int i = ((n-(t-2))+1); i <= n; i++) {
			int dest_tower = 0;
			//for number n, last one is always the dest tower
			if (i==n){
				dest_tower=destination_tower;
			}else {
			//otherwise generate buffer tower
				dest_tower=getNewTower(t, source_tower, destination_tower, lastBufferTower);
				lastBufferTower=dest_tower;
				currentBufferTowers.push(lastBufferTower);
			}
			//for the first numbers (counting up), first one is always the src tower
			print_move(i, source_tower, dest_tower, writer);
		}
		//mv t-1...n towers from their buffer towers to the dest tower
		for (int i = n-1; i >= ((n-(t-2))+1); i--) {
			//for the second numbers (counting down), last one is always the dest tower
			print_move(i, currentBufferTowers.pop(), destination_tower, writer);
		}

		//reuse buffer tower set by the last upper recursive call
		lastBufferTower=recBufferTower;

		//mv 0...n-(t-2) disks from last buffer tower -> dest tower
		move_t(n-(t-2), t, lastBufferTower, destination_tower, writer, lastBufferTower, recBufferTower);
		return 0;
	}

	public static void main(String[] args) {
		Scanner myObj = new Scanner(System.in);  // Create a Scanner object
		int n, t, s, d;
		String my_ID = new String("un");

		if (args.length < 4){
			System.out.printf("Usage: java %s_task1 <n> <t> <s> <d>\n", my_ID);
			return;
		}

		n = Integer.parseInt(args[0]);  // Read user input n
		t = Integer.parseInt(args[1]);  // Read user input t
		s = Integer.parseInt(args[2]);  // Read user input s
		d = Integer.parseInt(args[3]);  // Read user input d

		// Check the inputs for sanity
		if (n<1 || t<3 || s<1 || s>t || d<1 || d>t){
			System.out.printf("Please enter proper parameters. (n>=1; t>=3; 1<=s<=t; 1<=d<=t)\n");
			return;
		}

		// Create the output file name
		String filename;
		filename = new String(my_ID + "_ToH_n" + n + "_t" + t + "_s" + s + "_d" + d + ".txt");
		try {
			// Create the Writer object for writing to "filename"
			FileWriter writer = new FileWriter(filename, true);

			// Write the first line: n, t, s, d
			writer.write(n + "\t" + t + "\t" + s + "\t" + d);

			// Create the object of this class to solve the generalised ToH problem
			HanoiTowers ToH = new HanoiTowers();

			//System.out.printf("\nFollowing is a set of dummy moves for n=6, t=5, s=1, d=2");
			System.out.printf("\noutput also written to %s", filename);

			// Call the recursive function that solves the ToH problem
			ToH.move_t(n, t, s, d, writer, 0, 0);

			// Close the file
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.printf("\n");
		return;
	}
}
