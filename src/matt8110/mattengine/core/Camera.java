package matt8110.mattengine.core;

import org.lwjgl.util.vector.Vector3f;

public class Camera {

	public Vector3f position, rotation;
	public int fov;
	public float near, far;
	
	public Camera(float x, float y, float z, float rotX, float rotY, float rotZ, int fov, float near, float far) {
		
		position = new Vector3f(x, y, z);
		rotation = new Vector3f(rotX, rotY, rotZ);
		this.fov = fov;
		this.near = near;
		this.far = far;
		
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(float x, float y, float z) {
		setX(x);
		setY(y);
		setZ(z);
	}
	
	public float getX() {
		return position.x;
	}
	
	public float getY() {
		return position.y;
	}
	
	public float getZ() {
		return position.z;
	}
	
	public void setX(float x) {
		position.x = x;
	}
	
	public void setY(float y) {
		position.y = y;
	}
	
	public void setZ(float z) {
		position.z = z;
	}
	
	public float getRotX() {
		return rotation.x;
	}
	
	public float getRotY() {
		return rotation.y;
	}
	
	public float getRotZ() {
		return rotation.z;
	}
	
	public void setRotX(float x) {
		rotation.x = x;
	}
	
	public void setRotY(float y) {
		rotation.y = y;
	}
	
	public void setRotZ(float z) {
		rotation.z = z;
	}
	
	public void setRotation(float x, float y, float z) {
		setRotX(x);
		setRotY(y);
		setRotZ(z);
	}
	
}
