package matt8110.mattengine.geometry;

import java.io.File;
import java.util.Scanner;

import matt8110.mattengine.core.TextureManager;

public class MTLFile {

	private String lastMatName = null;
	private String lastTexName = null;
	
	public MTLFile(String filename, String filePath) {
		
		try {
			
			Scanner scan = new Scanner(new File(filename));
			
			while (scan.hasNextLine()) {
				
				String line = scan.nextLine();
				line = line.replaceAll("\\\\", " ");
				String[] lineSplit = line.split(" ");
				
				if (lineSplit[0].equalsIgnoreCase("map_Kd")) {
					lastTexName = filePath + lineSplit[lineSplit.length-1];
				}
				
				if (lineSplit[0].equalsIgnoreCase("newmtl")) {
					
					if (lastMatName != null) {
						
						TextureManager.addTexture(lastMatName, lastTexName);
						
					}
					
					lastTexName = "texturerror.png";
					lastMatName = lineSplit[1];
				}
				
			}
			
			scan.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
