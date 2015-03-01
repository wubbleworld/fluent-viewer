package edu.isi.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;


public class DataProperties {
	protected String fileName = ".modeler.properties";
	protected String path = ".";
	protected Properties prop;

	private static DataProperties mp = null;
	
	private DataProperties() {
		loadProperties();
	}
	
	public static DataProperties inst() {
		if (mp == null) {
			mp = new DataProperties();
		}
		return mp;
	}
	
	protected void loadProperties() {
        FileInputStream fin = null;
    	prop = new Properties();
        try {
            fin = new FileInputStream(fileName);
            prop.load(fin);
            path = prop.getProperty("path");
            fin.close();
        } catch (Exception e) {
        	System.out.println("Properties don't exist, will create after first save");
        }
	}

	public void saveProps(String newPath) {
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(fileName);

            System.out.println("newPath: " + newPath);
    		int fileStart = newPath.lastIndexOf(File.separator);
    		String directory = newPath.substring(0, fileStart);
            path = directory;
            
            prop.clear();
            prop.put("path", path);

            prop.store(fout, "Properties");

            fout.close();
        } catch (IOException e) {
            System.out.println("Could not save properties: " + e.toString());
        }
	}
	
	public String getPath() {
		return path;
	}

}
