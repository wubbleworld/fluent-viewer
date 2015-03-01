package edu.isi.data.graphics.filter;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Comparator;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import edu.isi.data.DataVisualization;
import edu.isi.data.graphics.DataComponent;
import edu.isi.data.graphics.GBC;
import edu.isi.data.model.DataModel;

public class CompositeFilterPanel extends JPanel implements DataComponent {

	private JTabbedPane _tabbedPane;
	
	protected TimeFilterPanel _time;
	
	protected NamedFilterPanel _entity;
	protected NamedFilterPanel _fluent;
	
	protected JButton _updateButton;
	protected TreeSet<BaseFilterPanel> _modifiedPanels;
	
	protected DataModel _model;
	
	public CompositeFilterPanel() {
		Comparator<BaseFilterPanel> comp = new Comparator<BaseFilterPanel>() {
			public int compare(BaseFilterPanel arg0, BaseFilterPanel arg1) {
				return arg0.getName().compareTo(arg1.getName());
			}
		};
		_modifiedPanels = new TreeSet<BaseFilterPanel>(comp);
		
		DataVisualization.inst().add(this);

		addComponents();
		addListeners();
	}
	
	private void addComponents() {
		setLayout(new GridBagLayout());
		
		_tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		add(_tabbedPane, GBC.makeGBC(0,0,BOTH,1,1));
		
		_time = new TimeFilterPanel(this);
		_tabbedPane.add("Time", _time);
		
		_entity = new NamedFilterPanel("Entity", this);
		_tabbedPane.add("Entity", _entity);
		
		_fluent = new NamedFilterPanel("Fluent", this);
		_tabbedPane.add("Fluent", _fluent);
		
		_updateButton = new JButton("Apply Filter");
		_updateButton.setPreferredSize(new Dimension(100,20));
		
		JPanel panel = new JPanel();
		panel.add(_updateButton);
		add(panel, GBC.makeGBC(0,1,BOTH,1,0));
	}
	
	private void addListeners() {
		_updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (_modifiedPanels.size() == 0 || _model == null) 
					return;
				
				_model.clearIgnored();
				
				_entity.update(_model);
				_fluent.update(_model);
				_time.update(_model);

				DataVisualization.inst().refreshModel(_time.getStart(), _time.getEnd());
			}
		});
	}
	
	public void modified(BaseFilterPanel panel) {
		if (_modifiedPanels.size() == 0) {
			// enable the update button
		}
		_modifiedPanels.add(panel);
	}

	public void refreshData(DataModel dm) {
		_model = dm;
		
		_time.setTimes(dm.getStartLogicalTime(), dm.getEndLogicalTime());
		
		_entity.setData(dm.getUniqueEntity());
		_fluent.setData(dm.getUniqueFluents());
	}

	public void changeTime(int time) {

	}
}
