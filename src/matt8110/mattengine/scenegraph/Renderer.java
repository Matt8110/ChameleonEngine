package matt8110.mattengine.scenegraph;

import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.shaders.ShaderType;

public class Renderer {

	public static RootNode rootNode = new RootNode();
	public static Vector3f _ambientColor = new Vector3f(1.0f, 1.0f, 1.0f);
	public static boolean _cellShadingEnabled = false;
	public static float _exposure = 1.0f;
	public static float _gamma = 2.2f;
	public static boolean _bloomEnabled = false;
	public static int polyCount = 0;
	
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
	
	public static void setBloom(boolean enable) {
		_bloomEnabled = enable;
	}
	
	public static void setExposure(float exp) {
		_exposure = exp;
	}
	
	public static float getExposure() {
		return _exposure;
	}
	
	public static void setGammaOffset(float gamma) {
		_gamma = 2.2f + gamma;
	}
	
	public static float getGammaOffset() {
		return _gamma - 2.2f;
	}
	
	public static float getGamma() {
		return _gamma;
	}
	
}
