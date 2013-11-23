package com.cloud.gatordrive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.swing.JOptionPane;

public class Merge {

	public File mergeFiles(InputStream[] files) {
		try {
			/*
			File[] files = new File[5];
			for (int i = 1; i <= 5; i++) {
				String fname = "/tmp/tempPlacement"+i+".splt";
				files[i - 1] = new File(fname);
			}
			*/
			File outFile = new File("/tmp/Temp.txt");

			FileOutputStream fileOS = new FileOutputStream(outFile);

			for (int i = 0; i < files.length; i++) {
				InputStream fileIS = files[i]; // new FileInputStream(files[i]);
				byte[] data = new byte[(int) files[i].available()];
				int count = fileIS.read(data);
				fileOS.write(data);
				fileIS.close();
				// if (del) {
				// files[i].delete();
				// }

			}
			fileOS.close();

			return outFile;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Error in Merge File \n" + e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

}
