package matt8110.mattengine.lighting;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class DirectionalLight {

	private static Vector2f _rotation = new Vector2f(45f, 45f);
	private static Vector3f _rotationNormalized = new Vector3f();
	private static Vector3f _color = new Vector3f(1.0f, 1.0f, 1.0f);
	private static float _brightness = 0.5f;
	public static boolean _enabled = false;
	
	public static void setRotation(float x, float y) {
		_rotation.x = x;
		_rotation.y = y;
	}
	
	public static Vector2f getRotation() {
		return _rotation;
	}
	
	public static Vector3f getRotationNormalized() {
		
		_rotationNormalized.x = (float) Math.cos(Math.toRadians(_rotation.y));
		_rotationNormalized.z = (float) -Math.sin(Math.toRadians(_rotation.y));
		_rotationNormalized.y = (float) -Math.tan(Math.toRadians(_rotation.x));
		
		_rotationNormalized.normalise();
		
		return _rotationNormalized;
	}
	
	public static void setColor(float r, float g, float b) {
		_color.x = r;
		_color.y = g;
		_color.z = b;
	}
	
	public static Vector3f getColor() {
		return _color;
	}
	
	public static void setBrightness(float b) {
		_brightness = b;
	}
	
	public static float getBrightness() {
		return _brightness;
	}
	
	public static void enable(boolean enable) {
		_enabled = enable;
	}
	
}
