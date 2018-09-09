package matt8110.mattengine;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

import matt8110.mattengine.core.Camera;
import matt8110.mattengine.core.Utils;
import matt8110.mattengine.core.Window;
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
		
		Renderer.setAmbient(0.005f, 0.005f, 0.005f);
		Shadows.setShadows(false);
		Window.setCulling(true);
		
		Mouse.setGrabbed(true);
		DirectionalLight.enable(false);
		DirectionalLight.setColor(1.0f, 1.0f, 1.0f);
		DirectionalLight.setRotation(45, 90);
		
		Renderer.setCellShading(false);
		
		Skybox.setSkybox("lagoonbox");
		
		barrel = new OBJModel("models/barrel.obj", "models/barrel.png", "models/barrelNormal.png");
		barrel.setPosition(0, 6, 0);
		//barrel.setScale(4);
		barrel.material.enableNormalMap(true);
		barrel.material.enableSpecular(true);
		barrel.material.enableShadowCasting(false);
		rootNode.add(barrel);
		//barrel.material.setSpecularProperties(1.0f, 5);
		barrel.material.setSpecularMap(Utils.loadTexture("models/barrelSpecular.png"));
		barrel.material.setCubeMap(Utils.loadCubeMap("lagoonbox/front.png", "lagoonbox/back.png", "lagoonbox/left.png", "lagoonbox/right.png", "lagoonbox/top.png", "lagoonbox/bottom.png"));
		
		/*OBJModel barrel2 = new OBJModel("models/barrel.obj", "models/barrel.png", "models/barrelNormal.png");
		barrel2.setPosition(0, 6, 12);
		barrel2.material.setNormalMapEnabled(true);
		barrel2.material.setSpecularEnabled(true);
		barrel2.material.setSpecularMap(Utils.loadTexture("models/barrelSpecular.png"));
		rootNode.add(barrel2);*/
		
		OBJModel floor = new OBJModel("models/floor.obj", "stone.png", "stoneNormal.png");
		floor.setPosition(0, -50, 0);
		floor.setScale(500);
		rootNode.add(floor);
		floor.material.enableNormalMap(true);
		floor.material.enableSpecular(true);
		floor.material.setSpecularProperties(2.25f, 50.0f);
		//floor.material.setCubeMap(Utils.loadCubeMap("lagoonbox/front.png", "lagoonbox/back.png", "lagoonbox/left.png", "lagoonbox/right.png", "lagoonbox/top.png", "lagoonbox/bottom.png"));
		
		//Scene scene = new Scene("models/kimpossible.obj", "kpbody1.png");
		//rootNode.add(scene);
		//scene.setPosition(-8, 6, -8);
		
		Random ran = new Random();
		
		PointLight light = new PointLight(10, 10, 10, 7);
		LightManager.addLight(light);
		
		//PointLight light2 = new PointLight(100, 10, 10, 7);
		//light2.setColor(5.0f, 5.0f, 5.0f);
		//LightManager.addLight(light2);
		
		
		Font font = new Font("fonts/default_dist.fnt");
		
		Text t = new Text(font, "                 ", 16, 16, 0.25f);
		Renderer2D.add(t);
		
		//barrel.material.generateCubeMapFromScene(0, 6, 0, 2048);
		barrel.material.setCubeMapStrength(0.15f);
		//floor.material.generateCubeMapFromScene(0, -51, 0, 512);
		floor.material.setCubeMapStrength(0.50f);
		
		/*float dWidth = Display.getWidth() / 4.0f;
		float dHeight = ((float)Display.getHeight()/(float)Display.getWidth()) * dWidth;
		
		Texture2D tex = new Texture2D(GBuffer.getFBO()._gPosition, new Vector2f(0, 0), new Vector2f(dWidth, dHeight));
		Renderer2D.add(tex);
		Texture2D gPos = new Texture2D(GBuffer.getFBO()._gNormal, new Vector2f(dWidth*1, 0), new Vector2f(dWidth, dHeight));
		Renderer2D.add(gPos);
		Texture2D gNorm = new Texture2D(GBuffer.getFBO()._gTangent, new Vector2f(dWidth*2, 0), new Vector2f(dWidth, dHeight));
		Renderer2D.add(gNorm);
		Texture2D gDiffuse = new Texture2D(GBuffer.getFBO()._gDiffuse, new Vector2f(dWidth*3, 0), new Vector2f(dWidth, dHeight));
		Renderer2D.add(gDiffuse);*/
		
		/*Texture2D gDiffuse = new Texture2D(GBuffer.getFBO()._gDiffuse, new Vector2f(0, 0), new Vector2f(612, 412));
		Renderer2D.add(gDiffuse);*/
		
		float lightRot = 180;
		
		while (Window.windowIsOpen()) {
			
			Window.clearWindow(0.0f, 0.0f, 0.0f);
			
			Player.update(camera);
			
			t.updateText(Utils.FPS + " FPS");
			
			//lightRot += 0.01f * Utils.getDeltaTime();
			
			if (lightRot > 359)
				lightRot = 0;
			
			DirectionalLight.setRotation(45, lightRot);
			
			Window.updateWindow(false, 0);
			
		}
		
		Window.cleanup();
		
	}
	
}
