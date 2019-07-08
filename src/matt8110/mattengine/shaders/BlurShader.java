package matt8110.mattengine.shaders;

import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Maths;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.lighting.DirectionalLight;

public class BlurShader extends Shader{

	private int horLoc, texLoc;
	
	public BlurShader() {
		super("shaders/blur.vert", "shaders/blur.frag");
		
		useShader();
		horLoc = super.getUniformLocation("horizontal");
		texLoc = super.getUniformLocation("tex");
		
		super.setInt(texLoc, 0);
		
	}
	
	public void setHorizontal(boolean hor) {
		super.setBool(horLoc, hor);
	}
	
}
