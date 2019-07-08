package matt8110.mattengine.shaders;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import matt8110.mattengine.core.FBO;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.gui.Texture2D;

public class Blur {

	public static FBO[] fbo;
	private static int[] textures = new int[2];
	private static Texture2D tex = new Texture2D(1, new Vector2f(0, 0), new Vector2f(Display.getWidth(), Display.getHeight()));
	
	public static void initBlur() {
		
		fbo = new FBO[2];
		
		for (int i = 0; i < 2; i++) {
			fbo[i] = new FBO(Display.getWidth(), Display.getHeight());
			fbo[i].addTextureAttachment();
		}
		
	}
	
	public static int renderBlur(int texture, int times) {
		
		textures[0] = texture;
		
		Window.blurShader.useShader();
		
		for (int j = 0; j < times; j++)
		for (int i = 0; i < 2; i++) {
			
			fbo[i].bindFBO();
			
			if (i == 0) {
				Window.blurShader.setHorizontal(true);
				tex.render(texture);
			}
			if (i == 1) {
				Window.blurShader.setHorizontal(true);
				tex.render(textures[0]);
			}
			
			fbo[i].unbindFBO();
			
			textures[i] = fbo[i].getTexture();
		
		}
		
		//Return the last output
		return textures[1];
		
	}
	
}
