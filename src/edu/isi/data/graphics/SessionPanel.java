
package edu.isi.data.graphics;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import edu.isi.data.DataVisualization;
import edu.isi.data.model.DataModel;
import edu.isi.data.model.DatabaseManager;

public class SessionPanel extends JPanel implements DataComponent {

	private static final long serialVersionUID = 1L;

	private SimpleDateFormat _dateFormat;
	
	private JTree            _tree;
	private DefaultTreeModel _treeModel;
	
	public SessionPanel() {
		super();
		
		_dateFormat = new SimpleDateFormat("MM/dd/yyyy [HH:mm:ss:SSSS]");

		DataVisualization.inst().add(this);
		
		addComponents();
		addListeners();

		repopulate();
	}
	
	private void addComponents() {
		setLayout(new GridBagLayout());

		_tree = new JTree();
		
		JScrollPane scroll = new JScrollPane(_tree);
		scroll.setPreferredSize(new Dimension(400,768));
		scroll.getViewport().setPreferredSize(new Dimension(1600,1200)); 
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		add(scroll, GBC.makeGBC(0, 0, BOTH, 1, 1.0));
	}
	
	private void addListeners() {
	    _tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    
	    _tree.addTreeSelectionListener(new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e) {
		    	DefaultMutableTreeNode node = (DefaultMutableTreeNode) _tree.getLastSelectedPathComponent();
		    	
		    	if (node == null)
		    		return;

		    	try {
		    		Integer sessionId = (Integer) node.getUserObject();
		    		DataVisualization.inst().setSessionId(sessionId);
		    	} catch (Exception ex) { }
		    }
	    });
		
	}
	
	public void repopulate() {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Sessions");
		try {
			Statement s = DatabaseManager.inst().getStatement();
			String sql = "select session_id, start_time, end_time from user_sessions";
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				int session = rs.getInt("session_id");
				String time = rs.getString("start_time");
				long startTime = 0;
				if (time != null) {
					double tmp = Double.parseDouble(time);
					startTime = (long) tmp;
				}
				
				time = rs.getString("end_time");
				long endTime = 0;
				if (time != null) {
					double tmp = Double.parseDouble(time);
					endTime = (long) tmp;
				}
				
				System.out.println("Start: " + startTime + " End: " + endTime);
				
				GregorianCalendar start = new GregorianCalendar();
				start.setTimeInMillis(startTime);
				
				GregorianCalendar end = new GregorianCalendar();
				end.setTimeInMillis(endTime);
				
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(session);
				node.add(new DefaultMutableTreeNode("Start: " + _dateFormat.format(start.getTime())));
				node.add(new DefaultMutableTreeNode("End: " + _dateFormat.format(end.getTime())));
				
				rootNode.add(node);
			}
			rs.close();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		_treeModel = new DefaultTreeModel(rootNode);
		_tree.setModel(_treeModel);
	}

	public void refreshData(DataModel dm) {
		// TODO Auto-generated method stub
		
	}

	public void changeTime(int time) {
		// TODO Auto-generated method stub
		
	}
}
