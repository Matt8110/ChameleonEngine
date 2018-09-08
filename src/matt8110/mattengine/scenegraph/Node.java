package matt8110.mattengine.scenegraph;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.shaders.ShaderType;

public class Node {

	private List<Node> children = new ArrayList<Node>();
	private List<Renderable> renderables = new ArrayList<Renderable>();
	private Vector3f position;
	private Vector3f rotation;
	private float scale;
	
	public Node(float x, float y, float z) {
		
		scale = 0;
		rotation = new Vector3f();
		position = new Vector3f(x, y, z);
		
	}
	
	public void add(Node child) {
		
		child.position = Vector3f.add(child.position, position, null);
		child.rotation = Vector3f.add(child.rotation, rotation, null);
		child.scale = child.scale + scale;
		children.add(child);
		
	}
	
	public void add(Renderable child) {
		
		child.position = Vector3f.add(child.position, position, null);
		child.rotation = Vector3f.add(child.rotation, rotation, null);
		child.scale = child.scale + scale;
		renderables.add(child);
		
	}
	
	public void render(ShaderType type) {
		
		for (Node node : children) {
			node.render(type);
		}
		
		for (Renderable ren : renderables) {
			ren.render(type);
		}
		
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	public Vector3f getRotation() {
		return rotation;
	}
	
	public void setRotation(float x, float y, float z) {
		rotation.x = x;
		rotation.y = y;
		rotation.z = z;
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
}
