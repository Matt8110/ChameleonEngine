package matt8110.mattengine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBTimerQuery;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;

import matt8110.mattengine.core.Camera;
import matt8110.mattengine.core.Input;
import matt8110.mattengine.core.Utils;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.cubemap.Skybox;
import matt8110.mattengine.geometry.OBJModel;
import matt8110.mattengine.gui.Font;
import matt8110.mattengine.gui.Renderer2D;
import matt8110.mattengine.gui.Text;
import matt8110.mattengine.lighting.DirectionalLight;
import matt8110.mattengine.lighting.LightManager;
import matt8110.mattengine.lighting.PointLight;
import matt8110.mattengine.lighting.Shadows;
import matt8110.mattengine.planets.Planet;
import matt8110.mattengine.planets.Rocky;
import matt8110.mattengine.scenegraph.Renderer;
import matt8110.mattengine.scenegraph.RootNode;
import matt8110.mattengine.terrain.TerrainManager;

public class Application {

	public static RootNode rootNode;
	public static Camera camera = new Camera(0, 5000, 0, 0, 45, 0, 90, 0.1f, 10000.0f);
	public static boolean debug = true;
	public static Font font;
	public static Text t, ft, gamma, polyCount, playerPos, vRam;
	public static Text healthText, oxygenText;
	public static Planet currentPlanet;
	
	public static void main(String[] args) {
		
		Window.createWindow("Chameleon Engine", 1024, 768, false, camera, 16);
		Window.setVSync(true);
		rootNode = Renderer.rootNode;
		
		long startVMem = GL11.glGetInteger(0x9049);
		
		Renderer.setAmbient(0.10f, 0.10f, 0.10f);
		Shadows.setShadows(true);
		Window.setCulling(true);
		
		Renderer.setGammaOffset(0.5f);
		Renderer.setBloom(true);
		
		Mouse.setGrabbed(true);
		
		Renderer.setCellShading(false);
		
		Skybox.setSkybox("spacebox/");
		Skybox.enableSkybox(true);
		
		DirectionalLight.enable(true);
		DirectionalLight.setRotation(20, 180-75);
		DirectionalLight.setColor(1.0f, 1.0f, 1.0f);
		
		font = new Font("fonts/default_dist.fnt");
		
		debugStuff();
		
		currentPlanet = new Rocky();
		
		//Player information
		healthText = new Text(font, "                     ", 16, Display.getHeight()-64, 0.25f);
		oxygenText = new Text(font, "                     ", 16, Display.getHeight()-48, 0.25f);
		Renderer2D.add(healthText);
		Renderer2D.add(oxygenText);
		
		OBJModel model = new OBJModel("models/oxygenator.obj", "models/white.png");
		model.material.enableShadowCasting(true);
		if (debug) {
			rootNode.add(model);
			model.setScale(2f);
			model.material.enableSpecular(true);
			model.material.setDiffuseColor(0.6f, 1, 0.6f);
		}
		
		int q = GL15.glGenQueries();
		
		//PointLight light = new PointLight(0, 0, 0, 50);
		//LightManager.addLight(light);
		
		while (Window.windowIsOpen()) {
			
			if (debug) {
				//model.setPosition(currentPlanet.terrainManager.getRayIntersectionPosition().x, currentPlanet.terrainManager.getRayIntersectionPosition().y+2, currentPlanet.terrainManager.getRayIntersectionPosition().z);
			}
			
			GL15.glBeginQuery(GL33.GL_TIME_ELAPSED, q);
			
			Window.clearWindow(0.0f, 0.0f, 0.0f);
			
			if(Mouse.isButtonDown(0)) {
				//OBJModel model2 = new OBJModel("models/oxygenator.obj", "models/white.png");
				//model2.setPosition(currentPlanet.terrainManager.getRayIntersectionPosition().x, currentPlanet.terrainManager.getRayIntersectionPosition().y+2, currentPlanet.terrainManager.getRayIntersectionPosition().z);
				//model2.setScale(2f);
				//model2.material.enableSpecular(true);
				//rootNode.add(model2);
				
				currentPlanet.terrainManager.addHeight(currentPlanet.terrainManager.getRayIntersectionPosition().x, currentPlanet.terrainManager.getRayIntersectionPosition().z, 0.1f);
			}
			
			if(Mouse.isButtonDown(1)) {
				currentPlanet.terrainManager.addHeight(currentPlanet.terrainManager.getRayIntersectionPosition().x, currentPlanet.terrainManager.getRayIntersectionPosition().z, -0.1f);
			}
			
			
			
			//light.setPosition(Player.position.x, Player.position.y+8, Player.position.z);
			
			//Updating player stuff
			Player.update(camera);
			healthText.updateText("Health: " + (int)Player.health + "/" + (int)Player.maxHealth);
			oxygenText.updateText("Oxygen: " + (int)Player.oxygen + "/" + (int)Player.maxOxygen);
			
			currentPlanet.update();
			
			Window.updateWindow(false, 0);
			
			GL15.glEndQuery(GL33.GL_TIME_ELAPSED);
			long r = ARBTimerQuery.glGetQueryObjecti64(q, GL15.GL_QUERY_RESULT);
			
			if (debug) {
				gamma.updateText("Gamma: " + Renderer.getGammaOffset());
				
				if (Keyboard.isKeyDown(Keyboard.KEY_ADD)) {
					Renderer._gamma += 0.0005f * Utils.deltaTime;
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) {
					Renderer._gamma -= 0.0005f * Utils.deltaTime;
				}
				
				t.updateText("Fps: " + Utils.FPS);
				polyCount.updateText("Tris: " + Renderer.polyCount);
				ft.updateText("Frame-Time: " + r/1000000.0f);
				playerPos.updateText("X: " + camera.getX() + " Y: " + camera.getY() + " Z: " + camera.getZ());
				vRam.updateText("VRAM: " + (float)(startVMem - GL11.glGetInteger(0x9049))/1000 + "MB");
			}
		}
		
		Window.cleanup();
		
	}
	
	public static void debugStuff() {
		
		if (debug) {
			t = new Text(font, "                                       ", 16, 16, 0.25f);
			Renderer2D.add(t);
			ft = new Text(font, "                                       ", 16, 32, 0.25f);
			Renderer2D.add(ft);
			gamma = new Text(font, "                                       ", 16, 48, 0.25f);
			Renderer2D.add(gamma);
			polyCount = new Text(font, "                                       ", 16, 64, 0.25f);
			Renderer2D.add(polyCount);
			playerPos = new Text(font, "                                       ", 16, 80, 0.25f);
			Renderer2D.add(playerPos);
			vRam = new Text(font, "                                       ", 16, 96, 0.25f);
			Renderer2D.add(vRam);
		}
		
	}
	
}
