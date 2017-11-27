package main;

import java.util.ArrayList;
import java.util.Scanner;

public class StartModel {
	// list of all nodes:
	private static ArrayList<Nodes> node = new ArrayList<>();
	private static int n;
	private static int m;
	private static int cr;
	private static double alpha;
	private static double chance;
	private static NMatrix matrix;
	private static PDGame game;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Number of nodes: ");
		n = sc.nextInt();
		System.out.println("\nInitial friendship chance 0-100.0 - as double: ");
		chance = sc.nextDouble();
		System.out.println("\nMemory span: ");
		m = sc.nextInt();
		System.out.println("\nInitial credit [cr]: ");
		cr = sc.nextInt();
		System.out.println("\nRisk aversion [alpha]: ");
		alpha = sc.nextDouble();
		sc.close();
		// create model structure:
		matrix = new NMatrix(n, chance);

		// ini nodes using model and parameters, add them to the Arraylist
		//NodeFactory nf = new NodeFactory();
		//(int memorySpan, int numberOfNodes, int nodeID, int cr, NMatrix matrix, double coop, double a)
		for(int i = 0; i<n; i++) {
			node.add(new Nodes(m, n, i, cr, matrix, alpha));
		}
		// start the game:
		game = new PDGame(matrix, node);
		// export end data to file:
		

	}

}
