package matt8110.mattengine.core;

import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import matt8110.mattengine.cubemap.Skybox;
import matt8110.mattengine.deferred.GBuffer;
import matt8110.mattengine.deferred.GBufferOutputShader;
import matt8110.mattengine.deferred.GBufferShader;
import matt8110.mattengine.gui.Renderer2D;
import matt8110.mattengine.lighting.LightManager;
import matt8110.mattengine.lighting.Shadows;
import matt8110.mattengine.scenegraph.Renderer;
import matt8110.mattengine.shaders.Blur;
import matt8110.mattengine.shaders.BlurShader;
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
	public static BlurShader blurShader;
	public static GBufferOutputShader gBufferOutputShader;
	public static Shader shader2D, shaderText;
	public static Camera currentCamera;
	public static AtomicBoolean running = new AtomicBoolean();
	
	public static void createWindow(String title, int width, int height, boolean fullscreen, Camera camera, int aa) {
		
		WIDTH = width;
		HEIGHT = height;
		FULLSCREEN = fullscreen;
		currentCamera = camera;
		running.set(true);
		
		try {
			
			Display.setTitle(title);
			setFullscreen(fullscreen);
			Display.create(new PixelFormat().withSamples(aa).withDepthBits(24));
			
			AL.create();
			
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
		blurShader = new BlurShader();
		shader2D = new Shader("shaders/2d.vert", "shaders/2d.frag");
		shaderText = new Shader("shaders/text.vert", "shaders/text.frag");
		
		//Loading default error texture
		Utils.loadTexture("texturerror.png");
		
		//Enable culling by default
		setCulling(true);
		
		//Initialize shadows
		Shadows.initShadows(4096, 200);
		
		//Init gBuffer
		GBuffer.initGBuffer(1);
		
		//Init gaussian blur
		Blur.initBlur();
		
		
	}
	public static void setCamera(Camera camera) {
		
		currentCamera = camera;
		
	}
	
	public static Camera getCurrentCamera() {
		return currentCamera;
	}
	
	public static boolean windowIsOpen() {
		
		return !Display.isCloseRequested();
		
	}
	
	public static void cleanup() {
		
		running.set(false);
		AL.destroy();
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
		
		if (Renderer._bloomEnabled)
			GBuffer._finalGBloom = Blur.renderBlur(GBuffer.getFBO()._gBloomMap, 1);
		
	}
	
	public static void _renderFinal() {
		
		//Render the final output
		LightManager.updateLights();
		gBufferOutputShader.useShader();
		gBufferOutputShader.updateShader();
		GBuffer.renderGBuffer();
		
		//Rendering the skybox
		Skybox.renderSkybox();
		
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
