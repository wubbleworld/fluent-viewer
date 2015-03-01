package edu.isi.data.graphics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.isi.data.DataProperties;
import edu.isi.data.DataVisualization;
import edu.isi.data.model.DatabaseManager;

public class DataMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;
	
	public DataMenu() {

		addComponents();
		addListeners();
	}
	
	private void addComponents() {
		add(_fileMenu);
		
		_fileMenu.add(_loadItem);
	}
	
	private void addListeners() {
		_loadItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				String path = DataProperties.inst().getPath();
				JFileChooser jf = new JFileChooser(path);
				if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String fileName = jf.getSelectedFile().getAbsolutePath();
					
					DataVisualization dv = DataVisualization.inst();
					DatabaseManager.inst().connect(fileName);
					SessionPanel sp = (SessionPanel) dv.get(SessionPanel.class.getName());
					sp.repopulate();
					
					DataProperties.inst().saveProps(jf.getSelectedFile().getPath());
				}
				
			}
		});
	}
	
	protected JMenu _fileMenu = new JMenu("File");

	protected JMenuItem _loadItem = new JMenuItem("Load...");
}
