package matt8110.mattengine.cubemap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Utils;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.geometry.VAO;

public class Skybox {

	private static int texture;
	private static boolean enabled = false;
	private static VAO vao;
	
	public static void setSkybox(String left, String right, String top, String bottom, String front, String back) {
		createSkybox(left, right, top, bottom, front, back);
	}
	
	public static void setSkybox(String folder) {
		createSkybox(folder + "/left.png", folder + "/right.png", folder + "/top.png", folder + "/bottom.png", folder + "/front.png", folder + "/back.png");
	}
	
	public static void createSkybox(String left, String right, String top, String bottom, String front, String back) {
		
		texture = Utils.loadCubeMap(front, back, left, right, top, bottom);
		
		float[] vertices = {
				 -1.0f,-1.0f,-1.0f, 
				  -1.0f,-1.0f, 1.0f,
				  -1.0f, 1.0f, 1.0f, 
				  1.0f, 1.0f,-1.0f, 
				  -1.0f,-1.0f,-1.0f,
				  -1.0f, 1.0f,-1.0f, 
				  1.0f,-1.0f, 1.0f,
				  -1.0f,-1.0f,-1.0f,
				  1.0f,-1.0f,-1.0f,
				  1.0f, 1.0f,-1.0f,
				  1.0f,-1.0f,-1.0f,
				  -1.0f,-1.0f,-1.0f,
				  -1.0f,-1.0f,-1.0f,
				  -1.0f, 1.0f, 1.0f,
				  -1.0f, 1.0f,-1.0f,
				  1.0f,-1.0f, 1.0f,
				  -1.0f,-1.0f, 1.0f,
				  -1.0f,-1.0f,-1.0f,
				  -1.0f, 1.0f, 1.0f,
				  -1.0f,-1.0f, 1.0f,
				  1.0f,-1.0f, 1.0f,
				  1.0f, 1.0f, 1.0f,
				  1.0f,-1.0f,-1.0f,
				  1.0f, 1.0f,-1.0f,
				  1.0f,-1.0f,-1.0f,
				  1.0f, 1.0f, 1.0f,
				  1.0f,-1.0f, 1.0f,
				  1.0f, 1.0f, 1.0f,
				  1.0f, 1.0f,-1.0f,
				  -1.0f, 1.0f,-1.0f,
				  1.0f, 1.0f, 1.0f,
				  -1.0f, 1.0f,-1.0f,
				  -1.0f, 1.0f, 1.0f,
				  1.0f, 1.0f, 1.0f,
				  -1.0f, 1.0f, 1.0f,
				  1.0f,-1.0f, 1.0f
		};
		
		vao = new VAO(vertices);
		
		enabled = true;
		
	}
	
	public static void enableSkybox(boolean enable) {
		
		if (vao != null)
			enabled = enable;
		
	}
	
	public static void renderSkybox() {
		
		
		if (enabled) {
			
			//System.out.println("rendering");
			
			Window.skyboxShader.useShader();
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
			
			Window.skyboxShader.setProjectionAndViewMatrix();
			Window.skyboxShader.setTransformation(Window.currentCamera.getPosition(), new Vector3f(),  1000);
			
			GL11.glDisable(GL11.GL_CULL_FACE);
			
			GL30.glBindVertexArray(vao.getVaoID());
		
			GL20.glEnableVertexAttribArray(0);
		
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
		
			GL30.glBindVertexArray(0);
		
			GL11.glEnable(GL11.GL_CULL_FACE);
		}
		
	}
	
}
