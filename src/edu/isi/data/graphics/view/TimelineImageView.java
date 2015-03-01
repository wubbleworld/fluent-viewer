package edu.isi.data.graphics.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;

import edu.isi.data.DataVisualization;
import edu.isi.data.graphics.DataComponent;
import edu.isi.data.model.DataModel;
import edu.isi.data.model.Fluent;

public class TimelineImageView extends JPanel implements DataComponent {
	private static final long serialVersionUID = 1L;

	public static final int TICK_WIDTH = 8;
	public static final int ROW_HEIGHT  = 12;
	
	public static final int X_OFFSET = 175;
	public static final int Y_OFFSET = 20;
	
	protected int _mouseX;
	protected int _mouseY;
	
	protected InnerPanel _panel;
	protected JScrollBar _horizontalBar;
	protected JScrollBar _verticalBar;
	
	protected DataModel  _model;
	
	protected int _currRow = 0;
	protected int _currCol = 0;
	
	// this represents the northernmost corner
	protected int _row = 0;
	protected int _startTime = 0;
	
	public TimelineImageView() {
		DataVisualization.inst().add(this);
		
		addComponents();
		addListeners();
	}
	
	protected void addComponents() {
		setLayout(new BorderLayout());
		
		_panel = new InnerPanel();
		_horizontalBar = new JScrollBar(JScrollBar.HORIZONTAL);
		_verticalBar = new JScrollBar(JScrollBar.VERTICAL);
		
		_horizontalBar.setUnitIncrement(1);
		_verticalBar.setUnitIncrement(1);
		
		add(_horizontalBar, BorderLayout.SOUTH);
		add(_verticalBar, BorderLayout.EAST);
		add(_panel, BorderLayout.CENTER);
	}
	
