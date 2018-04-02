package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.mxgraph.swing.mxGraphComponent;

public class PDGame {

	private NMatrix matrix;
	public ArrayList<Nodes> nodes;
	private int turn;
	private int finalTurn;
	private JFrame frmPdgApplication;
	private JPanel panel;
	private mxGraphComponent graph;
	private ResultsGraph draw;

	public PDGame(NMatrix nm, ArrayList<Nodes> nd) {
		matrix = nm;
		nodes = nd;
		turn = 0;
		finalTurn = 1000;
		// starts PD game:
		while (turn < finalTurn) {
			runGame(turn);
			turn++;
		}

		// prepare frame for graph
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					printGraph();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// draw graph based on results
		draw = new ResultsGraph(nm, nd);
		redraw();

		// print end results:
		printToFile();
	}

	private void printGraph() {

		frmPdgApplication = new JFrame();
		frmPdgApplication.setResizable(true);
		frmPdgApplication.setTitle("PDG Application");
		frmPdgApplication.setSize(1024, 768);
		frmPdgApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPdgApplication.setLayout(new BorderLayout());

		// add game results to frame
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(frmPdgApplication.getContentPane().getSize());

		frmPdgApplication.add(panel);
		frmPdgApplication.pack();
		frmPdgApplication.setVisible(true);

	}

	private void redraw() {
		// panel.removeAll();
		this.graph = draw.drawGraph();
		panel.add(graph);
		panel.revalidate();
		panel.repaint();
	}

