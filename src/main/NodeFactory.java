package main;

import java.util.ArrayList;
import java.util.Random;

public class NodeFactory {
	
	private double coop;
	
	public NodeFactory() {

	}

	// int memorySpan, int numberOfNodes, int nodeID, int cr, NMatrix matrix, double
	// coop, double a
	public ArrayList<Nodes> produceNodes(int m, int n, int credit, NMatrix matrix, double alpha) {
		ArrayList<Nodes> nodes = new ArrayList<>();
		Random rd = new Random();
		
		for (int i = 0; i < n; i++) {
			coop = (double)(rd.nextInt(100)+1)/100.0;
			//for debugging:
			System.out.println(coop);
			nodes.add(new Nodes(m, n, i, credit, matrix, coop, alpha));
		}

		return nodes;
	}
}
