package matt8110.mattengine.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Maths;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.lighting.DirectionalLight;
import matt8110.mattengine.lighting.Shadows;

public class SkyboxShader extends Shader{

	private int projectionLoc, transformationLoc, viewLoc, skyboxTexLoc;
	
	public SkyboxShader() {
		super("shaders/skybox.vert", "shaders/skybox.frag");
		
		useShader();
		projectionLoc = super.getUniformLocation("projectionMatrix");
		transformationLoc = super.getUniformLocation("transformationMatrix");
		viewLoc = super.getUniformLocation("viewMatrix");
		skyboxTexLoc = super.getUniformLocation("skybox");
		
		super.setInt(skyboxTexLoc, 0);
		
	}
	
	public void setTransformation(Vector3f position, Vector3f rotation, float scale) {
		super.setMatrix4(transformationLoc, Maths.getTransformtionMatrix(position, rotation, scale));
	}
	
	public void setProjectionAndViewMatrix() {
		super.setMatrix4(projectionLoc, Maths.getProjectionMatrix(Window.currentCamera.fov, Window.currentCamera.near, Window.currentCamera.far));
		super.setMatrix4(viewLoc, Maths.getViewMatrix());
	}
	
}
