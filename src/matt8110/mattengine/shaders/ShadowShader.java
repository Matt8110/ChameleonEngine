package matt8110.mattengine.shaders;

import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Maths;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.lighting.DirectionalLight;

public class ShadowShader extends Shader{

	private int projectionLoc, transformationLoc, viewLoc;
	
	public ShadowShader() {
		super("shaders/shadow.vert", "shaders/shadow.frag");
		
		useShader();
		projectionLoc = super.getUniformLocation("projectionMatrix");
		transformationLoc = super.getUniformLocation("transformationMatrix");
		viewLoc = super.getUniformLocation("viewMatrix");
		
	}
	
	public void setTransformation(Vector3f position, Vector3f rotation, float scale) {
		super.setMatrix4(transformationLoc, Maths.getTransformtionMatrix(position, rotation, scale));
	}
	
	public void setProjectionAndViewMatrix() {
		super.setMatrix4(projectionLoc, Maths.createOrthoMatrix());
		super.setMatrix4(viewLoc, Maths.getShadowViewMatrix(DirectionalLight.getRotation().x, DirectionalLight.getRotation().y));
	}
	
}
