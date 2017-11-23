package main;

import java.util.ArrayList;

public class Nodes {

	//this node id:
	private int id;
	//total points
	private int tp;
	//memory span:
	private int m;
	//credit value (should equal m)
	int credit;
	//node total number
	private int n;
	//vi coop = vi invite
	private double vicoop;
	//vi accept
	private double viaccept;
	//alpha (risk aversion 0 - 1)
	private double alpha;
	//this node neighbours (friends are 1, unknown are 0):
	private boolean[] myNeighbours;
	// hold score with each node here
	private int[] scores;
	// interaction with nodes outside neighbourhood - we keep only one list for non neighbours 
	private ArrayList<Integer> interactionHistory;
	private int nonNeighbourIndex;
	// history of interaction with all nodes:
	private ArrayList<ArrayList<Integer>> allHistory;
	// last interaction index with node
	private ArrayList<Integer> index;
	public Nodes(int memorySpan, int numberOfNodes, int nodeID, int cr, NMatrix matrix, double coop, double a) {
		tp = 0;
		n = numberOfNodes;
		m = memorySpan;
		credit = cr;
		id = nodeID;
		scores = new int[n];
		alpha = a;
		vicoop = coop;
		viaccept = vicoop * alpha;
		
		// set interaction history to 0 for everyone
		for (int i = 0; i < m; i++) {
			interactionHistory.set(i, 0);
		}
		
		myNeighbours = matrix.getMyNeighbours(id);
		// set initial node interaction history indexes to 0
		for (int k = 0; k < n; k++) {
			index.set(k, 0);
		}
		
		// set initial m interactions with everyone to 0
		for (int l = 0; l < n; l++) {
			for(int n = 0; n< m; n++) {
				allHistory.get(l).set(n, 0);
			}
		}
	}

	//get total points for this node:
	public int setTotalScore() {
		for (int i = 0; i < n; i++) {
			tp += scores[i];
		}
		return tp;
	}
	//set outcome of an interaction with a node
	public void setScore(int node, int score) {
		scores[node] += score;
	}
	//return this node id:
	public int getID() {
		return id;
	}
	//get viaccept:
	public double getViAccept() {
		return viaccept;
	}
	//get vicoop:
	public double getViCoop() {
		return vicoop;
	}
	//get total score:
	public int getTP() {
		return tp;
	}
	//set history of interaction with given node
	public void setInteractionResult(int node, int result) {
		//check if node was a neighbour
		if(myNeighbours[node]==true) {
			//set result score with neighbour
			
			//add score to the history:
			
				//check history index
		}
		//if not neighbour:
		else {
			
		}
	}
}
