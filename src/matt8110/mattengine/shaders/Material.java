package matt8110.mattengine.shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Camera;
import matt8110.mattengine.core.FBO;
import matt8110.mattengine.core.Utils;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.deferred.GBufferOutputShader;
import matt8110.mattengine.deferred.GBufferShader;
import matt8110.mattengine.lighting.DirectionalLight;

public class Material {

	public int mainTexture = -1;
	public int secondTexture = -1;
	public int normalMap = -1;
	public int specularMap = -1;
	public int cubeMap = -1;
	private boolean lightingEnabled = true;
	private boolean secondTextureEnabled = false;
	private boolean specularEnabled = false;
	private boolean specularMapEnabled = false;
	private boolean cubeMapEnabled = false;
	private boolean normalMapEnabled = false;
	private float secondTextureStrength = 0.5f;
	private float specularPower = 1.0f, specularDampening = 10.0f;
	private Vector3f diffuseColor = new Vector3f(1.0f, 1.0f, 1.0f);
	private boolean canCastShadows = false;
	public boolean _cullingEnabled = true;
	private FBO cubeMapRenderFBO;
	
	public void setShaderData(GBufferShader shader) {
		
		//Enabling anything that needs it
		shader.setBool(shader.secondTextureEnable, secondTextureEnabled);
		shader.setBool(shader.normalMapEnable, normalMapEnabled);
		shader.setBool(shader.specularEnable, specularEnabled);
		shader.setBool(shader.specularMapEnable, specularMapEnabled);
		shader.setBool(shader.cubeMapEnable, cubeMapEnabled);
		
		//Setting values that need set
		shader.setVector3(shader.diffuseColor, diffuseColor);
		shader.setFloat(shader.secondTextureStrength, secondTextureStrength);
		shader.setFloat(shader.specularStrength, specularPower);
		shader.setFloat(shader.specularDampening, specularDampening);
		
		//Only setting textures if they're valid
		if (mainTexture != -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mainTexture);
		}else {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 1);
		}
		
		if (secondTextureEnabled) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, secondTexture);
		}
			
		
		if (normalMapEnabled) {
			GL13.glActiveTexture(GL13.GL_TEXTURE2);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMap);
		}
			
		
		if (specularMap != -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE3);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, specularMap);
			specularMapEnabled = true;
		}
		
		if (cubeMap != -1) {
			GL13.glActiveTexture(GL13.GL_TEXTURE5);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubeMap);
			cubeMapEnabled = true;
		}
		
		
		//Reset just in case
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
	}
	
	public void generateCubeMapFromScene(float x, float y, float z, int size) {
		
		cubeMapRenderFBO = new FBO(size, size);
		cubeMapRenderFBO.addDepthAttachment();
		
		cubeMap = Utils.createEmptyCubeMap(size);
		cubeMapEnabled = true;
		
		Camera defaultCam = Window.currentCamera;
		
		Camera cubeMapCam = new Camera(x, y, z, 0, 0, 0, 90, 0.1f, 10000.0f);
		Window.currentCamera = cubeMapCam;
		
		for (int i = 0; i < 6; i++) {
			
			float rotX = 0;
			float rotY = 0;
			
			switch (i) {
			case 0:
				rotX = 0;
				rotY = 0;
				break;
			case 1:
				rotX = 0;
				rotY = 180;
				break;
			case 2:
				rotX = -90;
				rotY = 90;
				break;
			case 3:
				rotX = 90;
				rotY = 90;
				break;
			case 4:
				rotX = 0;
				rotY = 270;
				break;
			case 5:
				rotX = 0;
				rotY = 90;
				break;
			}
				
			cubeMapCam.setRotation(rotX, rotY, 0);
			
			Window._renderScene();
		
			
			
			cubeMapRenderFBO.setCubeMapTexture(cubeMap, i);
			cubeMapRenderFBO.bindFBO();
			
			Window.gBufferOutputShader.useShader();
			
			Window.gBufferOutputShader.setRenderingCubeMap(true);
		
			Window._renderFinal();
			
			Window.gBufferOutputShader.setRenderingCubeMap(false);
			
			cubeMapRenderFBO.unbindFBO();
			
		}
		
		Window.currentCamera = defaultCam;
		
	}
	
	public void setCulling(boolean enable) {
		_cullingEnabled = enable;
	}
	public void setCubeMap(int cubeMap) {
		this.cubeMap = cubeMap;
	}
	public void setCubeMapEnabled(boolean enable) {
		cubeMapEnabled = enable;
	}
	public void setSpecularPower(float pow) {
		specularPower = pow;
	}
	public void setShadowCasting(boolean enable) {
		canCastShadows = enable;
	}
	public boolean getCanCastShadows() {
		return canCastShadows;
	}
	public void setSpecularDampening(float damp) {
		specularDampening = damp;
	}
	public void setSpecularProperties(float power, float dampening) {
		setSpecularPower(power);
		setSpecularDampening(dampening);
	}
	public void setLightingEnabled(boolean enable) {
		lightingEnabled = enable;
	}
	public void setSpecularEnabled(boolean enable) {
		specularEnabled = enable;
	}
	public void setNormalMapEnabled(boolean enable) {
		normalMapEnabled = enable;
	}
	public void setSecondTextureEnabled(boolean enable) {
		secondTextureEnabled = enable;
	}
	public void setSecondTextureStrength(float strength) {
		secondTextureStrength = strength;
	}
	public void setNormalMap(int tex) {
		normalMap = tex;
		normalMapEnabled = true;
	}
	public void setSpecularMap(int tex) {
		specularMap = tex;
		specularMapEnabled = true;
	}
	public void setSecondTexture(int tex) {
		secondTexture = tex;
		secondTextureEnabled = true;
	}
	public void setMainTexture(int tex) {
		mainTexture = tex;
	}
	public void setDiffuseColor(float r, float g, float b) {
		diffuseColor.x = r;
		diffuseColor.y = g;
		diffuseColor.z = b;
	}
	
}