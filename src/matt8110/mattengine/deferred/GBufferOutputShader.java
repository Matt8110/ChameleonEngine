package matt8110.mattengine.deferred;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Maths;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.lighting.DirectionalLight;
import matt8110.mattengine.lighting.LightManager;
import matt8110.mattengine.lighting.Shadows;
import matt8110.mattengine.scenegraph.Renderer;
import matt8110.mattengine.shaders.Shader;

public class GBufferOutputShader extends Shader{

	private int gPositionLoc, gNormalLoc, gDiffuseLoc, gSpecularLoc, gBloomMapLoc, directionalLightDirectionLoc, directionalLightEnableLoc, directionalLightColorLoc;
	private int ambientColor, cameraPositionLoc, cellShadingEnableLoc, renderingCubeMapLoc;
	private int pointLightPositionLoc, pointLightColorLoc, pointLightRangeLoc, numberOfPointLightsLoc, exposureLoc, gammaLoc;
	
	public GBufferOutputShader() {
		super("shaders/gBufferOutput.vert", "shaders/gBufferOutput.frag");
		
		useShader();
		
		//Textures
		gPositionLoc = super.getUniformLocation("gPosition");
		gNormalLoc = super.getUniformLocation("gNormal");
		gDiffuseLoc = super.getUniformLocation("gDiffuse");
		gSpecularLoc = super.getUniformLocation("gSpecular");
		gBloomMapLoc = super.getUniformLocation("gBloomMap");
		
		directionalLightDirectionLoc = super.getUniformLocation("directionalLightDirection");
		directionalLightEnableLoc = super.getUniformLocation("directionalLightEnable");
		directionalLightColorLoc = super.getUniformLocation("directionalLightColor");
		cellShadingEnableLoc = super.getUniformLocation("cellShadingEnable");
		ambientColor = super.getUniformLocation("ambientColor");
		cameraPositionLoc = super.getUniformLocation("cameraPosition");
		renderingCubeMapLoc = super.getUniformLocation("renderingCubeMap");
		
		pointLightPositionLoc = super.getUniformLocation("pointLightPosition");
		pointLightColorLoc = super.getUniformLocation("pointLightColor");
		pointLightRangeLoc = super.getUniformLocation("pointLightRange");
		numberOfPointLightsLoc = super.getUniformLocation("numberOfPointLights");
		exposureLoc = super.getUniformLocation("exposure");
		gammaLoc = super.getUniformLocation("gamma");
		
		super.setInt(gPositionLoc, 0);
		super.setInt(gNormalLoc, 1);
		super.setInt(gDiffuseLoc, 2);
		super.setInt(gSpecularLoc, 3);
		super.setInt(gBloomMapLoc, 4);
		
	}
	
	public void setRenderingCubeMap(boolean enable) {
		super.setBool(renderingCubeMapLoc, enable);
	}
	
	public void updateShader() {
		
		super.setVector3(directionalLightDirectionLoc, DirectionalLight.getRotationNormalized());
		super.setVector3(directionalLightColorLoc, DirectionalLight.getColor());
		super.setVector3(ambientColor, Renderer._ambientColor);
		super.setVector3(cameraPositionLoc, Window.currentCamera.getPosition());
		
		super.setVector3Array(pointLightPositionLoc, LightManager._lightPosition);
		super.setVector3Array(pointLightColorLoc, LightManager._lightColor);
		super.setFloatArray(pointLightRangeLoc, LightManager._lightRange);
		super.setFloat(numberOfPointLightsLoc, LightManager.pointLights.size());
		
		super.setBool(directionalLightEnableLoc, DirectionalLight._enabled);
		super.setBool(cellShadingEnableLoc, Renderer._cellShadingEnabled);
		super.setFloat(exposureLoc, Renderer._exposure);
		super.setFloat(gammaLoc, Renderer._gamma);
		
	}
	
}
