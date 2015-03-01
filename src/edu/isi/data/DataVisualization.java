package edu.isi.data;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import edu.isi.data.graphics.DataComponent;
import edu.isi.data.graphics.DataMenu;
import edu.isi.data.graphics.GBC;
import edu.isi.data.graphics.SessionPanel;
import edu.isi.data.graphics.filter.CompositeFilterPanel;
import edu.isi.data.graphics.view.ChartView;
import edu.isi.data.graphics.view.GraphView;
import edu.isi.data.graphics.view.TimelineImageView;
import edu.isi.data.graphics.view.TimelineView;
import edu.isi.data.graphics.view.ValueView;
import edu.isi.data.model.DataModel;

public class DataVisualization {

	private static DataVisualization _vis;
	
	protected DataModel _dataModel;
	protected JFrame _dataFrame;
	
	protected TreeMap<String,DataComponent> _listeners;
	
	private DataVisualization() {
		_listeners = new TreeMap<String,DataComponent>();
	}
	
	public static DataVisualization inst() {
		if (_vis == null) {
			_vis = new DataVisualization();
		}
		return _vis;
	}
	
	public void setTime(int time) {
		for (DataComponent comp : _listeners.values()) {
			try {
				comp.changeTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void refreshModel(int start, int end) {
		_dataModel.refresh();
		_dataModel.setStartLogicalTime(start);
		_dataModel.setEndLogicalTime(end);
		
		notifyVisual();
	}
	public void refreshModel() {
		_dataModel.refresh();
		notifyVisual();
	}
	
	public void notifyVisual() {
		for (Map.Entry<String,DataComponent> entry : _listeners.entrySet()) {
			//System.out.println(entry.getKey());
			try {
				entry.getValue().refreshData(_dataModel);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setTime(_dataModel.getStartLogicalTime());
	}
	
	public void setSessionId(int sessionId) {
		_dataModel.setSession(sessionId);
		_dataModel.completeRefresh();
		notifyVisual();
	}
	
	public void showDialog(int time) {
		JDialog d = new JDialog(_dataFrame, "Time: " + time, false);
		Container content = d.getContentPane();
		content.setLayout(new GridBagLayout());

		GraphView g = new GraphView();
		g.refreshData(_dataModel);
		g.changeTime(time);
		
		ValueView v = new ValueView();
		v.refreshData(_dataModel);
		v.changeTime(time);
		
		JTabbedPane pane = new JTabbedPane(JTabbedPane.BOTTOM);
		pane.setPreferredSize(new Dimension(600,600));
		pane.add("Graph", g);
		pane.add("Values", v);
		content.add(pane, GBC.makeGBC(0, 1, BOTH, 1, 1.0));	
		
		d.setSize(new Dimension(800,600));
		d.setVisible(true);
	}
	
	public void start() {
		_dataModel = new DataModel();
		_dataFrame = new JFrame("Data Visualization");
		_dataFrame.setJMenuBar(new DataMenu());
		_dataFrame.setSize(1024,768);
		//_dataFrame.setResizable(false);

		_dataFrame.getContentPane().setLayout(new GridBagLayout());
	
		Container content = _dataFrame.getContentPane();
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(200,768));
		panel.setLayout(new GridBagLayout());
		panel.add(new SessionPanel(), GBC.makeGBC(0, 0, BOTH, 1, 0.5));
		panel.add(new CompositeFilterPanel(), GBC.makeGBC(0,1, BOTH, 1, 0.5));
		content.add(panel, GBC.makeGBC(0,0,BOTH,0.1,1));
		
		JPanel thePanel = new JPanel();
		thePanel.setLayout(new GridBagLayout());
		
//		pane = new JTabbedPane(JTabbedPane.BOTTOM);
//		pane.setPreferredSize(new Dimension(600,600));
//		pane.add("Graph", new GraphView());
//		pane.add("Values", new ValueView());
//		thePanel.add(pane, GBC.makeGBC(0, 1, BOTH, 1, 1.0));
		
		JTabbedPane bigPane = new JTabbedPane(JTabbedPane.TOP);
		bigPane.add("Fluent Timeline", new TimelineImageView());
		bigPane.add("Chart View", new ChartView());
		
		content.add(bigPane, GBC.makeGBC(1,0,BOTH,1,1));

		_dataFrame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}
		});
		
		_dataFrame.setVisible(true);
	}
	
	public void add(DataComponent component) {
		_listeners.put(component.getClass().getName(), component);
	}
	
	public void add(String name, DataComponent component) {
		//System.out.println("Adding: " + name + " " + component);
		_listeners.put(name, component);
	}
	
	public DataComponent get(String name) {
		DataComponent dc = _listeners.get(name);
		if (dc == null) {
			System.err.println("[getComponent] unknown component: " + name);
		}
		return dc;
	}
	
	public static void main(String[] args) {
		DataVisualization.inst().start();
	}
}
