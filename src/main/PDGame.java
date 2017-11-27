package main;

import java.util.ArrayList;
import java.util.Random;

public class PDGame {

	private NMatrix matrix;
	private ArrayList<Nodes> nodes;
	private int turn;
	private int finalTurn;

	public PDGame(NMatrix nm, ArrayList<Nodes> nd) {
		matrix = nm;
		nodes = nd;
		turn = 0;
		finalTurn = 4;
		// starts PD game:
		while (turn < finalTurn) {
			runGame();
			turn++;
		}
	}

	private void runGame() {
		// iterate for each node

		for (int i = 0; i < nodes.size(); i++) {
			// remove nodes with lower reputation than node i
			int m = nodes.get(i).getMemorySpan();
			double viaccept = nodes.get(i).getViAccept();

			for (int j = 0; j < nodes.size(); j++) {
				int tpij = nodes.get(i).getTPIJ(j);
				double l = (double) (tpij / m);
				// if i!=j, don't compare itself:
				if (i != j) {
					if (l < viaccept) {
						matrix.removeFriend(i, j);
					}
				}
			}
			// select a player to play with
			int selectedNode = nodeSelect(i);

			// Debug:
			// System.out.println("NODE: " + i + " at turn: " + turn + " - selected to play
			// with: " + selectedNode);

			// accept invitation:
			if (acceptInvite(i, selectedNode) == true) {
				// play PD game
				// calculate payoffs
				// update tpij and tpji
			}

			// if both nodes are able to become friends create the relationship, do it for
			// both sides separately
		}
		// end round
	}

	private boolean acceptInvite(int thisNode, int selectedNode) {
		ArrayList<Double> eWij = new ArrayList<>();
		// constant value for both cases:
		//sum of neighbour node values from i to k Wik:
		for (int i = 0; i < nodes.size(); i++) {
			int tpij = nodes.get(thisNode).getTPIJ(i);
			// if neighbour add to the list:
			if (nodes.get(thisNode).isNeighbour(i) == true && i != thisNode) {
				eWij.add((double) ((nodes.get(i).getCredit() + tpij) / (nodes.get(i).getMemorySpan())));
			}
		}
		//if node has no neighbours case kENi = 0:
		if(eWij.size()<=0) {
			eWij.add(0.0);
		}
		// WN'i
		double wni = (double) ((nodes.get(thisNode).getCredit() + nodes.get(thisNode).getNNTPIJ())
				/ (nodes.get(thisNode).getMemorySpan()));
		if (wni < 0) {
			wni = 0.05;
		}
		// value for case if non-neighbour node:
		double wij = (nodes.get(thisNode).getCredit() + nodes.get(thisNode).getTPIJ(selectedNode)) / (nodes.get(thisNode).getMemorySpan());

		if (nodes.get(thisNode).isNeighbour(selectedNode)) {
			//calculate acceptance for neighbour node:
			double acceptance = 0.0;
			for(int t = 0; t < eWij.size(); t++) {
				acceptance += eWij.get(t);
			}
			double result = acceptance / (wni + acceptance);
			
			Random rd = new Random();
			if (result <= ((double)rd.nextInt(100000000))/100000000.0) {
				return true;
			}
			else {
				return false;
			}
			

		} else {
			//calculate acceptance for non-neighbour node:
			double acceptance = 0.0;
			for(int t = 0; t < eWij.size(); t++) {
				acceptance += eWij.get(t);
			}
			double result = acceptance / (wni + acceptance);
			
			Random rd = new Random();
			if (result <= ((double)rd.nextInt(100000000))/100000000.0) {
				return true;
			}
			else {
				return false;
			}
		}
	}

