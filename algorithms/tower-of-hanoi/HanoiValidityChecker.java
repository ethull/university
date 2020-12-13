/**
 * HanoiValidityChecker
 * @author ethull
*/

import java.util.*;
import java.io.*;
import java.util.ArrayList;

public class HanoiValidityChecker {

	//tower number is = the towers index+1
	private static ArrayList<ArrayList<Integer>> towers = new ArrayList<>();
	//used to keep track of what towers have been used
	private static ArrayList<Integer> usedTowers = new ArrayList<>();

	public HanoiValidityChecker (){}

	public static boolean isBlank (int character) {
		if (
		character == ' ' ||
		character == '\t' ||
		character == '\n' ||
		character == '\r'
		)
			return true;
		return false;

	}

	// This function only works assuming that the file has positive integers
	public static int getNextInteger (FileInputStream input_file) {
		int character;
		int digit;
		int number = 0;
		try {
			while ((character = input_file.read()) != -1 && !isBlank(character))
			{
				number *= 10;
				digit = (int) character - '0';
				number += digit;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return number;
	}

	//check if a tower a tower addition will break hanoi rules
	public static boolean evalTowers(ArrayList<Integer> srcTower, ArrayList<Integer> destTower, int n){
		boolean isValid = true;
		//if (!srcTower.contains(n)){
		//check if disc number is at the top of the src tower
		if (srcTower.size() == 0){
			System.out.printf("Move error: Source tower: " + srcTower.toString() + ", is empty, hence it cant have disc " + n + " at its top\n");
		} else {
			if (srcTower.get(srcTower.size()-1) != n){
				System.out.printf("Move error: Source tower: " + srcTower.toString() + ", does not have disc " + n + " at its top\n");
				isValid = false;
			}
		}

		//check dest tower has any discs
		if (!(destTower.size() == 0)){
			//check if disk being moved is smaller than the disc ontop of the dest tower
			if (destTower.get(destTower.size()-1) < n){
				System.out.printf("Move error: Destination tower: " + destTower.toString() + ", has a disk smaller than " + n + "\n");
				isValid = false;
			}
		}
		return isValid;
	}

	//evaluate towers after loop
	public static boolean endEvaltowers(int finalDestTowerNum, int numOfTowers, int numOfDisks){
		boolean isValid = true;
		//check if any towers other than destination are empty
		for (int i = 0; i < towers.size(); i++) {
			if (towers.get(i).size() > 0 && (i+1) != finalDestTowerNum){
				System.out.println("End error: Tower " + i + ": " + towers.get(i).toString() + " should be empty\n");
				isValid = false;
			}
			
		}

		//if its uneccessary to use all towers for full efficiency (when theres more towers than disks) use the minimal amount of towers needed
		//  so if n100 t1000 s1000 d999
		//  towers 1 to 99 and 999, towers 100...998 are not required
		if (numOfTowers > numOfDisks){
			numOfTowers = numOfDisks-1;
		}
		//check if all towers have been used
		for (int i = 1; i <= numOfTowers; i++) {
			if (!(usedTowers.contains(i))){
				System.out.println("End error: Tower " + i + ": has not been used\n");
				isValid = false;
			}
		}
		//check if the dest tower is used separately
		if (!(usedTowers.contains(finalDestTowerNum))){
			System.out.println("End error: Tower " + finalDestTowerNum + ": has not been used\n");
			isValid = false;
		}


		return isValid;
	}

	//print current status of all towers
	public static void printTowerStatuses(){
		System.out.println("The status of all the towers is as follows:");
		for (int i = 0; i < towers.size(); i++) {
			//System.out.println("Tower " + (i+1) + ": "  + towers.get(i).toString());
			System.out.printf("Tower %d: %s\n", (i+1), towers.get(i).toString());
		}
		System.out.println();
	}

	// main
	public static void main(String[] args) {
		Scanner myObj = new Scanner(System.in);  // Create a Scanner object
		int n, t, s, d;
		String str_filename;
		String my_ID = new String("un");

		// Check if the input filename has been provided as an argument
		if (args.length < 1)
		{
			System.out.printf("Usage: java %s_task2 <file_name>\n", my_ID);
			return;
		}

		try {
			// Get the filename
			str_filename = args[0];
			System.out.printf("Reading the file " + str_filename + "\n");

			// Create the object for reading the input file
			FileInputStream input_file = new FileInputStream(str_filename);

			//boolean used throughout the program to determine if the set of moves are true for the current problem
			boolean isValid = true;

			// Read the four parameters in the first row of the input file
			n = getNextInteger(input_file);
			t = getNextInteger(input_file);
			s = getNextInteger(input_file);
			d = getNextInteger(input_file);
			System.out.printf("%d\t%d\t%d\t%d\n", n, t, s, d);

			//store problem variables for later use
			int finalDestTowerNum = d;
			int numOfDisks = n;
			int numOfTowers = t;
			

			//create arr for each tower
			for (int i = 0; i < t; i++) {
				towers.add(new ArrayList<>());
			}


			//fill initial tower with discs
			ArrayList<Integer> initialTower = towers.get((s-1));
			for (int i = 0; i < n; i++) {
				initialTower.add((n-i));
			}
			usedTowers.add(Integer.valueOf(s));

			//print state of initial towers
			printTowerStatuses();

			int a = 0; //counter for while loop
			//iterate the file
			while (true){
				n = getNextInteger(input_file);
				s = getNextInteger(input_file);
				d = getNextInteger(input_file);
				//if at last line break
				if (n == 0) {
					break;	
				}
				//check if n,s,d are inbounds of the problem
				if (n > numOfDisks){
					System.out.printf("disc number %d\n is out of the problem scope for the number of disks which is %d\n", n, numOfDisks);
					isValid = false;
					break;
				} else if (s > numOfTowers){
					System.out.printf("Source tower %d is out of the problem scope for the number of towers which is %d\n", s, t);
					isValid = false;
					break;
				} else if (d > numOfTowers){
					System.out.printf("Destination tower %d is out of the problem scope for the number of towers which is %d\n", d, t);
					isValid = false;
					break;
				}

				//get src and dest towers
				ArrayList<Integer> currentSrcTower = towers.get(s-1);
				ArrayList<Integer> currentDestTower = towers.get(d-1);

				//if dest tower has not been used yet, add it to the list of used towers
				if (!(usedTowers.contains(d))){
					usedTowers.add(Integer.valueOf(d));
				}

				//print the move and status of towers before it
				System.out.printf("Move: disc %d from tower %d to tower %d\n", n, s, d);
				System.out.printf("Before the move:\n");
				System.out.printf("Source tower %d: %s\n", s, currentSrcTower.toString());
				System.out.printf("Destination tower %d: %s\n", d, currentDestTower.toString());

				//check if evaluate towers doesnt return any errors
				if (evalTowers(currentSrcTower, currentDestTower, n)){
					currentSrcTower.remove(Integer.valueOf(n));
					currentDestTower.add(n);
					//status of towers after the move
					System.out.printf("After the move:\n");
					System.out.printf("Source tower %d: %s\n", s, currentSrcTower.toString());
					System.out.printf("Destination tower %d: %s\n", d, currentDestTower.toString());
					System.out.println();
				} else {
					System.out.println();
					isValid = false;
					break;
				}
				a++;
			}
			//if no errors so far check condition of towers after the algorithm
			if (isValid){
				isValid = endEvaltowers(finalDestTowerNum, numOfTowers, numOfDisks);
			}
			printTowerStatuses();
			if (isValid){
				System.out.println("The sequence of moves is correct");
			} else {
				System.out.println("The sequence of moves is incorrect");
			}

			// Close the file
			input_file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.printf("\n");
		return;
	}
}
