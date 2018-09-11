package matt8110.mattengine;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBTimerQuery;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
import org.lwjgl.util.vector.Vector2f;

import matt8110.mattengine.core.Camera;
import matt8110.mattengine.core.Utils;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.cubemap.CubeMap;
import matt8110.mattengine.cubemap.Skybox;
import matt8110.mattengine.deferred.GBuffer;
import matt8110.mattengine.geometry.OBJModel;
import matt8110.mattengine.gui.Font;
import matt8110.mattengine.gui.Renderer2D;
import matt8110.mattengine.gui.Text;
import matt8110.mattengine.gui.Texture2D;
import matt8110.mattengine.lighting.DirectionalLight;
import matt8110.mattengine.lighting.LightManager;
import matt8110.mattengine.lighting.PointLight;
import matt8110.mattengine.lighting.Shadows;
import matt8110.mattengine.scene.Scene;
import matt8110.mattengine.scenegraph.Renderer;
import matt8110.mattengine.scenegraph.RootNode;

public class Application {

	public static RootNode rootNode;
	public static Camera camera = new Camera(0, 100, 0, 0, 45, 0, 90, 0.1f, 10000.0f);
	public static OBJModel barrel;
	
	public static void main(String[] args) {
		
		Window.createWindow("Chameleon Engine", 1024, 768, true, camera, 0);
		Window.setVSync(false);
		rootNode = Renderer.rootNode;
		
		Renderer.setAmbient(0.05f, 0.05f, 0.05f);
		Shadows.setShadows(false);
		Window.setCulling(true);
		
		Renderer.setGammaOffset(1);
		
		Mouse.setGrabbed(true);
		DirectionalLight.enable(false);
		DirectionalLight.setColor(1.0f, 1.0f, 1.0f);
		DirectionalLight.setRotation(45, 90);
		
		Renderer.setCellShading(false);
		
		Skybox.setSkybox("lagoonbox");
		Skybox.enableSkybox(false);
		
		/*barrel = new OBJModel("models/barrel.obj", "models/barrel.png", "models/barrelNormal.png");
		barrel.setPosition(0, 6, 0);
		//barrel.setScale(4);
		barrel.material.enableNormalMap(true);
		barrel.material.enableSpecular(true);
		barrel.material.enableShadowCasting(false);
		//rootNode.add(barrel);
		barrel.material.setSpecularProperties(2.0f, 10);
		barrel.material.setSpecularMap(Utils.loadTexture("models/barrelSpecular.png"));
		barrel.material.setCubeMap(Utils.loadCubeMap("lagoonbox/front.png", "lagoonbox/back.png", "lagoonbox/left.png", "lagoonbox/right.png", "lagoonbox/top.png", "lagoonbox/bottom.png"));
		
		OBJModel cube = new OBJModel("models/cube.obj", "models/barrel.png");
		cube.setPosition(15, 6, 0);
		cube.setScale(4);
		cube.material.setCubeMap(Utils.loadCubeMap("lagoonbox/front.png", "lagoonbox/back.png", "lagoonbox/left.png", "lagoonbox/right.png", "lagoonbox/top.png", "lagoonbox/bottom.png"));
		cube.material.setCubeMapStrength(1.0f);
		//rootNode.add(cube);
		
		OBJModel floor = new OBJModel("models/floor.obj", "stone.png", "stoneNormal.png");
		floor.setPosition(0, -50, 0);
		floor.setScale(500);
		//rootNode.add(floor);
		floor.material.enableNormalMap(true);
		floor.material.enableSpecular(true);
		floor.material.setSpecularProperties(2.25f, 50.0f);
		floor.material.setCubeMap(Utils.loadCubeMap("lagoonbox/front.png", "lagoonbox/back.png", "lagoonbox/left.png", "lagoonbox/right.png", "lagoonbox/top.png", "lagoonbox/bottom.png"));
		*/
		Scene templeOfTime = new Scene("templeoftime/templeoftime.obj", "templeoftime/");
		templeOfTime.setPosition(0, -135, -200);
	//	templeOfTime.getChunk("VMtl028").material.setNormalMap(Utils.loadTexture("totbricknorm.png"));
	//	templeOfTime.getChunk("VMtl006").material.setNormalMap(Utils.loadTexture("stonefloornorm1.png"));
	//	templeOfTime.getChunk("VMtl007").material.setNormalMap(Utils.loadTexture("stonefloornorm1.png"));
	//	templeOfTime.getChunk("VMtl001").material.setNormalMap(Utils.loadTexture("bricktestbeam.png"));
		templeOfTime.getChunk("VMtl009").material.setNormalMap(Utils.loadTexture("floorref.png"));
		templeOfTime.getChunk("VMtl009").material.enableSpecular(true);
		templeOfTime.getChunk("VMtl009").material.setSpecularProperties(10.0f, 30.0f);;
		templeOfTime.getChunk("VMtl007").material.enableSpecular(true);
		templeOfTime.getChunk("VMtl007").material.setSpecularProperties(5.0f, 30.0f);;
		templeOfTime.getChunk("VMtl032").material.setNormalMap(Utils.loadTexture("symbol.png"));
		templeOfTime.getChunk("VMtl018").material.enableSpecular(true);
		templeOfTime.getChunk("VMtl018").material.setSpecularProperties(100.0f, 100.0f);
		
		templeOfTime.getChunk("VMtl028").material.enableSpecular(true);
		templeOfTime.getChunk("VMtl028").material.setSpecularProperties(1.10f, 20);
		
		templeOfTime.getChunk("VMtl001").material.enableSpecular(true);
		templeOfTime.getChunk("VMtl001").material.setSpecularProperties(0.6f, 20);
		
		templeOfTime.getChunk("VMtl092").material.setDiffuseColor(15.0f, 15.0f, 15.0f);
		rootNode.add(templeOfTime);
		
		PointLight light = new PointLight(-180, 65, 94, 125);
		light.setColor(2, 2, 2);
		LightManager.addLight(light);
		PointLight light2 = new PointLight(180, 65, 94, 125);
		light2.setColor(2, 2, 2);
		LightManager.addLight(light2);
		PointLight light3 = new PointLight(-180, 65, -100, 125);
		light3.setColor(2, 2, 2);
		LightManager.addLight(light3);
		PointLight light4 = new PointLight(180, 65, -100, 125);
		light4.setColor(2, 2, 2);
		LightManager.addLight(light4);
		PointLight light5 = new PointLight(1, 115, 230, 125);
		light5.setColor(2, 2, 2);
		LightManager.addLight(light5);
		//PointLight light6 = new PointLight(1, -15, -220, 100);
		//LightManager.addLight(light6);
		
		PointLight light7 = new PointLight(1, 300, -490, 250);
		LightManager.addLight(light7);
		
		Font font = new Font("fonts/default_dist.fnt");
		
		Text t = new Text(font, "                                       ", 16, 16, 0.25f);
		Renderer2D.add(t);
		Text gamma = new Text(font, "                                       ", 16, 32, 0.25f);
		Renderer2D.add(gamma);
		
		//barrel.material.generateCubeMapFromScene(0, 6, 0, 2048);
		//barrel.material.setCubeMapStrength(0.15f);
		//floor.material.generateCubeMapFromScene(0, -51, 0, 512);
		//floor.material.setCubeMapStrength(0.50f);
		
		CubeMap mainCubeMap = templeOfTime.getChunk("VMtl009").material.generateCubeMapFromScene(0, 0, 0, 512);
		templeOfTime.getChunk("VMtl009").material.setParallaxCorrectedCubeMap(true, 420, 600, 500);
		templeOfTime.getChunk("VMtl007").material.setCubeMap(mainCubeMap);
		templeOfTime.getChunk("VMtl007").material.setParallaxCorrectedCubeMap(true, 420, 600, 500);
		templeOfTime.getChunk("VMtl007").material.setCubeMapStrength(0.10f);
		templeOfTime.getChunk("VMtl009").material.setCubeMapStrength(0.2f);
		
		/*float dWidth = Display.getWidth() / 4.0f;
		float dHeight = ((float)Display.getHeight()/(float)Display.getWidth()) * dWidth;
		
		Texture2D tex = new Texture2D(GBuffer.getFBO()._gPosition, new Vector2f(0, 0), new Vector2f(dWidth, dHeight));
		Renderer2D.add(tex);
		Texture2D gPos = new Texture2D(GBuffer.getFBO()._gNormal, new Vector2f(dWidth*1, 0), new Vector2f(dWidth, dHeight));
		Renderer2D.add(gPos);
		Texture2D gDiffuse = new Texture2D(GBuffer.getFBO()._gDiffuse, new Vector2f(dWidth*3, 0), new Vector2f(dWidth, dHeight));
		Renderer2D.add(gDiffuse);*/
		
		/*Texture2D gDiffuse = new Texture2D(GBuffer.getFBO()._gDiffuse, new Vector2f(0, 0), new Vector2f(612, 412));
		Renderer2D.add(gDiffuse);*/
		
		float lightRot = 180;
		
		int q = GL15.glGenQueries();
		
		while (Window.windowIsOpen()) {
			
			GL15.glBeginQuery(GL33.GL_TIME_ELAPSED, q);
			
			//light6.setPosition(camera.getX(), camera.getY(), camera.getZ());
			Window.clearWindow(0.0f, 0.0f, 0.0f);
			
			Player.update(camera);
			
			//t.updateText("X: " + camera.getX() + " Y: " + camera.getY() + " Z: " + camera.getZ());
			//t.updateText("FPS: " + Utils.FPS);
			
			//lightRot += 0.01f * Utils.getDeltaTime();
			
			if (lightRot > 359)
				lightRot = 0;
			
			DirectionalLight.setRotation(45, lightRot);
			
			
			gamma.updateText("Gamma: " + Renderer.getGammaOffset());
			
			if (Keyboard.isKeyDown(Keyboard.KEY_ADD)) {
				Renderer._gamma += 0.0005f * Utils.deltaTime;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) {
				Renderer._gamma -= 0.0005f * Utils.deltaTime;
			}
			
			
			Window.updateWindow(false, 0);
			
			GL15.glEndQuery(GL33.GL_TIME_ELAPSED);
			
			//int result = 0;
			//while (result != 1) {
			//	result = GL15.glGetQueryObjecti(q, GL15.GL_QUERY_RESULT_AVAILABLE);
			//}
			
			long r = ARBTimerQuery.glGetQueryObjecti64(q, GL15.GL_QUERY_RESULT);
				t.updateText("TIME: " + r/1000000.0f + " FPS: " + Utils.FPS);
			
		}
		
		Window.cleanup();
		
	}
	
}
