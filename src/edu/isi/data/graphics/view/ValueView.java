package edu.isi.data.graphics.view;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.TreeMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import edu.isi.data.graphics.DataComponent;
import edu.isi.data.graphics.GBC;
import edu.isi.data.model.DataModel;
import edu.isi.data.model.Fluent;

public class ValueView extends JPanel implements DataComponent {
	private static final long serialVersionUID = 1L;

	protected DataModel        _dataModel;
	protected JTree            _tree;
	
	public ValueView() {
	
		//DataVisualization.inst().add(this);
		
		addComponents();
		addListeners();
	}
	
	protected void addComponents() {
		setLayout(new GridBagLayout());
		
		_tree = new JTree();
		
		JScrollPane scroll = new JScrollPane(_tree);
		add(scroll, GBC.makeGBC(0,0,BOTH,1,1));
	}
	
	protected void addListeners() {
		
	}
	
	public void changeTime(int logicalTime) {
		if (_dataModel == null)
			return;
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Values");
		TreeMap<String,DefaultMutableTreeNode> map = new TreeMap<String,DefaultMutableTreeNode>();
		
		// first add all of the entity nodes to the tree
		for (String s : _dataModel.getUniqueEntity()) {
			DefaultMutableTreeNode entityNode = new DefaultMutableTreeNode(s);
			root.add(entityNode);
			map.put(s, entityNode);
		}
		
		
		TreeMap<String,DefaultMutableTreeNode> relMap = new TreeMap<String,DefaultMutableTreeNode>();
		// now move through all of the fluents and add the correct nodes
		for (Fluent p : _dataModel.getFluents()) {
			DefaultMutableTreeNode entityNode = map.get(p.getEntity1());
			if (entityNode == null) {
				System.err.println("Unknown: " + p.getEntity1());
				continue;
			}

			// if we are really dealing with a property then add prop : value 
			if ("".equals(p.getEntity2())) {
				entityNode.add(new DefaultMutableTreeNode(p.getName() + ": " + p.getValue(logicalTime)));
				continue;
			}

			// now move through all of the relations and add a node for each other entity
			// off of that node we add the relations and values to it
			DefaultMutableTreeNode entity2Node = relMap.get(p.getEntity1() + " " + p.getEntity2());
			if (entity2Node == null) {
				entity2Node = new DefaultMutableTreeNode(p.getEntity2());
				relMap.put(p.getEntity1() + " " + p.getEntity2(), entity2Node);
				entityNode.add(entity2Node);
			}
			
			entity2Node.add(new DefaultMutableTreeNode(p.getName() + ": " + p.getValue(logicalTime)));
		}
		
		_tree.setModel(new DefaultTreeModel(root));
		expandAll(_tree, true);
	}
	
    public void expandAll(JTree tree, boolean expand) {
        TreeNode root = (TreeNode)tree.getModel().getRoot();
    
        // Traverse tree from root
        expandAll(tree, new TreePath(root), expand);
    }
    
    @SuppressWarnings("unchecked")
	private void expandAll(JTree tree, TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
    
        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }	
	
	public void refreshData(DataModel dm) {
		_dataModel = dm;
	}
}
