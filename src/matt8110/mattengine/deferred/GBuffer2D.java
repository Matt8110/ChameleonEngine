package matt8110.mattengine.deferred;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;

import matt8110.mattengine.core.Window;
import matt8110.mattengine.geometry.VAO;
import matt8110.mattengine.geometry.VAO2D;

public class GBuffer2D {

	public Vector2f position, scale;
	private VAO2D vao;
	
	public GBuffer2D() {
		
		this.position = new Vector2f(0, 0);
		this.scale = new Vector2f(Display.getWidth(), Display.getHeight());
		
		init();
		
	}
	
	public void render() {
		
		Window.setCulling(false);
		
		GL30.glBindVertexArray(vao.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
		
		GL30.glBindVertexArray(0);
		
		Window.setCulling(true);
		
		
	}
	
	private void init() {
		
		float scalerX = (2.0f / Display.getWidth());
		float scalerY = (2.0f / Display.getHeight());
		
		float scaledX = scalerX * position.x - 1.0f;
		float scaledY = scalerY * (position.y) - 1.0f;
		float scaledW = (scalerX * scale.x) + scaledX;
		float scaledH = (scalerY * scale.y) + scaledY;
		
		float[] vertices = {
				scaledX, scaledY,
				scaledX, scaledH,
				scaledW, scaledY,
				
				scaledW, scaledY,
				scaledX, scaledH,
				scaledW, scaledH,
				
		};
		
		float[] texCoords = {
				0, 0,
				0, 1,
				1, 0,
				
				1, 0,
				0, 1,
				1, 1
		};
		
		if (vao == null)
			vao = new VAO2D(vertices, texCoords);
		else
			vao.modifyVAO(vertices, texCoords);
		
	}
	
}
