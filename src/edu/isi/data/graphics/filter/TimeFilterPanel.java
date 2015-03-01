package edu.isi.data.graphics.filter;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.isi.data.graphics.GBC;
import edu.isi.data.model.DataModel;

public class TimeFilterPanel extends BaseFilterPanel {

	// could use a JFormattedTextField to bound the choices for 
	// min and max, but that seems like overkill for an internal project
	protected JTextField _startField;
	protected JTextField _endField;
	
	protected boolean _modified;
	
	public TimeFilterPanel(CompositeFilterPanel parent) {
		super(parent);
		_name = getClass().getCanonicalName();
		
		addComponents();
		addListeners();
	}
	
	private void addComponents() {
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Time"), 
				BorderFactory.createEmptyBorder(5,5,5,5)));
		
		setLayout(new GridBagLayout());
		
		JLabel startLabel = new JLabel("Start Time:");
		startLabel.setPreferredSize(new Dimension(80,20));
		add(startLabel, GBC.makeGBC(0,0,BOTH,0,0));
		
		JLabel endLabel = new JLabel("End Time:");
		endLabel.setPreferredSize(new Dimension(80,20));
		add(endLabel, GBC.makeGBC(0,1,BOTH,0,0));
		
		_startField = new JTextField("0");
		add(_startField, GBC.makeGBC(1,0,BOTH,1,0));
		
		_endField = new JTextField("0");
		add(_endField, GBC.makeGBC(1,1,BOTH,1,0));
		
		add(new JPanel(), GBC.makeGBC(0,2,2,1,BOTH,1,1));
	}
	
	private void addListeners() {
		final BaseFilterPanel tmp = this;
		KeyListener key = new KeyListener() {
			public void keyPressed(KeyEvent arg0) {}
			public void keyReleased(KeyEvent e) {}

			public void keyTyped(KeyEvent e) {
				_parent.modified(tmp);
				_modified = true;
			}
		};
		_startField.addKeyListener(key);
		_endField.addKeyListener(key);
	}
	
	public void setTimes(int start, int end) {
		_startField.setText(start+"");
		_endField.setText(end+"");
	}
	
	public int getStart() {
		return Integer.parseInt(_startField.getText());
	}
	
	public int getEnd() {
		return Integer.parseInt(_endField.getText());
	}
	
	public void update(DataModel model) {
		_modified = false;
		int start = 0;
		if (!"".equals(_startField.getText()))
			start = Integer.parseInt(_startField.getText());
		
		int end = 0;
		if (!"".equals(_endField.getText())) 
			end = Integer.parseInt(_endField.getText());

		model.setExplicitTime(start, end);
	}
}