	// function returns node id to ask to play with:
	private int nodeSelect(int z) {

		ArrayList<Double> wij = new ArrayList<>();
		ArrayList<Integer> ijIndex = new ArrayList<>();
		ArrayList<Double> wijnn = new ArrayList<>();
		ArrayList<Integer> ijnnIndex = new ArrayList<>();
		// calculate wni
		double wni = (double) ((nodes.get(z).getCredit() + nodes.get(z).getNNTPIJ()) / (nodes.get(z).getMemorySpan()));
		// in case where wni < 0 assume wni = min weight = 0.05
		if (wni < 0) {
			wni = 0.05;
		}
		// calculate wik [sum of wij without wni]
		for (int i = 0; i < nodes.size(); i++) {
			int tpij = nodes.get(z).getTPIJ(i);
			// if neighbour add to the list:
			if (nodes.get(z).isNeighbour(i) == true && i != z) {
				wij.add((double) ((nodes.get(i).getCredit() + tpij) / (nodes.get(i).getMemorySpan())));
				// remember which node was it:
				ijIndex.add(i);
			}
			if (nodes.get(z).isNeighbour(i) == false && i != z) {
				wijnn.add((double) ((nodes.get(i).getCredit() + tpij) / (nodes.get(i).getMemorySpan())));
				// remember which node was it:
				ijnnIndex.add(i);
			}
		}
		System.out.println("Node" + z + " id neighbours: " + ijIndex);
		System.out.println("Node" + z + " id non neighbours: " + ijnnIndex);
		// begin roulette calculations
		double sumWik = 0.0;
		// sum of node neighbourhood wij values:
		for (int k = 0; k < wij.size(); k++) {
			sumWik += wij.get(k);
		}

		double sumWiknn = 0.0;
		// sum of node non neighbourhood wijnn values:
		for (int k = 0; k < wijnn.size(); k++) {
			sumWiknn += wijnn.get(k);
		}

		// n-1 nodes:
		double[] roulette = new double[nodes.size() - 1];
		int[] node = new int[nodes.size() - 1];

		// build roulette start:
		// first check if node has any neighbours:
		if (wij.size() <= 0) {
			System.out.println("warning node: " + z + " has no neighbours using alternative");
			// first check if there are any non-neighbours:
			for (int l = 0; l < wijnn.size(); l++) {
				if (l == 0) {
					roulette[l] = (((wni) / (wni + sumWik)) * (wijnn.get(l) / (sumWiknn))) + roulette[wij.size() - 1];
					node[l + wij.size()] = ijnnIndex.get(l);
				} else {
					// sum previous node value so that we will get ie: 20%, 24%(4%), 34%(10%)...
					// 100%(1%)
					roulette[l + wij.size()] = (((wni) / (wni + sumWik)) * (wijnn.get(l) / (sumWiknn)))
							+ roulette[wij.size() - 1 + l];
					// keep node index
					node[l + wij.size()] = ijnnIndex.get(l);
				}
			}

			// roulette completed:
			// time to randomly pick the result:
			Random rd = new Random();
			int randomed = rd.nextInt(1000000000);
			// get value between 0.0000 and 1.0000 as double:
			double converted = (double) randomed / 1000000000.0;
			// finally find number which we drew and return the result:
			for (int t = 0; t < roulette.length; t++) {
				System.out.println(roulette[t] + " is it higher than: " + converted + "for node id: " + node[t]);
				if (roulette[t] >= converted) {
					return node[t];
				}
			}
		}
		// if node has neighbours and non neighbours:
		if (wij.size() > 0 && wijnn.size() > 0) {
			for (int j = 0; j < wij.size(); j++) {
				if (j == 0) {
					roulette[j] = (wij.get(j)) / (wni + sumWik);
					node[j] = ijIndex.get(j);
				} else {
					// sum previous node value so that we will get ie: 20%, 24%(4%), 34%(10%)...
					// 100%(1%)
					roulette[j] = ((wij.get(j)) / ((wni + sumWik))) + roulette[j - 1];
					// keep node index
					node[j] = ijIndex.get(j);
				}
			}

			for (int l = 0; l < wijnn.size(); l++) {
				if (l == 0) {
					roulette[l + wij.size()] = (((wni) / (wni + sumWik)) * (wijnn.get(l) / (sumWiknn)))
							+ roulette[wij.size() - 1];
					node[l + wij.size()] = ijnnIndex.get(l);
				} else {
					// sum previous node value so that we will get ie: 20%, 24%(4%), 34%(10%)...
					// 100%(1%)
					roulette[l + wij.size()] = (((wni) / (wni + sumWik)) * (wijnn.get(l) / (sumWiknn)))
							+ roulette[wij.size() - 1 + l];
					// keep node index
					node[l + wij.size()] = ijnnIndex.get(l);
				}
			}
		}
		// check if node has only neighbours:
		if (wijnn.size() <= 0) {
			for (int j = 0; j < wij.size(); j++) {
				if (j == 0) {
					roulette[j] = (wij.get(j)) / (wni + sumWik);
					node[j] = ijIndex.get(j);
				} else {
					// sum previous node value so that we will get ie: 20%, 24%(4%), 34%(10%)...
					// 100%(1%)
					roulette[j] = ((wij.get(j)) / ((wni + sumWik))) + roulette[j - 1];
					// keep node index
					node[j] = ijIndex.get(j);
				}
			}
		}

		// roulette completed:
		// time to randomly pick the result:
		Random rd = new Random();
		int randomed = rd.nextInt(1000000000);
		// get value between 0.0000 and 1.0000 as double:
		double converted = (double) randomed / 1000000000.0;
		// finally find number which we drew and return the result:
		for (int t = 0; t < roulette.length; t++) {
			System.out.println(roulette[t] + " is it higher than: " + converted + "for node id: " + node[t]);
			if (roulette[t] >= converted) {
				return node[t];
			}
		}
		return -1;

	}
}
