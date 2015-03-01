package edu.isi.data.graphics.filter;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.isi.data.graphics.GBC;
import edu.isi.data.model.DataModel;

public class NamedFilterPanel extends BaseFilterPanel {

	protected JComboBox            _comboBox;
	protected DefaultComboBoxModel _model;
	
	protected JButton   _addButton;
	protected JButton   _removeButton;

	protected JList            _list;
	protected DefaultListModel _listModel;
	
	protected String _type;
	
	public NamedFilterPanel(String name, CompositeFilterPanel parent) {
		super(parent);
		
		_name = getClass().getCanonicalName() + name;
		_type = name;
		
		addComponents();
		addListeners();
	}
	
	protected void addComponents() {
		setLayout(new GridBagLayout());
		
		JLabel label = new JLabel("Name:");
		label.setPreferredSize(new Dimension(70,20));
		add(label, GBC.makeGBC(0,0,BOTH,0,0));
		
		_comboBox = new JComboBox();
		add(_comboBox, GBC.makeGBC(1,0,BOTH,1,0));
		
		_addButton = new JButton("Add");
		_addButton.setPreferredSize(new Dimension(80,20));
		add(_addButton, GBC.makeGBC(2,0,BOTH,0,0));
		
		_listModel = new DefaultListModel();
		_list = new JList(_listModel);
		 JScrollPane scrollPane = new JScrollPane(_list);
		add(scrollPane, GBC.makeGBC(0,1,3,1,BOTH,1,1));
		
		_removeButton = new JButton("Remove");
		_removeButton.setPreferredSize(new Dimension(80,20));
		
		JPanel panel = new JPanel();
		panel.add(_removeButton);
		add(panel, GBC.makeGBC(0,2,3,1,BOTH,1,0));
	}
	
	protected void addListeners() {
		final BaseFilterPanel tmp = this;
		
		_addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String item = (String) _model.getSelectedItem();
				if (item == null)
					return;
				
				_model.removeElement(item);
				_listModel.addElement(item);
				
				_parent.modified(tmp);
			}
		});
		
		_removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Object[] items = _list.getSelectedValues();
				for (int i = 0; i < items.length; ++i) {
					_model.addElement((String) items[i]);
					_listModel.removeElement(items[i]);

					_parent.modified(tmp);
				}
			}
		});
	}
	
	public void setData(TreeSet<String> set) {
		_model = new DefaultComboBoxModel(set.toArray());
		_comboBox.setModel(_model);
		
		for (String item : set) {
			if (_listModel.contains(item))
				_model.removeElement(item);
		}
	}
	
	/**
	 * user clicked update, so change the model accordingly.
	 * @param model
	 */
	public void update(DataModel model) {
		System.out.println("Update: " + _type + " " + _listModel.getSize());
		for (int i = 0; i < _listModel.getSize(); ++i) {
			model.ignore(_type, (String) _listModel.get(i));
		}
	}
}
