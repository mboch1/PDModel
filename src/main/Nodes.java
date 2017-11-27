package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Nodes {

	// this node id:
	private int id;
	// total points
	private int tp;
	// memory span:
	private int m;
	// credit value (should equal m)
	private int credit;
	// node total number
	private int n;
	// vi coop = vi invite
	private double vicoop;
	// vi accept
	private double viaccept;
	// alpha (risk aversion 0 - 1)
	private double alpha;
	// this node neighbours (friends are 1, unknown are 0):
	private boolean[] myNeighbours;
	// hold score with each node here
	private int[] scores;
	// interaction with nodes outside neighbourhood - we keep only one list for non
	// neighbours
	private int[] nnHistory;
	private int nnIndex;
	// history of interaction with all nodes:
	private int[][] history;
	// last interaction index with node
	private int[] index;

	public Nodes(int memorySpan, int numberOfNodes, int nodeID, int cr, NMatrix matrix, double a) {
		tp = 0;
		n = numberOfNodes;
		m = memorySpan;
		credit = cr;
		id = nodeID;
		scores = new int[n];
		alpha = a;
		Random rd = new Random();
		
		vicoop = (double)(rd.nextInt(100000000)/100000000.0);
		System.out.println(vicoop);
		viaccept = vicoop * alpha;
		// set interaction history to 0 for m last node interactions from the outside of
		// the neighbourhood
		nnHistory = new int[m];
/*		Random rd = new Random();
		for (int i = 0; i < nnHistory.length; i++) {
			nnHistory[i] = rd.nextInt(3) - 1;
		}*/

		// create index to remember what was the last interaction
		nnIndex = 0;
		// set initial m last interactions with everyone to 0
		history = new int[n][m];
/*		for (int j = 0; j < history.length; j++) {
			for (int k = 0; k < m; k++) {
				if (j != id) {
					history[j][k] = rd.nextInt(3) - 1;
				}
			}
		}*/
		// create index to remember what was the last interaction
		index = new int[n];
		// get initial neighbourhood matrix:
		myNeighbours = matrix.getMyNeighbours(id);
	}

	// get total points for this node:
	public int getTotalScore() {
		for (int i = 0; i < n; i++) {
			tp += scores[i];
		}
		return tp;
	}

	// return this node id:
	public int getID() {
		return id;
	}

	// get viaccept:
	public double getViAccept() {
		return viaccept;
	}

	// get vicoop:
	public double getViCoop() {
		return vicoop;
	}

	// get vicoop:
	public int getMemorySpan() {
		return m;
	}

	// get viaccept:
	public double getCredit() {
		return credit;
	}

	// set neighbour true false
	public void setNeighbour(int nodeID, boolean friend) {
		myNeighbours[nodeID] = friend;
	}

	// set history of interaction with given node
	public void setInteractionResult(int node, int result) {
		// set result score with neighbour
		scores[node] += result;
		// add score to the history:
		// manage all interaction history:
		// if index is below memory span do:
		if (index[node] < m) {
			// set the result history at the given index:
			history[node][index[node]] = result;
			// increase index for this node:
			index[node] += 1;
		}
		// restart counter:
		else {
			index[node] = 0;
			// set the result history at the given index:
			history[node][index[node]] = result;
		}
		// if not neighbour:
		if (myNeighbours[node] == false) {
			// set non-neighbour interaction history:
			if (nnIndex < m) {
				nnHistory[nnIndex] = result;
				nnIndex += 1;
			}
			// restart counter:
			else {
				nnIndex = 0;
				nnHistory[nnIndex] = result;
			}
		}
	}

	// get total score from m last rounds between node i and node j:
	public int getTPIJ(int j) {
		int TPIJ = 0;
		for (int k = 0; k < m; k++) {
			TPIJ += history[j][k];
		}
		System.out.println("TPIJ: "+TPIJ);
		return TPIJ;
	}

	// get total score from m last rounds between node i and non-neighbours:
	public int getNNTPIJ() {
		int TPIJ = 0;
		for (int k = 0; k < m; k++) {
			TPIJ += nnHistory[k];
		}
		return TPIJ;
	}

	// check if nodes are neighbours
	public boolean isNeighbour(int j) {
		return myNeighbours[j];
	}
}
