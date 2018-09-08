package matt8110.mattengine.gui;

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

public class Texture2D {

	public Vector2f position, scale;
	private int texture;
	private VAO2D vao;
	
	public Texture2D(String textureFile, Vector2f position, Vector2f scale) {
		
		this.position = position;
		this.scale = scale;
		
		loadTexture(textureFile);
		init();
		
	}
	
	public Texture2D(int texture, Vector2f position, Vector2f scale) {
		
		this.position = position;
		this.scale = scale;
		this.texture = texture;
		
		
		init();
		
	}
	
	public void render() {
		
		Window.setCulling(false);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		
		GL30.glBindVertexArray(vao.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
		
		GL30.glBindVertexArray(0);
		
		Window.setCulling(true);
		
		
	}
	
	public void render(int texture) {
		
		Window.setCulling(false);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		
		GL30.glBindVertexArray(vao.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
		
		GL30.glBindVertexArray(0);
		
		Window.setCulling(true);
		
	}
	
	public boolean mouseInside() {
		
		if (Mouse.getX() > position.x && Mouse.getX() < position.x + scale.x &&
			Mouse.getY() > position.y && Mouse.getY() < position.y + scale.y)
			return true;
		
		return false;
		
	}
	
	public void render(float x, float y) {
		
		Window.setCulling(false);
		
		setPosition(x, y);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		
		GL30.glBindVertexArray(vao.getVaoID());
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
		
		GL30.glBindVertexArray(0);
		
		Window.setCulling(true);
		
	}
	
	private void loadTexture(String textureFile) {
		//texture = Utils.loadTexture(textureFile);
	}
	
	public void setPosition(float x, float y) {
		
		position.x = x;
		position.y = y;
		
		init();
		
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
