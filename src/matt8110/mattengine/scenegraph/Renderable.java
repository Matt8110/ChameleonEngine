package matt8110.mattengine.scenegraph;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.core.Window;
import matt8110.mattengine.geometry.VAO;
import matt8110.mattengine.shaders.Material;
import matt8110.mattengine.shaders.ShaderType;

public class Renderable {

	protected Vector3f position = new Vector3f();
	protected Vector3f rotation = new Vector3f();
	protected float scale = 1.0f;
	protected VAO vao;
	protected boolean canRender = true;
	public Material material = new Material();
	
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
	
	public void setRenderable(boolean enable) {
		canRender = enable;
	}
	
	public float getScale() {
		return scale;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
	}
	
	public void render(ShaderType type) {
		
		if (canRender) {
		
			//Window.mainShader.setTransformation(position, rotation, scale);
			if (type == ShaderType.SHADOW) {
				Window.shadowShader.setTransformation(position, rotation, scale);
			}
				
			if (type == ShaderType.GBUFFER) {
				Window.gBufferShader.setTransformation(position, rotation, scale);
				material.setShaderData(Window.gBufferShader);
			}
			
			
			if (type != ShaderType.SHADOW || material.getCanCastShadows()) {
			
				if (!material._cullingEnabled)
					GL11.glDisable(GL11.GL_CULL_FACE);
				
				GL30.glBindVertexArray(vao.getVaoID());
				
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				GL20.glEnableVertexAttribArray(2);
				
				//Enable normal mapping if there is a texture
				if (material.normalMap != -1) {
					GL20.glEnableVertexAttribArray(3);
				}
				
				GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vao.getVertexCount());
				
				GL30.glBindVertexArray(0);
				
				GL11.glEnable(GL11.GL_CULL_FACE);
			
			}
		
		}
		
	}
	
}
