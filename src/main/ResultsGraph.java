package main;

import java.util.ArrayList;

import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraphView;

public class ResultsGraph {
	ArrayList<Nodes> nodes;


	private boolean[][] matrix;
	private JGraphXAdapter<Integer, DefaultEdge> jgxAdapter;
	private Graph<Integer, DefaultEdge> g;
	private ArrayList<Object> vertices;
	private ArrayList<Object> edges;
	private Object parent;
	private NMatrix gm;
	private mxGraphComponent mxGraphComp;
	private mxCircleLayout layout;
	
	public ResultsGraph(NMatrix nm, ArrayList<Nodes> nd) {
		this.nodes = nd;
		gm = nm;
		matrix = gm.getMatrix();
	}

	// reverse the data saved in model and construct new graph:
	private void modelToStructure() {
		// get matrix
		// initialize arraylists:
		vertices = new ArrayList<>();
		edges = new ArrayList<>();
		// set parent:
		parent = jgxAdapter.getDefaultParent();
		// construct nodes:
		for(int i = 0; i < gm.n; i++) {
			vertices.add(jgxAdapter.insertVertex(parent, null, Integer.toString(nodes.get(i).getTotalScore()) + " Vicoop: "+ Double.toString(nodes.get(i).getViCoop()) + " ID: " + Integer.toString(i), 20, 20, 80, 30,
					"strokeColor=black;fillColor=#ffb3ff"));
		}
		// get weights matrix and construct vertices between nodes, o < k ensures that
		// edge is made just once
		for (int o = 0; o < gm.n; o++) {
			for (int k = 0; k < gm.n; k++) {
				if (matrix[o][k] == true && matrix[k][o] == true) {
					edges.add(jgxAdapter.insertEdge(parent, null, Integer.toString(o) + " : " + Integer.toString(k),
							vertices.get(o), vertices.get(k), "startArrow=none;endArrow=none;strokeWidth=2;strokeColor=blue"));
				}
				if (matrix[o][k] == true && matrix[k][o] == false) {
					edges.add(jgxAdapter.insertEdge(parent, null, Integer.toString(o) + " : " + Integer.toString(k),
							vertices.get(o), vertices.get(k), "startArrow=none;endArrow=none;strokeWidth=2;strokeColor=yellow"));
				}
			}

		}
	}

	public mxGraphComponent drawGraph() {
		g = new SimpleGraph<>(DefaultEdge.class);
		jgxAdapter = new JGraphXAdapter<>(g);
		mxGraphComp = new mxGraphComponent(jgxAdapter);

		jgxAdapter.getModel().beginUpdate();
		try { // create a JGraphT graph
			modelToStructure(); // positioning via jgraphx layouts
			layout = new mxCircleLayout(jgxAdapter);
			layout.execute(jgxAdapter.getDefaultParent());
			mxGraphView view = jgxAdapter.getView();
			view.setScale(0.5);
			
			jgxAdapter.setCellsEditable(false);

		} finally {
			jgxAdapter.getModel().endUpdate();
		}

		return mxGraphComp;
	}

}
