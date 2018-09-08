package matt8110.mattengine.lighting;

import org.lwjgl.opengl.GL11;

import matt8110.mattengine.core.FBO;
import matt8110.mattengine.core.Window;
import matt8110.mattengine.scenegraph.Renderer;
import matt8110.mattengine.shaders.ShaderType;

public class Shadows {

	public static FBO _shadowFBO;
	public static int _orthoSize;
	public static boolean enabled = false;
	
	public static void initShadows(int mapSize, int size) {
		
		//Create FBO for shadows
		_shadowFBO = new FBO(mapSize, mapSize);
		_shadowFBO.addDepthAttachment();
		_shadowFBO.addTextureAttachment();
		_shadowFBO.addDepthTextureAttachment();
		
		_orthoSize = size;
		
	}
	
	public static void renderShadows() {
		Window.shadowShader.setProjectionAndViewMatrix();
		_shadowFBO.bindFBO();
		GL11.glCullFace(GL11.GL_FRONT);
		Renderer.render(ShaderType.SHADOW);
		GL11.glCullFace(GL11.GL_BACK);
		_shadowFBO.unbindFBO();
	}
	
	public static void setShadows(boolean enable) {
		enabled = enable;
	}
	
}
