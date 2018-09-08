package matt8110.mattengine.geometry;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import matt8110.mattengine.core.Utils;

public class VAO2D {

	private int vertexCount;
	private int vaoID;
	private List<Integer> VBOs = new ArrayList<Integer>();
	
	public VAO2D(float[] vertices, float[] texCoords) {
		
		vertexCount = vertices.length / 2;
		
		vaoID = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vaoID);
		
		putDataInVBO(vertices, 0, 2);
		putDataInVBO(texCoords, 1, 2);
		
		GL30.glBindVertexArray(0);
		
	}

	private void putDataInVBO(float[] data, int position, int size) {
		
		int vboID = GL15.glGenBuffers();
		VBOs.add(vboID);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Utils.asFloatBuffer(data), GL15.GL_DYNAMIC_DRAW);
		
		GL20.glVertexAttribPointer(position, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void modifyVBO(float[] data, int position, int size) {
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBOs.get(position));
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Utils.asFloatBuffer(data), GL15.GL_DYNAMIC_DRAW);
		
		GL20.glVertexAttribPointer(position, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
	}
	
	public void modifyVAO(float[] vertices, float[] texCoords) {
		
		GL30.glBindVertexArray(vaoID);
		
		modifyVBO(vertices, 0, 2);
		modifyVBO(texCoords, 1, 2);
		
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
