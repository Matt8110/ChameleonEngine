package matt8110.mattengine.core;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {

	public static Map<String, Integer> textures = new HashMap<String, Integer>();
	
	public static void addTexture(String key, String texture) {
		
		if (textures.containsKey(key)) {
			System.err.println("Warning: texture " + key + " has already been defined!");
		}else {
			textures.put(key, Utils.loadTexture(texture));
		}
		
	}
	public static int getTexture(String key) {
		
		if (textures.containsKey(key))
			return textures.get(key);
		else
			return 1;
		
	}
	public static void removeTexture(String key) {
		textures.remove(key);
	}
	
	public static void clearTextures() {
		textures.clear();
	}
	
}
