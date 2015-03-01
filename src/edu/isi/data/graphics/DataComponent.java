package edu.isi.data.graphics;

import edu.isi.data.model.DataModel;

public interface DataComponent {
	
	public void refreshData(DataModel dm);
	public void changeTime(int time);
	
}
