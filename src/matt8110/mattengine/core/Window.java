package matt8110.mattengine.core;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.cubemap.Skybox;
import matt8110.mattengine.deferred.GBuffer;
import matt8110.mattengine.deferred.GBufferOutputShader;
import matt8110.mattengine.deferred.GBufferShader;
import matt8110.mattengine.gui.Renderer2D;
import matt8110.mattengine.lighting.LightManager;
import matt8110.mattengine.lighting.Shadows;
import matt8110.mattengine.scenegraph.Renderer;
import matt8110.mattengine.shaders.Shader;
import matt8110.mattengine.shaders.ShaderType;
import matt8110.mattengine.shaders.ShadowShader;
import matt8110.mattengine.shaders.SkyboxShader;

public class Window {

	public static int WIDTH, HEIGHT;
	public static boolean FULLSCREEN;
	public static boolean VSYNC;
	public static ShadowShader shadowShader;
	public static SkyboxShader skyboxShader;
	public static GBufferShader gBufferShader;
	public static GBufferOutputShader gBufferOutputShader;
	public static Shader shader2D, shaderText;
	public static Camera currentCamera;
	
	public static void createWindow(String title, int width, int height, boolean fullscreen, Camera camera, int msaa) {
		
		WIDTH = width;
		HEIGHT = height;
		FULLSCREEN = fullscreen;
		currentCamera = camera;
		
		try {
			
			Display.setTitle(title);
			setFullscreen(fullscreen);
			Display.create();
			
		}catch(Exception e) {
			System.err.println("Failed to create window!");
		}
		
		//Enable GL stuff
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable (GL11.GL_BLEND); 
		GL11.glBlendFunc (GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		shadowShader = new ShadowShader();
		gBufferShader = new GBufferShader();
		gBufferOutputShader = new GBufferOutputShader();
		skyboxShader = new SkyboxShader();
		shader2D = new Shader("shaders/2d.vert", "shaders/2d.frag");
		shaderText = new Shader("shaders/text.vert", "shaders/text.frag");
		
		//Loading default error texture
		Utils.loadTexture("texturerror.png");
		
		//Enable culling by default
		setCulling(true);
		
		//Initialize shadows
		Shadows.initShadows(2048, 400);
		
		//Init gBuffer
		GBuffer.initGBuffer(msaa);
		
		
	}
	public static void setCamera(Camera camera) {
		
		currentCamera = camera;
		
	}
	
	public static boolean windowIsOpen() {
		
		return !Display.isCloseRequested();
		
	}
	
	public static void cleanup() {
		
		Display.destroy();
		
	}
	
	public static void setFullscreen(boolean fullscreen) {
		
		FULLSCREEN = fullscreen;
		
		try {
		
			if (fullscreen) 
				Display.setDisplayMode(Display.getDesktopDisplayMode());
			else
				Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			
			Display.setFullscreen(fullscreen);
			
		}catch (Exception e) {
			System.err.println("Failed to set fullscreen!");
		}
		
	}
	
	public static void setVSync(boolean VSync) {
		
		VSYNC = VSync;
		
	}
	
	public static void _renderScene() {
		
		//Render everything in the scene
		gBufferShader.useShader();
		GBuffer.bindGBufferFBO();
		gBufferShader.setProjectionAndViewMatrix();
		Renderer.render(ShaderType.GBUFFER);
		GBuffer.unbindGBufferFBO();
	}
	
	public static void _renderFinal() {
		
		//Render skybox before final output
		Skybox.renderSkybox();
		
		//Render the final output
		LightManager.updateLights();
		gBufferOutputShader.useShader();
		gBufferOutputShader.updateShader();
		GBuffer.renderGBuffer();
		
	}
	
	public static void updateWindow(boolean capped, int FPS) {
		
		//Delta timing
		Utils._tickDeltaTime();
		
		//Rendering the shadow map
		if (Shadows.enabled) {
			shadowShader.useShader();
			Shadows.renderShadows();
		}
		
		_renderScene();
		_renderFinal();
		
		Skybox.renderSkybox();
		
		//Render 2D stuff
		shader2D.useShader();
		Renderer2D._renderTextures();
		
		//Render text
		shaderText.useShader();
		Renderer2D._renderTexts();
		
		Display.update();
		
		if (capped && !VSYNC)
			Display.sync(FPS);
		
			//Display.setVSyncEnabled(VSYNC);
		
	}
	
	public static void setCulling(boolean enable) {
		if (enable)
			GL11.glEnable(GL11.GL_CULL_FACE);
		else
			GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public static void clearWindow(float r, float g, float b) {
		
		GL11.glClearColor(r, g, b, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
	}
	
}
