package edu.isi.data.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataModel {
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	public static final String PROPERTY = "Property";
	public static final String RELATION = "Relation";
	
	protected long _startTime;
	protected long _endTime;
	
	protected int _startLogicalTime;
	protected int _endLogicalTime;
	
	protected int _sessionId;
	
	protected TreeSet<String> _entitySet;
	protected TreeSet<String> _fluentSet;
	
	protected ArrayList<Fluent> _fluents;
	protected ArrayList<Fluent> _fluentsPred;
	protected HashMap<String,Fluent> _fluentsMap;

	protected long _setMinTime;
	protected long _setMaxTime;
	protected boolean _setTime;
	protected TreeSet<String> _fluentIgnore;
	protected TreeSet<String> _entityIgnore;
	
	
	public DataModel() {
		_setTime = false;
		_setMinTime = 0;
		_setMaxTime = 0;
		
		_entitySet = new TreeSet<String>();
		_fluentSet = new TreeSet<String>();
	
		_entityIgnore = new TreeSet<String>();
		_fluentIgnore = new TreeSet<String>();
	}
	
	public void setSession(int sessionId) {
		_sessionId = sessionId;
	}
	
	public void ignore(String type, String name) {
		if ("Entity".equals(type)) {
			System.out.println("ignoring: " + name);
			_entityIgnore.add(name);
		}
		else
			_fluentIgnore.add(("fluent_" + name).toUpperCase());
	}
	
	public void clearIgnored() {
		System.out.println("cleared");
		_fluentIgnore.clear();
		_entityIgnore.clear();
	}
	
	public void setExplicitTime(int start, int end) {
		_setTime = true;
		_setMinTime = start;
		_setMaxTime = end;
	}
	
	public void completeRefresh() {
		_setTime = false;
		refresh();
	}
	
	public void refresh() {
		_startLogicalTime = Integer.MAX_VALUE;
		_endLogicalTime = Integer.MIN_VALUE;
		
		refreshSession();

		_fluents = new ArrayList<Fluent>();
		_fluentsPred = new ArrayList<Fluent>();
		_fluentsMap = new HashMap<String,Fluent>();
		
		long start = System.currentTimeMillis();
		try {
			ResultSet rs = DatabaseManager.inst().getTables("fluent%");
			while (rs.next()) {
				String table = rs.getString(3);
				if (_fluentIgnore.contains(table))
					continue;
					
				System.out.println("Name: " + rs.getString(3));
				StringBuffer buf = new StringBuffer();
				buf.append(
					"select l.name as name, e1.name as entity_1, e2.name as entity_2, value, " +
					"start_logical_time, end_logical_time " +
					"from " + rs.getString(3) + " f, lookup_fluent_table l, entity_table e1, entity_table e2 " +
					"where f.fluent_id = l.id and f.entity_1 = e1.id and f.entity_2 = e2.id and " +
					"f.session_id = " + _sessionId + " ");

				if (_setTime) {
					buf.append("and (start_logical_time < " + _setMaxTime + " and end_logical_time >= " + _setMinTime + ")");
				}
				
				if (_entityIgnore.size() > 0) {
					StringBuffer e1 = new StringBuffer(" and e1.name not in (");
					StringBuffer e2 = new StringBuffer(" and e2.name not in (");
						
					int count = 0;
					for (String entity : _entityIgnore) {
						if (count > 0) {
							e1.append(",");
							e2.append(",");
						}
						e1.append("'" + entity + "'");
						e2.append("'" + entity + "'");
						++count;
					}
					e1.append(")");
					e2.append(")");
					
					buf.append(e1);
					buf.append(e2);
				}
				refreshData(buf.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("...completed load [" + (System.currentTimeMillis() - start) + "]");

		for (Fluent f : _fluentsPred) 
			f.prepare(_endLogicalTime);
	}
	
	public void refreshSession() {
		_entitySet.clear();
		
		_startLogicalTime = 0;
		_endLogicalTime = getMax();
	}
	
	protected int getMax() {
		int max = 0;
		try {
			Statement s = DatabaseManager.inst().getStatement();
			String sql = "select end_logical_time  from user_sessions " + 
					"where session_id = " + _sessionId;

			ResultSet rs = s.executeQuery(sql);
			if (rs.next()) {
				max = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return max;
	}
	
	public void refreshData(String sql) {
		try {
			Statement s = DatabaseManager.inst().getStatement();
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				String object1 = rs.getString("entity_1");
				String object2 = rs.getString("entity_2");
				String value = rs.getString("value");

				_fluentSet.add(name);
				_entitySet.add(object1);
				if (!"".equals(object2)) {
					_entitySet.add(object2);
				}
				
				Fluent f = _fluentsMap.get(name + " " + object1 + " " + object2);
				if (f == null) {
					f = new Fluent(name, object1, object2);
					_fluentsMap.put(f.getKey(), f);
					
					if (TRUE.equals(value) || FALSE.equals(value)) 
						_fluentsPred.add(f);
					else
						_fluents.add(f);
				}

				int start = rs.getInt("start_logical_time");
				int end = rs.getInt("end_logical_time");

				_startLogicalTime = Math.min(start, _startLogicalTime);
				_endLogicalTime = Math.max(end, _endLogicalTime);

				if (_setTime) {
					start = (int) Math.max(start, _setMinTime);
					end = (int) Math.min(end, _setMaxTime);
				}
				f.add(start, end, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void swap(int i1, int i2) {
		Fluent tmp = _fluentsPred.get(i1);
		_fluentsPred.set(i1, _fluentsPred.get(i2));
		_fluentsPred.set(i2, tmp);
	}

	public long getStartTime() {
		return _startTime;
	}

	public long getEndTime() {
		return _endTime;
	}
	
	public int getStartLogicalTime() {
		return _startLogicalTime;
	}

	public int getEndLogicalTime() {
		return _endLogicalTime;
	}

	public int getSessionId() {
		return _sessionId;
	}
	
	public TreeSet<String> getUniqueEntity() {
		return _entitySet;
	}
	
	public TreeSet<String> getUniqueFluents() {
		return _fluentSet;
	}
	
	public TreeSet<String> getIgnoredFluents() {
		return _fluentIgnore;
	}

	public ArrayList<Fluent> getFluents() {
		return _fluents;
	}
	
	public ArrayList<Fluent> getFluentPredicates() {
		return _fluentsPred;
	}

	public void getSubset(int time, TreeSet<String> nodes, ArrayList<Fluent> fluents) {
		for (Fluent f : _fluentsPred) {
			if (f.isActive(time)) {
				nodes.add(f.getEntity1());
				if (!"".equals(f.getEntity2()))
					nodes.add(f.getEntity2());
				fluents.add(f);
			}
		}
	}
	
	public void setStartLogicalTime(int logicalTime) {
		_startLogicalTime = logicalTime;
	}

	public void setEndLogicalTime(int logicalTime) {
		_endLogicalTime = logicalTime;
	}
	
	public Fluent getFluent(String key) {
		return _fluentsMap.get(key);
	}
	
	public String[] getAllFluents() {
		String[] all = new String[_fluents.size() + _fluentsPred.size()];
		int count = 0;
		for (count = 0; count < _fluents.size(); ++count) {
			all[count] = _fluents.get(count).getKey(); 
		}
		for (int i = 0; i < _fluentsPred.size(); ++i) {
			all[count+i] = _fluentsPred.get(i).getKey();
		}
		return all;
	}
}
