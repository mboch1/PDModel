package main;

import java.util.Arrays;
import java.util.Random;

public class NMatrix {

	// shared matrix which makes the network model:
	private boolean[][] matrix;
	// number of nodes
	private int n;
	// row of neighbours for single node
	private boolean[] neighbours;
	// values for generating random structure:
	private int connections;
	private int min;
	private int max;
	// initial factor, to match average number of connections:
	private double targetFactor = 0.0;
	private double currentFactor = 0.0;
	private double finalFactor;

	public NMatrix(int totalN, double trustIniChance) {
		n = totalN;
		matrix = new boolean[n][n];
		neighbours = new boolean[n];
		// total random structure with a trust chance (friendship) per each node at the
		// game start:
		generateStructure(matrix, trustIniChance);

		// more controlled random creation:
		// create nodes and neighbourhood:
		min = 0;
		// keep max value +1 higher than expected for random generator
		max = 5;
		// total number of connections / (number of nodes * average connections/2 * 2)
		// -> since two-sided connections
		// targetFactor = 0.35;
		// createConnections(min, max);
	}

	// populates matrix with random connections between nodes, the connections are
	// random and single sided, as in original paper:
	private void createConnections(int min2, int max2) {
		Random rd = new Random();
		// for each node:
		for (int i = 0; i < n; i++) {
			int c = rd.nextInt(max);

			if (c > 0) {
				for (int j = 0; j < c; j++) {
					boolean flag = true;
					// ensure that the node is not connected to itself:
					while (flag == true) {
						int nodeToConnect = rd.nextInt(n);
						if (nodeToConnect != i) {
							matrix[i][nodeToConnect] = true;
							flag = false;
						}
					}
				}
			}
			currentFactor += c;
		}
		finalFactor = currentFactor / ((double) n * (max - 1 - min));
		System.out.println("Final factor is: " + finalFactor + " target was: " + targetFactor);
	}

	// generates random network with a given chance to form an early friendship
	private void generateStructure(boolean[][] matrix, double trustIniChance) {
		// total random structure
		Random rd = new Random();

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if ( (((double)rd.nextInt(100)) / (100.0)) <= ((trustIniChance)/(100.0)) && i != j) {
					matrix[i][j] = true;
					matrix[j][i] = true;
				} else {
					matrix[i][j] = false;
					matrix[j][i] = false;
				}
			}
		}
		System.out.println(Arrays.deepToString(matrix));
	}

	// get node neighbours from matrix
	public boolean[] getMyNeighbours(int id) {
		boolean[] myNeighbours = new boolean[n];
		for (int i = 0; i < n; i++) {
			if(matrix[id][i]==true) {
				myNeighbours[i] = true;
			}
			else {
				myNeighbours[i] = false;
			}
		}
		return myNeighbours;
	}

	// set new friendship - update both rows
	public void setBothFriends(int myID, int friendID) {
		matrix[myID][friendID] = true;
		matrix[friendID][myID] = true;
	}

	// remove friend - does not remove other connection as in the research paper
	public void removeFriend(int myID, int friendID) {
		matrix[myID][friendID] = false;
	}

}