	private void printToFile() {
		Charset utf8 = StandardCharsets.UTF_8;

		try {
			ArrayList<String> data = new ArrayList<>();
			ArrayList<Integer> scores = new ArrayList<>();
			ArrayList<Integer> neighbours = new ArrayList<>();
			ArrayList<Integer> friendships = new ArrayList<>();
			ArrayList<String> resultData = new ArrayList<>();

			boolean matrixToWrite[][] = matrix.getMatrix();
			// convert matrix data to printable version:
			// for (int m = 0; m < matrixToWrite.length; m++) {
			// matrixData.add(Arrays.toString(matrixToWrite[m]));
			// }
			String results = "ID" + "\t" + "Total Score" + "\t" + "ViCoop" + "\t" + "Neighbours" + "\t" + "Friends";
			resultData.add(results);

			for (int k = 0; k < nodes.size(); k++) {
				int sumNeighbours = 0;
				int sumFriends = 0;
				int nodeScore = nodes.get(k).getTotalScore();

				for (int m = 0; m < nodes.size(); m++) {
					if (matrixToWrite[k][m] == true && matrixToWrite[m][k]) {
						sumFriends++;
					}
					if (matrixToWrite[k][m] == true && matrixToWrite[m][k] == false) {
						sumNeighbours++;
					}
				}
				// add results to array lists:
				neighbours.add(sumNeighbours);
				friendships.add(sumFriends);
				scores.add(nodeScore);
			}
			String id = "ID\t";
			String coop = "Vcoop\t";
			String sc = "Scores\t";
			String n = "Neighbours\t";
			String f = "Friends\t";
			String d = "Defected\t";
			String c = "Cooperated\t";

			for (int t = 0; t < nodes.size(); t++) {
				id = id + Integer.toString(t) + "\t";
				sc = sc + Integer.toString(scores.get(t)) + "\t";
				coop = coop + nodes.get(t).getViCoop() + "\t";
				n = n + Integer.toString(neighbours.get(t)) + "\t";
				f = f + Integer.toString(friendships.get(t)) + "\t";
				d = d + Integer.toString(nodes.get(t).getTimesDef()) + "\t";
				c = c + Integer.toString(nodes.get(t).getTimesCoop()) + "\t";
			}

			data.add(id);
			data.add(sc);
			data.add(coop);
			data.add(n);
			data.add(f);
			data.add(d);
			data.add(c);

			// Files.write(Paths.get("matrix.txt"), matrixData, utf8);
			Random rd = new Random();

			Files.write(Paths.get(Integer.toString(rd.nextInt(100000)) + " data.txt"), data, utf8);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void runGame(int turn) {
		// iterate for each node

		for (int i = 0; i < nodes.size(); i++) {
			// remove nodes with lower reputation than node i
			double m = nodes.get(i).getMemorySpan();
			double viaccept = nodes.get(i).getViAccept();
			for (int j = 0; j < nodes.size(); j++) {
				if (nodes.get(i).isNeighbour(j) && i != j && turn > -1) {
					double tpij = nodes.get(i).getTPIJ(j);
					double l = tpij / m;
					if (l < viaccept) {
						matrix.removeFriend(i, j);
						nodes.get(i).setNeighbour(j, false);
					}
				}
			}
			// select a player to play with
			int selectedNode = selectToPlay(i);

			// accept invitation:
			if (acceptInvite(i, selectedNode) == true) {
				// play PD game
				// System.out.println("invitation accepted");
				boolean thisNode = selectStrategy(nodes.get(i).getViCoop());
				boolean chosenNode = selectStrategy(nodes.get(selectedNode).getViCoop());
				// calculate payoffs and update tpij and tpji
				calculatePayoffs(thisNode, chosenNode, i, selectedNode);
			}

			// if both nodes are able to become friends create the relationship, do it for
			// both sides separately
			double tpij = nodes.get(i).getTPIJ(selectedNode);
			double tpji = nodes.get(selectedNode).getTPIJ(i);
			double Viinvite = nodes.get(i).getViCoop();
			double Viaccept = nodes.get(i).getViAccept();
			double Vjaccept = nodes.get(selectedNode).getViAccept();
			double Vjinvite = nodes.get(selectedNode).getViCoop();
			double memSpan = nodes.get(i).getMemorySpan();

			// form relationship if both are true:
			if (Viinvite < (tpij / memSpan) && (tpji / memSpan) > Vjaccept) {
				nodes.get(i).setNeighbour(selectedNode, true);
				nodes.get(selectedNode).setNeighbour(i, true);
				// share and update neighbours now:
				shareFriends(i, selectedNode);
				matrix.setBothFriends(i, selectedNode);
			}
			if (Vjinvite < (tpji / memSpan) && (tpij / memSpan) > Viaccept) {
				nodes.get(i).setNeighbour(selectedNode, true);
				nodes.get(selectedNode).setNeighbour(i, true);
				// share and update neighbours now:
				shareFriends(selectedNode, i);
				matrix.setBothFriends(selectedNode, i);
			}

		}
		// end of round
	}

	private void shareFriends(int i, int selectedNode) {
		boolean[] n = nodes.get(i).getNeighbours();
		boolean[] m = nodes.get(selectedNode).getNeighbours();

		for (int j = 0; j < n.length; j++) {
			// update n array:
			if (i != j) {
				if (n[j] == false && m[j] == true) {
					n[j] = true;
				}
			}
			// update m array:
			if (selectedNode != j) {
				if (n[j] == true && m[j] == false) {
					m[j] = true;
				}
			}
		}
		// update myNeighbours in Nodes:
		for (int k = 0; k < n.length; k++) {
			nodes.get(i).setNeighbour(k, n[k]);
			nodes.get(selectedNode).setNeighbour(k, m[k]);
		}

		// update matrix with new data:
		matrix.setNeighbourhood(n, i);
		matrix.setNeighbourhood(m, selectedNode);
	}

	private void calculatePayoffs(boolean activeResult, boolean targetResult, int active, int target) {
		// calculates payoffs based on PD punctuation and updates tp tables for both
		// nodes:
		// A coop, B coop = 1,1, A def, B coop = 2,-1, A coop, B def = -1,2, A def B def
		// = 0,0
		if (activeResult == true && targetResult == true) {
			nodes.get(active).setInteractionResult(target, 1);
			nodes.get(target).setInteractionResult(active, 1);
			nodes.get(active).setTimesCoop();
			nodes.get(target).setTimesCoop();

		}
		if (activeResult == false && targetResult == true) {
			nodes.get(active).setInteractionResult(target, 2);
			nodes.get(target).setInteractionResult(active, -1);
			nodes.get(active).setTimesDef();
			nodes.get(target).setTimesCoop();
		}
		if (activeResult == true && targetResult == false) {
			nodes.get(active).setInteractionResult(target, -1);
			nodes.get(target).setInteractionResult(active, 2);
			nodes.get(active).setTimesCoop();
			nodes.get(target).setTimesDef();
		}
		if (activeResult == false && targetResult == false) {
			nodes.get(active).setInteractionResult(target, 0);
			nodes.get(target).setInteractionResult(active, 0);
			nodes.get(active).setTimesDef();
			nodes.get(target).setTimesDef();
		}
	}

	private boolean selectStrategy(double viCoop) {
		Random rd = new Random();
		double check = rd.nextInt(10000) / 10000.0;
		// returns true if node is going to cooperate, false otherwise
		if (viCoop >= check) {
			return true;
		} else {
			return false;
		}
	}

	private boolean acceptInvite(int thisNode, int selectedNode) {
		double m = nodes.get(selectedNode).getMemorySpan();
		double cr = nodes.get(selectedNode).getCredit();
		double nntpij = nodes.get(selectedNode).getNNTPIJ();
		double wn = (cr + nntpij) / m;

		if (wn <= 0) {
			wn = 0.05;
		}

		double sumWij = 0;
		for (int i = 0; i < nodes.size(); i++) {
			// if neighbour add to the list:
			if (nodes.get(selectedNode).isNeighbour(i) == true && nodes.get(i).isNeighbour(selectedNode) == true
					&& i != selectedNode) {
				double tpij = nodes.get(selectedNode).getTPIJ(i);
				sumWij += (cr + tpij) / m;
			}
		}

		if (nodes.get(thisNode).isNeighbour(selectedNode) == true
				&& nodes.get(selectedNode).isNeighbour(thisNode) == true) {
			double acceptance = sumWij / (wn + sumWij);

			Random rd = new Random();
			int value = rd.nextInt(100000);
			double check = value / 100000.0;
			if (check <= acceptance) {
				return true;
			} else {
				return false;
			}
		} else {
			double tp = nodes.get(selectedNode).getTPIJ(thisNode);
			double wijnn = (cr + tp) / m;
			double nnaccept = wijnn / (wn + sumWij);

			Random rd = new Random();
			int value = rd.nextInt(100000);
			double check = value / 100000.0;
			if (check <= nnaccept) {
				return true;
			} else {
				return false;
			}
		}
	}

	// working:
	private int selectToPlay(int node) {
		// get neighbourhood
		boolean[] n = nodes.get(node).getNeighbours();
		double cr = nodes.get(node).getCredit();
		int nntpij = nodes.get(node).getNNTPIJ();
		int m = nodes.get(node).getMemorySpan();

		// set wn or use min weight
		double wn = (cr + nntpij) / m;

		if (wn <= 0) {
			wn = 0.05;
		}
		// make n-1 possible spaces to fill

		double[] prep = new double[nodes.size()];
		double wij = 0.0;
		double wijnn = 0.0;

		// sum wij and wijnn
		for (int i = 0; i < nodes.size(); i++) {
			if (n[i] == true && nodes.get(i).isNeighbour(node) == true && i != node) {
				double tpij = nodes.get(node).getTPIJ(i);
				wij += (cr + tpij) / m;
			}
			if ((n[i] == false || nodes.get(i).isNeighbour(node) == false) && i != node) {
				double tpij = nodes.get(node).getTPIJ(i);
				if ((cr + tpij) / m > 0) {
					wijnn += (cr + tpij) / m;
				} else {
					wijnn += 0.05;
				}
			}
		}

		wij = BigDecimal.valueOf(wij).setScale(3, RoundingMode.HALF_DOWN).doubleValue();
		wijnn = BigDecimal.valueOf(wijnn).setScale(3, RoundingMode.HALF_DOWN).doubleValue();
		int countN = 0;
		int countNN = 0;
		// calculate neighbours chance to get picked and place on array:
		for (int j = 0; j < nodes.size(); j++) {
			if ((n[j] == true || nodes.get(j).isNeighbour(node) == true) && j != node) {
				// calculate chance to get picked:
				double tpij = nodes.get(node).getTPIJ(j);
				double chance = (cr + tpij) / m;
				chance = BigDecimal.valueOf(chance).setScale(3, RoundingMode.HALF_DOWN).doubleValue();
				double value = chance / (wn + wij);
				value = BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_DOWN).doubleValue();
				prep[j] = value;
				countN++;
			}
			if ((n[j] == false || nodes.get(j).isNeighbour(node) == false) && j != node) {
				double tpij = nodes.get(node).getTPIJ(j);
				double chance = (cr + tpij) / m;
				chance = BigDecimal.valueOf(chance).setScale(3, RoundingMode.HALF_DOWN).doubleValue();
				// (wn / (wn + wij)) *
				double value = (chance / wijnn);
				value = BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_DOWN).doubleValue();
				prep[j] = value;
				countNN++;
			}
			if (j == node) {
				prep[j] = -1000;
			}
		}

		double[] roulette;
		int[] index;
		double[] rouletteNN;
		int[] indexNN;

		if (countN > 0) {
			roulette = new double[countN];
			index = new int[countN];
		} else {
			roulette = new double[1];
			index = new int[1];
		}
		if (countNN > 0) {
			rouletteNN = new double[countNN];
			indexNN = new int[countNN];
		} else {
			rouletteNN = new double[1];
			indexNN = new int[1];
		}

		// create roulette:
		if (countN > 0) {
			int counter = 0;
			for (int k = 0; k < nodes.size(); k++) {
				if (prep[k] != -1000 && n[k] == true) {
					if (counter > 0) {
						roulette[counter] = BigDecimal.valueOf(roulette[counter - 1] + prep[k])
								.setScale(3, RoundingMode.HALF_DOWN).doubleValue();
						index[counter] = k;
						counter++;
					} else {
						roulette[counter] = BigDecimal.valueOf(prep[k]).setScale(3, RoundingMode.HALF_DOWN)
								.doubleValue();
						index[counter] = k;
						counter++;
					}
				}
			}
		}

		if (countNN > 0) {
			int counter = 0;
			for (int k = 0; k < nodes.size(); k++) {
				if (prep[k] != -1000 && n[k] == false) {
					if (counter > 0) {
						rouletteNN[counter] = BigDecimal.valueOf(rouletteNN[counter - 1] + prep[k])
								.setScale(3, RoundingMode.HALF_DOWN).doubleValue();
						indexNN[counter] = k;
						counter++;
					} else {
						rouletteNN[counter] = BigDecimal.valueOf(prep[k]).setScale(3, RoundingMode.HALF_DOWN)
								.doubleValue();
						indexNN[counter] = k;
						counter++;
					}
				}
			}
		}
		if (countN > 0) {
			Random rd = new Random();
			int value = rd.nextInt(10000);
			double check = value / 10000.0;
			for (int i = 0; i < roulette.length; i++) {
				if (check <= roulette[i]) {
					return index[i];
				}
			}
		}

		if (countNN > 0) {
			Random rd = new Random();
			int value = rd.nextInt(10000);
			double check = value / 10000.0;
			for (int i = 0; i < rouletteNN.length; i++) {
				if (check <= rouletteNN[i]) {
					return indexNN[i];
				}
				if (i == rouletteNN.length - 1 && rouletteNN[i] - check <= 0) {
					return indexNN[i];
				}
			}
		}

		return -1;
	}
}
