package matt8110.mattengine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Camera;
import matt8110.mattengine.core.Utils;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.geometry.OBJModel;

public class Player {

	//Player stuff
	public static Vector3f position = new Vector3f(15, 10, 15);
	public static float rotX, rotY;
	public static float speed = 0.05f;
	private static float lastXInc = 0, lastYInc = 0, lastZInc = 0;
	private static float lastX, lastY, lastZ;
	
	//Mouse stuff
	public static int lastMouseX, lastMouseY;
	public static float mouseSensitivity = 0.15f;
	
	//General stuff
	private static float degToRad = 3.14159265359f / 180.0f;
	private static float deltaTime;
	
	public static void update(Camera camera) {
		
		deltaTime = Utils.getDeltaTime();
		
		movement();
		mouseLook();
		
		/*if (Application.barrel.checkAABB(position)) {
			position.x = lastX;
			position.y = lastY;
			position.z = lastZ;
		}*/
		
		camera.setRotX(rotX);
		camera.setRotY(rotY);
		camera.setPosition(position.x, position.y, position.z);
		
		lastX = position.x;
		lastY = position.y;
		lastZ = position.z;
		
	}
	
	private static void mouseLook() {
		
		rotY -= (Mouse.getX() - lastMouseX) * mouseSensitivity;
		rotX -= (Mouse.getY() - lastMouseY) * mouseSensitivity;
		
		if (Mouse.getX() > Display.getWidth()/2 + 64)
			Mouse.setCursorPosition(Display.getWidth()/2 - 64, Mouse.getY());
		if (Mouse.getX() < Display.getWidth()/2 - 64)
			Mouse.setCursorPosition(Display.getWidth()/2 + 64, Mouse.getY());
		if (Mouse.getY() > Display.getHeight()/2 + 64)
			Mouse.setCursorPosition(Mouse.getX(), Display.getHeight()/2 - 64);
		if (Mouse.getY() < Display.getHeight()/2 - 64)
			Mouse.setCursorPosition(Mouse.getX(), Display.getHeight()/2 + 64);
		
		lastMouseX = Mouse.getX();
		lastMouseY = Mouse.getY();
		
		if (rotX < -90)
			rotX = -90;
		if (rotX > 90)
			rotX = 90;
		
		if (rotY > 359)
			rotY = 0;
		if (rotY < 0)
			rotY = 359;
		
		
	}
	
	private static void movement() {
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			position.x += Math.cos((rotY) * degToRad) * speed * deltaTime;
			position.z -= Math.sin((rotY) * degToRad) * speed * deltaTime;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			position.x += Math.cos((rotY + 180) * degToRad) * speed * deltaTime;
			position.z -= Math.sin((rotY + 180) * degToRad) * speed * deltaTime;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			position.x += Math.cos((rotY + 90) * degToRad) * speed * deltaTime;
			position.z -= Math.sin((rotY + 90) * degToRad) * speed * deltaTime;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			position.x += Math.cos((rotY + 270) * degToRad) * speed * deltaTime;
			position.z -= Math.sin((rotY + 270) * degToRad) * speed * deltaTime;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			position.y += speed * deltaTime;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			position.y -= speed * deltaTime;
		}
		
		lastXInc = position.x - lastX;
		lastYInc = position.x - lastX;
		lastZInc = position.x - lastX;
		
	}

}
