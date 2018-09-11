package matt8110.mattengine.deferred;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Maths;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.lighting.DirectionalLight;
import matt8110.mattengine.lighting.Shadows;
import matt8110.mattengine.shaders.Shader;

public class GBufferShader extends Shader{

	private int projectionLoc, transformationLoc, viewLoc, shadowViewLoc, shadowProjectionLoc, shadowBiasLoc, cameraPosLoc;
	private int mainTextureLoc, secondTextureLoc, shadowMapLoc, directionalLightDirectionLoc, normalMapLoc, specularMapLoc, cubeMapLoc;
	public int secondTextureEnable, normalMapEnable, specularEnable, specularMapEnable, shadowEnable, cubeMapEnable, cubePosition, cubeSize;
	public int secondTextureStrength, diffuseColor, specularStrength, specularDampening, cubeMapStrength, parallaxCorrected;
	
	public GBufferShader() {
		super("shaders/GBuffer.vert", "shaders/GBuffer.frag");
		
		useShader();
		
		projectionLoc = super.getUniformLocation("projectionMatrix");
		transformationLoc = super.getUniformLocation("transformationMatrix");
		viewLoc = super.getUniformLocation("viewMatrix");
		shadowProjectionLoc = super.getUniformLocation("shadowProjectionMatrix");
		shadowViewLoc = super.getUniformLocation("shadowViewMatrix");
		shadowBiasLoc = super.getUniformLocation("shadowBias");
		cubeMapStrength = super.getUniformLocation("cubeMapStrength");
		parallaxCorrected = super.getUniformLocation("parallaxCorrected");
		cubePosition = super.getUniformLocation("cubePosition");
		cubeSize = super.getUniformLocation("cubeSize");
		
		//Textures
		mainTextureLoc = super.getUniformLocation("mainTexture");
		secondTextureLoc = super.getUniformLocation("secondTexture");
		shadowMapLoc = super.getUniformLocation("shadowMap");
		normalMapLoc = super.getUniformLocation("normalMap");
		specularMapLoc = super.getUniformLocation("specularMap");
		cubeMapLoc = super.getUniformLocation("cubeMap");
		
		secondTextureEnable = super.getUniformLocation("secondTextureEnable");
		normalMapEnable = super.getUniformLocation("normalMapEnable");
		specularEnable = super.getUniformLocation("specularEnable");
		cubeMapEnable = super.getUniformLocation("cubeMapEnable");
		specularMapEnable = super.getUniformLocation("specularMapEnable");
		specularStrength = super.getUniformLocation("specularStrength");
		specularDampening = super.getUniformLocation("specularDampening");
		shadowEnable = super.getUniformLocation("shadowEnable");
		cameraPosLoc = super.getUniformLocation("cameraPos");
		
		secondTextureStrength = super.getUniformLocation("secondTextureStrength");
		diffuseColor = super.getUniformLocation("diffuseColor");
		
		directionalLightDirectionLoc = super.getUniformLocation("directionalLightDirection");
		
		super.setInt(mainTextureLoc, 0);
		super.setInt(secondTextureLoc, 1);
		super.setInt(normalMapLoc, 2);
		super.setInt(specularMapLoc, 3);
		super.setInt(shadowMapLoc, 4);
		super.setInt(cubeMapLoc, 5);
		
		super.setMatrix4(shadowBiasLoc, Maths.getShadowBiasMatrix());
		
	}
	
	public void setTransformation(Vector3f position, Vector3f rotation, float scale) {
		super.setMatrix4(transformationLoc, Maths.getTransformtionMatrix(position, rotation, scale));
	}
	
	private void setShadowProjectionAndViewMatrix() {
		super.setMatrix4(shadowProjectionLoc, Maths.createOrthoMatrix());
		super.setMatrix4(shadowViewLoc, Maths.getShadowViewMatrix(DirectionalLight.getRotation().x, DirectionalLight.getRotation().y));
		
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, Shadows._shadowFBO.getDepthBufferTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
	}
	
	public void setProjectionAndViewMatrix() {
		
		setShadowProjectionAndViewMatrix();
		
		super.setMatrix4(projectionLoc, Maths.getProjectionMatrix(Window.currentCamera.fov, Window.currentCamera.near, Window.currentCamera.far));
		super.setMatrix4(viewLoc, Maths.getViewMatrix());
		
		super.setVector3(directionalLightDirectionLoc, DirectionalLight.getRotationNormalized());
		
		super.setBool(shadowEnable, Shadows.enabled);
		
		super.setVector3(cameraPosLoc, Window.currentCamera.getPosition());
	}
	
}
