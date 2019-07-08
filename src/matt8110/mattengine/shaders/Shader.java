package matt8110.mattengine.shaders;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.Scanner;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Utils;

public class Shader {

	protected int programID;
	private int vertexID;
	private int fragmentID;
	private int geometryID;
	
	public Shader(String vertexShader, String fragmentShader) {
		
		createShader(vertexShader, null, fragmentShader);
		
	}
	
	public Shader(String vertexShader, String geometryShader, String fragmentShader) {
		
		createShader(vertexShader, geometryShader, fragmentShader);
		
	}
	
	private void createShader(String vertexShader, String geometryShader, String fragmentShader) {
		
		vertexID = loadShader(vertexShader, GL20.GL_VERTEX_SHADER);
		fragmentID = loadShader(fragmentShader, GL20.GL_FRAGMENT_SHADER);
		
		if (geometryShader != null) {
			geometryID = loadShader(geometryShader, GL32.GL_GEOMETRY_SHADER);
		}
		
		programID = GL20.glCreateProgram();
		
		GL20.glAttachShader(programID, vertexID);
		GL20.glAttachShader(programID, fragmentID);
		
		if (geometryShader != null)
			GL20.glAttachShader(programID, geometryID);
		
		GL20.glLinkProgram(programID);
		
	}
	
	private int loadShader(String filename, int type) {
		
		String source = "";
		int id;
		
		try {
			
			Scanner scan = new Scanner(new File(filename));
			
			while (scan.hasNextLine()) {
				source += scan.nextLine() + "\n";
			}
			
			scan.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		id = GL20.glCreateShader(type);
		
		GL20.glShaderSource(id, source);
		GL20.glCompileShader(id);
		
		int result = GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS);
		if (result == 0) {
			
			if (type == GL20.GL_VERTEX_SHADER)
				System.err.println("Vertex Shader");
			if (type == GL20.GL_FRAGMENT_SHADER)
				System.err.println("Fragment Shader");
			if (type == GL32.GL_GEOMETRY_SHADER)
				System.err.println("Geometry Shader");
			
			
			System.err.println(GL20.glGetShaderInfoLog(id, 500));
		}
		
		return id;
		
	}
	
	public int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(programID, name);
	}
	
	public void setVector3(int id, Vector3f vec) {
		
		GL20.glUniform3f(id, vec.x, vec.y, vec.z);
		
	}
	
	public void setVector2(int id, Vector2f vec) {
		GL20.glUniform2f(id, vec.x, vec.y);
	}
	
	public void setVector3Array(int id, float[] vec) {
		GL20.glUniform3(id, Utils.asFloatBuffer(vec));
	}
	
	public void setFloatArray(int id, float[] data) {
		GL20.glUniform1(id, Utils.asFloatBuffer(data));
	}
	
	public void setMatrix4(int id, Matrix4f mat) {
		
		FloatBuffer buf = BufferUtils.createFloatBuffer(4*4);
		mat.store(buf);
		buf.flip();
		
		GL20.glUniformMatrix4(id, false, buf);
		
		buf.clear();
		buf = null;
		
		
	}
	
	public void setFloat(int id, float dat) {
		GL20.glUniform1f(id, dat);
	}
	
	public void setInt(int id, int dat) {
		GL20.glUniform1i(id, dat);
	}
	
	public void setBool(int id, boolean dat) {
		if (dat)
			GL20.glUniform1i(id, 1);
		else
			GL20.glUniform1i(id, 0);
	}
	
	public void useShader() {
		
		GL20.glUseProgram(programID);
		
	}
	
}
