package matt8110.mattengine.geometry;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import matt8110.mattengine.core.Utils;

public class VAO {

	private int vertexCount;
	private int vaoID;
	private List<Integer> VBOs = new ArrayList<Integer>();
	private boolean isStatic;
	
	public VAO(float[] vertices, float[] normals, float[] texCoords, boolean isStatic) {
		
		vertexCount = vertices.length / 3;
		
		this.isStatic = isStatic;
		
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		
		putDataInVBO(vertices, 0, 3);
		putDataInVBO(normals, 1, 3);
		putDataInVBO(texCoords, 2, 2);
		
		GL30.glBindVertexArray(0);
		
	}
	
	public VAO(float[] vertices, float[] normals, float[] texCoords, float[] tangents, boolean isStatic) {
		
		vertexCount = vertices.length / 3;
		
		this.isStatic = isStatic;
		
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		
		putDataInVBO(vertices, 0, 3);
		putDataInVBO(normals, 1, 3);
		putDataInVBO(texCoords, 2, 2);
		putDataInVBO(tangents, 3, 3);
		
		GL30.glBindVertexArray(0);
		
	}
	
	public VAO(float[] vertices) {
		
		vertexCount = vertices.length / 3;
		
		this.isStatic = true;
		
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		
		putDataInVBO(vertices, 0, 3);
		GL30.glBindVertexArray(0);
		
	}

	private void putDataInVBO(float[] data, int position, int size) {
		
		int vboID = GL15.glGenBuffers();
		VBOs.add(vboID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		
		if (isStatic)
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Utils.asFloatBuffer(data), GL15.GL_STATIC_DRAW);
		else
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Utils.asFloatBuffer(data), GL15.GL_DYNAMIC_DRAW);
		
		GL20.glVertexAttribPointer(position, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void modifyVBO(float[] data, int position, int size) {
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOs.get(position));
		
		if (isStatic)
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Utils.asFloatBuffer(data), GL15.GL_STATIC_DRAW);
		else
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Utils.asFloatBuffer(data), GL15.GL_DYNAMIC_DRAW);
		
		GL20.glVertexAttribPointer(position, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
	}
	
	public void modifyVAO(float[] vertices, float[] normals, float[] texCoords) {
		
		GL30.glBindVertexArray(vaoID);
		
		modifyVBO(vertices, 0, 3);
		modifyVBO(normals, 1, 3);
		modifyVBO(texCoords, 2, 2);
		
		GL30.glBindVertexArray(0);
		
	}
	
	public void modifyVAO(float[] vertices, float[] normals, float[] texCoords, float[] tangents) {
		
		GL30.glBindVertexArray(vaoID);
		
		modifyVBO(vertices, 0, 3);
		modifyVBO(normals, 1, 3);
		modifyVBO(texCoords, 2, 2);
		modifyVBO(tangents, 3, 3);
		
		GL30.glBindVertexArray(0);
		
	}
	
	public int getVertexCount() {
		return vertexCount;
	}
	
	public int getVaoID() {
		return vaoID;
	}
	
	public void cleanup() {
		
		GL30.glBindVertexArray(vaoID);
		
		for (int id : VBOs) {
			GL15.glDeleteBuffers(id);
		}
		
		GL30.glBindVertexArray(0);
		GL30.glDeleteVertexArrays(vaoID);
		
	}
	
}
