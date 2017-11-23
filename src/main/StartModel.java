package main;

import java.util.ArrayList;
import java.util.Scanner;

public class StartModel {
	//list of all nodes:
	public static ArrayList<Nodes> node = new ArrayList<>();
	
	public static void main(String[] args) {

		
		Scanner sc = new Scanner(System.in);
		System.out.println("Number of nodes: ");
		int n = sc.nextInt();
		System.out.println("\nInitial friendship chance 0-100.0 - as double: ");
		double chance = sc.nextDouble();
		System.out.println("\nMemory span: ");
		int m = sc.nextInt();
		System.out.println("\nInitial credit [cr]: ");
		int cr = sc.nextInt();
		System.out.println("\nRisk aversion [alpha]: ");
		double alpha = sc.nextDouble();
		//create neigh. matrix
		NMatrix matrix = new NMatrix(n, chance);
		
		//ini nodes and parameters, add them to the arraylist
		NodeFactory nf = new NodeFactory();
		node.addAll(nf.produceNodes(m, n, cr, matrix, alpha));

		
		
		//generate random-ish/small-world like structure
		
		//ini game parameters ie. turn counter, total score counter
		
		//start game
		
			//iterate for each node
		
				//remove nodes with lower reputation than node i
				
				//select a player to play with
					//either from friends or someone outside
				//if targets will play select strategy for both [defect or coop]
				
				//calculate payoffs, add game to interaction history
				
				//if both nodes are able to become friends create the relationship, do it for both sides separately
		
			//end round
		
		//export end data

	}

}
