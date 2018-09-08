package matt8110.mattengine.lighting;

import org.lwjgl.util.vector.Vector3f;

public class PointLight {

	public int _InternalID;
	public Vector3f position;
	public Vector3f color;
	public float range;
	
	public PointLight(float x, float y, float z, float range) {
		createLight(x, y, z, 1.0f, 1.0f, 1.0f, range);
	}
	public PointLight(float x, float y, float z, float r, float g, float b, float range) {
		createLight(x, y, z, r, g, b, range);
	}
	
	private void createLight(float x, float y, float z, float r, float g, float b, float range) {
		
		position = new Vector3f(x, y, z);
		color = new Vector3f(r, g, b);
		this.range = range;
		
	}
	
	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	public void setColor(float r, float g, float b) {
		color.x = r;
		color.y = g;
		color.z = b;
	}
	
	public void setRange(float range) {
		this.range = range;
	}
	
}
