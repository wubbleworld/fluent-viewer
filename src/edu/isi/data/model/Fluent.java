package edu.isi.data.model;

import static edu.isi.data.model.DataModel.TRUE;
import static edu.isi.data.model.DataModel.FALSE;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.xy.XYSeries;

public class Fluent {

	public String _name;
	
	public ArrayList<Integer> _start;
	public ArrayList<Integer> _end;
	public ArrayList<String> _values;
	
	protected int _maxTime;
	
	protected boolean _booleanBased;
	protected boolean[] _stream;

	public String _object1;
	public String _object2;
	
	protected String _type;

	public Fluent(String name, String object1, String object2) {
		_name = name;
		
		_start  = new ArrayList<Integer>();
		_end    = new ArrayList<Integer>();
		_values = new ArrayList<String>();
		
		_maxTime = 0;
		
		_object1 = object1;
		_object2 = object2;
		
		if ("".equals(object2))
			_type = DataModel.PROPERTY;
		else
			_type = DataModel.RELATION;
	}
	
	public String getName() {
		return _name;
	}
	
	public String getEntity1() {
		return _object1;
	}
	
	public String getEntity2() {
		return _object2;
	}
	
	public String getType() {
		return _type;
	}
	
	public boolean isActive(int time) {
		try {
		for (int i = 0; i < _start.size(); ++i) {
			int start = _start.get(i);
			int end = _end.get(i);
			
			boolean value = Boolean.parseBoolean(_values.get(i));
						
			if (time >= start && time <= end && value) {
				return true;
			}
			
			if (start > time)
				break;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void prepare(int time) {
		if (!_booleanBased)
			return;
		
		_stream = new boolean[time];
		for (int i = 0; i < _start.size(); ++i) {
			int start = _start.get(i);
			int end   = _end.get(i);
			boolean b  = Boolean.parseBoolean(_values.get(i));
		
			if (!b)
				continue;
			
			for (int j = start; j < end; ++j) {
				_stream[j] = b;
			}
		}
	}
	
	public boolean[] toStream(int startTime, int endTime) {
		boolean[] ret = new boolean[endTime-startTime];
		System.arraycopy(_stream, startTime, ret, 0, (endTime-startTime));
		return ret;
	}
	
	public String getKey() {
		return _name + " " + _object1 + " " + _object2;
	}
	
	public void add(int start, int end, String value) {
		_start.add(start);
		_end.add(end);
		
		_values.add(value);
		if (TRUE.equals(value) || FALSE.equals(value))
			_booleanBased = true;
		
		_maxTime = Math.max(end, _maxTime);
	}
	
	public String getValue(int time) {
		for (int i = 0; i < _start.size(); ++i) {
			int start = _start.get(i);
			int end   = _end.get(i);
			String s  = _values.get(i);
			
			if (start <= time && end > time) {
				return s;
			}
		}
		return "";
	}
	
	/**
	 * only can be called for those predicates that are
	 * boolean in value
	 * @param row
	 * @param time
	 * @return
	 */
	public Color getStatus(int row, int time) {
		
		for (int i = 0; i < _start.size(); ++i) {
			int start = _start.get(i);
			int end = _end.get(i);
	
			boolean value = Boolean.parseBoolean(_values.get(i));
			
			if (time >= start && time <= end && value) {
				if (row % 2 == 0)
					return Color.blue;
				else 
					return Color.red;
			}
	
			if (start > time)
				break;
		}
		return Color.white;
	}
	
	public XYSeries createSeries() {
		XYSeries series = new XYSeries(getKey());
		
		Iterator<Integer> startIter  = _start.iterator(); 
		Iterator<Integer> endIter    = _end.iterator(); 
		Iterator<String>  valuesIter = _values.iterator(); 
		
		while (startIter.hasNext()) {
			int start = startIter.next();
			int end = endIter.next();
			
			String value = valuesIter.next();
			
			if (TRUE.equals(value)) {
				series.add(start, 1.0);
				series.add(end, 1.0);
			} else if (FALSE.equals(value)) {
				series.add(start, 0.0);
				series.add(end, 0.0);
			} else {
				try {
					double d = Double.parseDouble(value);
					series.add(start, d);
					series.add(end, d);
				} catch (Exception e) {
					break;
				}
			}
		}
		return series;
	}
}