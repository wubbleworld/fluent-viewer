package edu.isi.data.graphics.view;

import static java.awt.GridBagConstraints.BOTH;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTable;

import edu.isi.data.DataVisualization;
import edu.isi.data.graphics.DataComponent;
import edu.isi.data.graphics.GBC;
import edu.isi.data.model.DataModel;
import edu.isi.data.model.Fluent;

public class TimelineView extends JPanel implements DataComponent {

	private static final long serialVersionUID = 1L;
	
	protected String _type;
	protected JTable _table;
	
	protected int _offset;
	protected int _minTime;
	protected int _maxTime;

	public TimelineView(String type) {
		_type = type;
		
		DataVisualization.inst().add(getClass().getName()+type, this);
		setLayout(new GridBagLayout());
	}

	public void refreshData(DataModel dm) {
		TableModel tm = new MyTableModel(dm);

		_table = new JTable(tm);
		_table.setShowGrid(true);
        _table.setDefaultRenderer(Color.class, new ColorRenderer(true));
        _table.setGridColor(Color.black);
        _table.setPreferredScrollableViewportSize(new Dimension(5000, 7000));
        setupSelection();
        //addMouseListener();
		
		for (int i = 0; i < _table.getColumnCount(); ++i) {
			TableColumn column = _table.getColumnModel().getColumn(i);
			switch (i) {
			case 0:
				column.setPreferredWidth(175);
				break;
			case 1:
			case 2:
				break;
			default:
				column.setPreferredWidth(20);
				break;	
			}
		}
		
		removeAll();
		add(_table, GBC.makeGBC(0,1,BOTH,1,1));
		add(_table.getTableHeader(), GBC.makeGBC(0,0,BOTH,1,0));

		validate();
	}
	
	protected void setupSelection() {
		_table.setRowSelectionAllowed(true);
		_table.setColumnSelectionAllowed(true);

		JTableHeader header = _table.getTableHeader();
		header.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				TableColumnModel model = _table.getColumnModel();
				int viewColumn = model.getColumnIndexAtX(e.getX());
				int column = _table.convertColumnIndexToModel(viewColumn);
				if (e.getClickCount() == 1 && column >= _offset) {
					DataVisualization.inst().setTime(column-_offset+_minTime);
				}
			}
		});
	}
	
	class MyTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		private String[] _columnNames;
		private int _deltaTime;

		private ArrayList<? extends Fluent>       _rawData;

		public MyTableModel(DataModel dm) {
			_minTime = dm.getStartLogicalTime();
			_maxTime = dm.getEndLogicalTime();
			
			_deltaTime = _maxTime - _minTime;
			
			if (DataModel.PROPERTY.equals(_type)) {
//				_rawData = dm.getPropertyPredicates();
				_columnNames = new String[] { "Name", "Entity"};
				_offset = 2;
			} else {
//				_rawData = dm.getRelationPredicates();
				_columnNames = new String[] { "Name", "Entity 1", "Entity 2" };
				_offset = 3;
			}
			
		}

		public int getColumnCount() {
			return _deltaTime + _offset;
		}

		public int getRowCount() {
			return _rawData.size();
		}

		public String getColumnName(int col) {
			if (col < _offset)
				return _columnNames[col];
			return (col-_offset+_minTime) + "";
		}

		public Object getValueAt(int row, int col) {
			Fluent raw = _rawData.get(row);
			if (raw == null)
				return null;

			switch (col) {
			case 0:
				return raw.getName();
			case 1:
				return raw.getEntity1();
			case 2:
				if (DataModel.RELATION.equals(_type))
					return raw.getEntity2();
			}

			return raw.getStatus(row, (col-_offset+_minTime));
		}

		@SuppressWarnings("unchecked")
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) { return false; }
		public void setValueAt(Object value, int row, int col) { }
	}

	class ColorRenderer extends JLabel implements TableCellRenderer {
		Border unselectedBorder = null;
		Border selectedBorder = null;
		boolean isBordered = true;

		public ColorRenderer(boolean isBordered) {
			this.isBordered = isBordered;
			setOpaque(true); //MUST do this for background to show up.
		}

		public Component getTableCellRendererComponent(
				JTable table, Object color,
				boolean isSelected, boolean hasFocus,
				int row, int column) {
			Color newColor = (Color)color;
			setBackground(newColor);
			if (isBordered) {
				if (isSelected) {
					if (selectedBorder == null) {
						selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
								table.getSelectionBackground());
					}
					setBorder(selectedBorder);
				} else {
					if (unselectedBorder == null) {
						unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,
								table.getBackground());
					}
					setBorder(unselectedBorder);
				}
			}
			return this;
		}
	}

	public void changeTime(int time) {
		int column = time - _minTime + _offset;
		_table.setColumnSelectionInterval(column,column);
		
		if (_table.getRowCount() == 0) 
			return;
		
		_table.setRowSelectionInterval(0, _table.getRowCount()-1);
	}
}
