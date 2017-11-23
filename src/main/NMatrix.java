package main;

import java.util.Random;

public class NMatrix {

	//shared matrix which makes the network model:
	private boolean[][] matrix;
	//number of nodes
	private int n;
	//row of neighbours for single node
	private boolean[] neighbours;

	public NMatrix(int totalN, double trustIniChance) {
		n = totalN;
		matrix = new boolean[n][n];

		generateStructure(matrix, trustIniChance);
	}

	// generates random network with a given chance to form an early friendship
	private void generateStructure(boolean[][] matrix, double trustIniChance) {
		// total random structure
		Random rd = new Random();

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if ((double) rd.nextInt(100) <= trustIniChance) {
					matrix[i][j] = true;
					matrix[j][i] = true;
				}
			}
		}
	}

	// get node neighbours from matrix
	public boolean[] getMyNeighbours(int id) {
		for (int i = 0; i < n; i++) {
			neighbours[i] = matrix[id][i];
		}
		return neighbours;
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
