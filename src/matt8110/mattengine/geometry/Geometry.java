package matt8110.mattengine.geometry;

import matt8110.mattengine.scenegraph.Renderable;

public class Geometry extends Renderable{

	public Geometry(float[] vertices, float[] normals, float[] texCoords) {
		
		vao = new VAO(vertices, normals, texCoords, true);
		
	}
	
}
