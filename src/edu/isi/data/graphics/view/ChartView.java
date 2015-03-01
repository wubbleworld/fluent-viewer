package edu.isi.data.graphics.view;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.isi.data.DataVisualization;
import edu.isi.data.graphics.DataComponent;
import edu.isi.data.graphics.GBC;
import edu.isi.data.model.DataModel;
import edu.isi.data.model.Fluent;

public class ChartView extends JPanel implements DataComponent {

	protected JList            _list;
	protected DefaultListModel _listModel;
	
	protected JButton    _fluentButton;
	protected JButton    _removeButton;
	
	protected ChartPanel              _chartPanel;
	protected JFreeChart              _chart;
	protected XYSeriesCollection      _dataset;
	protected TreeMap<String,Integer> _seriesMap;
	
	protected DataModel  _model;
	
	public ChartView() {
		DataVisualization.inst().add(this);
		
		_seriesMap = new TreeMap<String,Integer>();
		
		addComponents();
		addListeners();
	}
	
	protected void addComponents() {
		setLayout(new GridBagLayout());
		
		JPanel left = new JPanel();
		left.setLayout(new GridBagLayout());
		
		_listModel = new DefaultListModel();
		_list = new JList(_listModel);
		left.add(_list, GBC.makeGBC(0,1,BOTH,1,1));
		
		JPanel buttonPanel = new JPanel();
		
		_fluentButton = new JButton("Add Fluent");
		buttonPanel.add(_fluentButton, GBC.makeGBC(0,0,BOTH,0,0));
		
		left.add(buttonPanel, GBC.makeGBC(0,0,BOTH,1,0));

		_removeButton = new JButton("Remove");
		left.add(_removeButton, GBC.makeGBC(0,2,BOTH,0,0));
		
		add(left, GBC.makeGBC(0,0, BOTH, 0, 1));
		
		_dataset = new XYSeriesCollection();
		_chart = ChartFactory.createXYLineChart(
				"Timeline Data",
				"Time",
				"Values",
				_dataset,
				PlotOrientation.VERTICAL,
				true,
				false,
				false);
		
		_chartPanel = new ChartPanel(_chart);
		add(_chartPanel, GBC.makeGBC(1,0,BOTH,1,1));
	
	}
	
	protected void addListeners() {
		_fluentButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				AddDialog dialog = new AddDialog(null, _model);
				if (dialog.okPressed()) {
					Fluent f = _model.getFluent(dialog.getKey());
					if (f == null) {
						System.err.println("Unkown relation: " + dialog.getKey()); 
						return;
					}
					_listModel.addElement(f.getKey());

					_seriesMap.put(f.getKey(), _dataset.getSeriesCount());
					_dataset.addSeries(f.createSeries());
					_chart.fireChartChanged();
				}
			}
		});
		
		_removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { 
				if (_list.getSelectedIndex() != -1) {
					String key = (String) _list.getSelectedValue();
					_listModel.remove(_list.getSelectedIndex());
					
					int index = _seriesMap.remove(key);
					_dataset.removeSeries(index);
					_chart.fireChartChanged();
				}
			}
		});
	}

	public void changeTime(int time) {
		// TODO Auto-generated method stub
		
	}

	public void refreshData(DataModel dm) {
		_model = dm;
	}
}

class AddDialog extends JDialog {
	
	protected boolean _okPressed;
	
	protected JComboBox _fluentBox;
	
	protected JButton _okButton;
	protected JButton _cancelButton;

	public AddDialog(JFrame parent, DataModel dm) {
		super(parent, "Add Property/Relation", true);

		addComponents(dm);
		addListeners();
		
		setSize(220,180);
		setVisible(true);
	}
	
	private void addComponents(DataModel dm) {
		getContentPane().setLayout(new GridBagLayout());
		JLabel label;
		
		label = new JLabel("Fluent:");
		label.setPreferredSize(new Dimension(80,20));
		getContentPane().add(label, GBC.makeGBC(0,0,BOTH,0,0));
		
		_fluentBox = new JComboBox(dm.getAllFluents());
		getContentPane().add(_fluentBox, GBC.makeGBC(1,0,BOTH,1,0));
		
		JPanel panel = new JPanel();
		
		panel.setPreferredSize(new Dimension(120,40));
		_okButton     = new JButton("OK");
		_cancelButton = new JButton("Cancel");
		
		panel.add(_okButton);
		panel.add(_cancelButton);
		
		getContentPane().add(panel, GBC.makeGBC(0,1,2,1,BOTH,1,0));
	}
	
	private void addListeners() {
		final JDialog parent = this;
		
		_okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				_okPressed = true;
				setVisible(false);
				dispose();
			}
		});
		
		_cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				_okPressed = false;
				setVisible(false);
				dispose();
			}
		});
	}
	
	public boolean okPressed() {
		return _okPressed;
	}
	
	public String getKey() {
		return (String) _fluentBox.getSelectedItem();
	}
}
