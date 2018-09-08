package matt8110.mattengine.scenegraph;

import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.shaders.ShaderType;

public class Renderer {

	public static RootNode rootNode = new RootNode();
	public static Vector3f _ambientColor = new Vector3f(0.0f, 0.0f, 0.0f);
	public static boolean _cellShadingEnabled = false;
	
	public static void render(ShaderType type) {
		
		rootNode.render(type);
		
	}
	
	public static void setAmbient(float r, float g, float b) {
		_ambientColor.x = r;
		_ambientColor.y = g;
		_ambientColor.z = b;
	}
	
	public static void setCellShading(boolean enable) {
		_cellShadingEnabled = enable;
	}
	
}
