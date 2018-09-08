package matt8110.mattengine.gui;

import java.util.ArrayList;
import java.util.List;

public class Renderer2D {
	
	private static List<Texture2D> textures = new ArrayList<Texture2D>();
	private static List<Text> texts = new ArrayList<Text>();
	
	public static void _renderTexts() {
		
		for (Text text : texts) {
			text.render();
		}
		
	}
	
	public static void _renderTextures() {
		
		for (Texture2D tex : textures) {
			tex.render();
		}
	}
	
	public static void add(Texture2D o) {
		textures.add(o);
	}
	public static void add(Text o) {
		texts.add(o);
	}
	
}
