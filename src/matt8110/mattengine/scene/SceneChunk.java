package matt8110.mattengine.scene;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.geometry.VAO;
import matt8110.mattengine.shaders.Material;

public class SceneChunk {
	
	public VAO vao;
	public Material material = new Material();
	public float x1, x2, y1, y2, z1, z2;
	public Vector3f rotation = new Vector3f();
	
}
