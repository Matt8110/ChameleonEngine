package matt8110.mattengine.core;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL42;

public class FBO {

	private int fboID;
	private int textureID, depthBufferTextureID;
	public int _gPosition, _gNormal, _gDiffuse, _gTangent, _gTexCoord;
	public int width, height;
	
	public FBO(int width, int height) {
		
		this.width = width;
		this.height = height;
		
		fboID = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		
	}
	
	public void addDepthAttachment() {
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
		
		int depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
	}
	
	public void addDepthTextureAttachment() {
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
		
		GL11.glDrawBuffer(GL11.GL_NONE);
		GL11.glReadBuffer(GL11.GL_NONE);
		
		depthBufferTextureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthBufferTextureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0,
				GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthBufferTextureID, 0);
		
		GL11.glDrawBuffer(GL11.GL_NONE);
		GL11.glReadBuffer(GL11.GL_NONE);
		
		depthBufferTextureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthBufferTextureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT16, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, depthBufferTextureID, 0);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
	}
	
	public void addTextureAttachment() {
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
		
		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, textureID, 0);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
	}
	
	public void setCubeMapTexture(int texID, int i){
		//GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, texID, 0);
	}
	
	public void addGBufferAttachment() {
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
		
		int[] drawBuffers = {
				GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1, GL30.GL_COLOR_ATTACHMENT2, GL30.GL_COLOR_ATTACHMENT3, GL30.GL_COLOR_ATTACHMENT4
		};
		
		IntBuffer test = Utils.asIntBuffer(drawBuffers);
		
		GL20.glDrawBuffers(test);
		
		_gPosition = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _gPosition);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB16F, width, height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, _gPosition, 0);
		
		_gNormal = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _gNormal);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB16F, width, height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, _gNormal, 0);
		
		_gDiffuse = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _gDiffuse);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB16F, width, height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT2, _gDiffuse, 0);
		
		_gTangent = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _gTangent);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB16F, width, height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT3, _gTangent, 0);
		
		_gTexCoord = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, _gTexCoord);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB16F, width, height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT4, _gTexCoord, 0);
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
	}
	
	public void bindFBO() {
		
		//GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthBufferTextureID);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboID);
		GL11.glViewport(0, 0, width, height);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
	}
	
	public void unbindFBO() {
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
	}
	
	public int getTexture() {
		
		return textureID;
		
	}
	
	public int getDepthBufferTexture() {
		
		return depthBufferTextureID;
		
	}
	
}
