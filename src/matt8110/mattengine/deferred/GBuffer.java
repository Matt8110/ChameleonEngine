package matt8110.mattengine.deferred;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import matt8110.mattengine.core.FBO;
import matt8110.mattengine.core.Utils;
import matt8110.mattengine.gui.Texture2D;

public class GBuffer {

	private static FBO _gBuffer;
	private static GBuffer2D output;
	public static int _finalGBloom;
	
	public static void initGBuffer(int msaa) {
		
		//For convenience
		if (msaa < 1)
			msaa = 1;
		
		_gBuffer = new FBO(Display.getWidth() * msaa, Display.getHeight() * msaa);
		_gBuffer.addDepthAttachment();
		_gBuffer.addGBufferAttachment();
		
		output = new GBuffer2D();
		
	}
	
	public static void bindGBufferFBO() {
		_gBuffer.bindFBO();
	}
	
	public static FBO getFBO() {
		return _gBuffer;
	}
	
	public static void renderGBuffer() {
		
		//Setting all gBuffer textures
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _gBuffer._gPosition);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _gBuffer._gNormal);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _gBuffer._gDiffuse);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _gBuffer._gSpecular);
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _finalGBloom);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		
		output.render();
		
	}
	
	public static void unbindGBufferFBO() {
		_gBuffer.unbindFBO();
	}
	
}
