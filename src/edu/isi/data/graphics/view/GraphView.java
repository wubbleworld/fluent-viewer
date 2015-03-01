package edu.isi.data.graphics.view;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.GridBagLayout;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JPanel;

import edu.isi.data.DataVisualization;
import edu.isi.data.graphics.DataComponent;
import edu.isi.data.graphics.GBC;
import edu.isi.data.model.DataModel;
import edu.isi.data.model.DatabaseManager;
import edu.isi.data.model.Fluent;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.BalloonTreeLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.layout.graph.FruchtermanReingoldLayout;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

public class GraphView extends JPanel implements DataComponent {

	private static final long serialVersionUID = 1L;

	protected DataModel _dataModel;
	protected Graph _graph;
	
	public GraphView() {
		setLayout(new GridBagLayout());
		
		//DataVisualization.inst().add(this);
	}
	
	public void changeTime(int logicalTime) {
		if (_dataModel == null)
			return;
		
		removeAll();
		
		Visualization vis = new Visualization();

		LabelRenderer r = new LabelRenderer("name");
		r.setRoundedCorner(8, 8); // round the corners
		
		EdgeRenderer er = new EdgeRenderer(Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_FORWARD);
		

		// create a new default renderer factory
		// return our name label renderer as the default for all non-EdgeItems
		// includes straight line edges for EdgeItems by default
		vis.setRendererFactory(new DefaultRendererFactory(r, er));	
		
		_graph = new Graph(true);
		Table t = _graph.getNodeTable();
		t.addColumn("name", String.class);
		t.addColumn("type", String.class);
		t.addColumn("obj1", String.class);
		t.addColumn("obj2", String.class);

		buildGraph(logicalTime);
		vis.add("graph", _graph);
		
		// create our nominal color palette
		// pink for females, baby blue for males
		int[] palette = new int[] {
				ColorLib.rgb(255, 117, 119), ColorLib.rgb(143, 143, 245), ColorLib.rgb(100, 255, 100)
		};
		// map nominal data values to colors using our provided palette
		DataColorAction fill = new DataColorAction("graph.nodes", "type",
		    Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
		// use black for node text
		ColorAction text = new ColorAction("graph.nodes",
		    VisualItem.TEXTCOLOR, ColorLib.gray(0));
		// use light grey for edges
		ColorAction edges = new ColorAction("graph.edges",
		    VisualItem.STROKECOLOR, ColorLib.gray(0));
		ColorAction edgesFill = new ColorAction("graph.edges",
				VisualItem.FILLCOLOR, ColorLib.gray(0));
		
		// create an action list containing all color assignments
		ActionList color = new ActionList();
		color.add(fill);
		color.add(text);
		color.add(edges);		
		color.add(edgesFill);
		
		ActionList layout = new ActionList();//Activity.INFINITY);
		layout.add(new FruchtermanReingoldLayout("graph"));
//		layout.add(new BalloonTreeLayout("graph"));
//		layout.add(new RadialTreeLayout("graph"));
//		layout.add(new ForceDirectedLayout("graph"));
		layout.add(new RepaintAction());
		
		vis.putAction("layout", layout);
		vis.putAction("color", color);
		
		Display display = new Display(vis);
		display.setSize(720, 500); // set display size
		display.addControlListener(new DragControl()); // drag items around
		display.addControlListener(new PanControl());  // pan with background left-drag
		display.addControlListener(new ZoomControl()); // zoom with vertical right-drag		
		
		vis.run("color");  // assign the colors
		vis.run("layout"); // start up the animated layout		

		add(display, GBC.makeGBC(0,0,BOTH,1,1));
		validate();
	}

	public void buildGraph(int logicalTime) {
		TreeSet<String> nodeSet = new TreeSet<String>();
		ArrayList<Fluent> fluentSet = new ArrayList<Fluent>();

		long start = System.currentTimeMillis();
		_dataModel.getSubset(logicalTime, nodeSet, fluentSet);
		
		HashMap<String,Node> tempMap = new HashMap<String,Node>();
		for (String obj : nodeSet) {
			Node n = _graph.addNode();
			try {
				n.set("name", obj);
				n.set("type", "entity");
			} catch (Exception e) {
				e.printStackTrace();
			}
			tempMap.put(obj, n);
		}
		
		for (Fluent f : fluentSet) {
			Node n1 = tempMap.get(f.getEntity1());
			Node n2 = tempMap.get(f.getEntity2());
			
			Node e = _graph.addNode();
			e.setString("name", f.getName());
			e.setString("obj1", f.getEntity1());
			e.setString("obj2", f.getEntity2());
			e.setString("type", f.getType());
			
			_graph.addEdge(n1, e);

			if (n2 != null) {
				_graph.addEdge(e, n2);
			}
		}
		//addEdges(logicalTime, tempMap);
	}
	
//	protected void addEdges(int logicalTime, HashMap<String,Node> tempMap) {
//		System.out.println("addEdges");
//		try {
//			int sessionId = DatabaseManager.inst().getActiveSession();
//			Statement s = DatabaseManager.inst().getStatement();
//			String sql = "select fluent_name, object_name1, object_name2 from fluent where session_id = " + sessionId +
//					" and start_logical_time <= " + logicalTime + " and end_logical_time >= " + logicalTime;
//			
//			ResultSet rs = s.executeQuery(sql);
//			
//			while (rs.next()) {
//				System.out.println("Edge: " + rs.getString(1));
//				String fluent = rs.getString(1);
//				String object1 = rs.getString(2);
//				String object2 = rs.getString(3);
//				if ("null".equals(object2)) {
//					object2 = object1;
//				}
//				Node n1 = tempMap.get(object1);
//				Node n2 = tempMap.get(object2);
//				
//				Node e = _graph.addNode();
//				e.setString("name", fluent);
//				e.setString("type", "relationship");
//				e.setString("obj1", object1);
//				e.setString("obj2", object2);
//				
//				_graph.addEdge(n1, e);
//				_graph.addEdge(e, n2);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	public void refreshData(DataModel dm) {
		_dataModel = dm;
	}
}