	protected void addListeners() {
		_horizontalBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				_startTime = e.getValue();
				repaint();
			}
		});
		
		_verticalBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				_row = e.getValue();
				repaint();
			}
		});
	}
	
	public void changeTime(int time) {
		// TODO Auto-generated method stub
	}
	
	private class InnerPanel extends JPanel implements MouseListener,MouseMotionListener {
		private JPopupMenu _popup;
		private JMenuItem _moveItem;
		
		private boolean _moving;
		private int _selectedMouseY;
		private int _selectedRow;
		
		public InnerPanel() {
			_selectedRow = -1;
			
			_popup = new JPopupMenu();
			_moveItem = new JMenuItem("Move");
			_popup.add(_moveItem);
			
			addListeners();
		}
		
		protected void addListeners() {
			final JPanel blah = this;
			_moveItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_moving = true;
					_selectedRow = ((_selectedMouseY - Y_OFFSET) / ROW_HEIGHT) + _row;
					blah.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				}
			});
			
			addMouseListener(this);
			addMouseMotionListener(this);
		}
		
		
		public void paintComponent(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics;
			g.setBackground(Color.white);
			g.setColor(Color.white);
			g.fillRect(0, 0, getWidth(), getHeight());
		
			int width = getWidth();
			int height = getHeight();
		
			// 200 is saved for the names of the fluents
			// 20 is saved for the time boundaries
			int imgWidth = width - X_OFFSET;
			int imgHeight = height - Y_OFFSET;
	
			if (_model == null || _model.getFluentPredicates() == null)
				return;
		
			int numRows = imgHeight / ROW_HEIGHT;
			int numTicks = imgWidth / TICK_WIDTH;
		
			numTicks = Math.min(numTicks, _model.getEndLogicalTime()-_startTime);
		
			g.setColor(Color.black);
			drawGrid(g, numRows, numTicks);

			Font font = g.getFont();
			g.setFont(font.deriveFont(9.0f));

			g.drawString("Time Interval: [" + _startTime+ " " + (_startTime+numTicks) + "]", 10, 14);
		
			ArrayList<Fluent> records = _model.getFluentPredicates();
			for (int i = 0; i < numRows && (i+_row) < records.size(); ++i) {
				Fluent f = records.get(i+_row);
				boolean[] values = f.toStream(_startTime, _startTime+numTicks);
			
				g.setColor(Color.black);
				g.drawString(f.getKey(), 0, transformY(i)+12);
			
				if ((i+_row) % 2 == 0) 
					g.setColor(Color.red);
				else 
					g.setColor(Color.blue);
			
				boolean on = false;
				int onTime = -1;
				for (int j = 0; j < values.length; ++j) {
					if (!on && values[j]) {
						// just turned on
						on = true;
						onTime = j;
						continue;
					}
				
					if (on && !values[j]) {
						// just turned off
						on = false;
						g.fillRect(transformX(onTime), transformY(i)+2, (j-onTime)*TICK_WIDTH, ROW_HEIGHT-4);
					}
				}
				if (on) {
					g.fillRect(transformX(onTime), transformY(i)+2, (values.length-onTime)*TICK_WIDTH, ROW_HEIGHT-4);
				}
			}
		}
	
		protected void drawGrid(Graphics2D g, int numRows, int numColumns) {
			int width = getWidth();
			int height = getHeight();

			g.setColor(Color.LIGHT_GRAY);
			if (_currRow-_row >= 0) // we are visible
				g.fillRect(0, transformY(_currRow-_row), width, ROW_HEIGHT);
			if (_currCol-_startTime >= 0) // we are visible
				g.fillRect(transformX(_currCol-_startTime), 0, TICK_WIDTH, height);
			
			g.setColor(Color.DARK_GRAY);
			for (int i = 0; i < numRows; ++i) {
				g.drawLine(0, transformY(i), width, transformY(i));
			}
		
			for (int i = 0; i < numColumns; ++i) {
				g.drawLine(transformX(i), 0, transformX(i), height);
			}
		}
	
		protected int transformY(int y) {
			return y * ROW_HEIGHT + Y_OFFSET;
		}
	
		protected int transformX(int x) {
			return x * TICK_WIDTH + X_OFFSET;
		}

		public void mouseClicked(MouseEvent arg0) {
			if (arg0.getY() < Y_OFFSET && arg0.getX() > X_OFFSET) {
				int tick = (arg0.getX() - X_OFFSET) / TICK_WIDTH;
				DataVisualization.inst().showDialog(_startTime + tick);
			}
			
			if (arg0.getY() > Y_OFFSET && arg0.getX() > X_OFFSET) {
				_currCol = (arg0.getX() - X_OFFSET) / TICK_WIDTH + _startTime;
				_currRow = (arg0.getY() - Y_OFFSET) / ROW_HEIGHT + _row;
				System.out.println(_currCol + " " + _currRow);
				repaint();
			}
			
			if (arg0.getY() > Y_OFFSET && arg0.getX() < X_OFFSET) {
				if (arg0.getButton() == MouseEvent.BUTTON3) {
					System.out.println("displaying popup");
					_popup.show(this, arg0.getX(), arg0.getY());
					_selectedMouseY = arg0.getY();
				}
			}

			if (_moving) {
				int currRow = ((arg0.getY() - Y_OFFSET) / ROW_HEIGHT) + _row;
				_model.swap(currRow, _selectedRow);
				_moving = false;
				
				setCursor(Cursor.getDefaultCursor());
				repaint();
			}
		}

		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		public void mousePressed(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				_mouseX = e.getX();
				_mouseY = e.getY();
			}
			
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseDragged(MouseEvent arg0) {

		}

		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public void refreshData(DataModel dm) {
		_model = dm;
		
		if (_model.getFluentPredicates() == null) 
			return;
		
		_horizontalBar.setMaximum(_model.getEndLogicalTime());
		_verticalBar.setMaximum(_model.getFluentPredicates().size());
		
		_horizontalBar.setValue(0);
		_verticalBar.setValue(0);
	
		repaint();
	}

}
