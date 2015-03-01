package edu.isi.data.graphics.filter;

import javax.swing.JPanel;

import edu.isi.data.model.DataModel;

public abstract class BaseFilterPanel extends JPanel {

	protected CompositeFilterPanel _parent;
	protected String _name;
	
	public BaseFilterPanel(CompositeFilterPanel parent) {
		_parent = parent;
	}
	
	public String getName() {
		return _name;
	}
	
	public abstract void update(DataModel model);
}
