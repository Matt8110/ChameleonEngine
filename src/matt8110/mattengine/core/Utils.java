package matt8110.mattengine.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.TextureLoader;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Utils {
	
	public static float deltaTime = 0;
	private static long lastTime = System.currentTimeMillis();
	private static long lastFPSTime = System.currentTimeMillis();
	private static int FPSCounter = 0;
	public static int FPS = 0;

	public static FloatBuffer asFloatBuffer(float[] data) {
		
		FloatBuffer tempBuffer = BufferUtils.createFloatBuffer(data.length);
		tempBuffer.put(data);
		tempBuffer.flip();
		
		return tempBuffer;
		
	}
	
	public static float lerp(float a, float b, float f)
	{
	    return a + f * (b - a);
	}
	
	public static float distanceTo3D(float x1, float y1, float z1, float x2, float y2, float z2) {
		
		return (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1) + (z2-z1)*(z2-z1);
		
	}
	
	public static float distanceTo2D(float x1, float z1, float x2, float z2) {
		
		return (x2-x1)*(x2-x1) + (z2-z1)*(z2-z1);
		
	}
	
	public static IntBuffer asIntBuffer(int[] data) {
		
		IntBuffer tempBuffer = BufferUtils.createIntBuffer(data.length);
		tempBuffer.put(data);
		tempBuffer.flip();
		
		return tempBuffer;
		
	}
	
	public static float getDeltaTime() {
		return deltaTime;
	}
	
	//Delta timing
	public static void _tickDeltaTime() {
		
		deltaTime = System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		
		FPSCounter++;
		
		if (System.currentTimeMillis() - lastFPSTime > 1000) {
			FPS = FPSCounter;
			FPSCounter = 0;
			lastFPSTime = System.currentTimeMillis();
		}
		
	}
	
	public static int loadTexture(String file) {
		
		String[] split = file.split("/");
		String filename = split[split.length-1];
		
		try {
			
			if (TextureManager.textures.containsKey(filename)) {
				return TextureManager.textures.get(filename);
			}
			
			int tempTex = TextureLoader.getTexture("PNG", new FileInputStream(new File(file))).getTextureID();
			
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			
			if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic){
				float amount = Math.min(4, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			}
			
			TextureManager.textures.put(filename, tempTex);
			
			return tempTex;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 1;
		
	}
	
	public static int createEmptyCubeMap(int size) {
		
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		
		for (int i = 0; i < 6; i++) {
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA8, size, size, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
		}
		
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, 0);
		
		return texID;
		
	}
	
	public static int loadCubeMap(String front, String back, String left, String right, String top, String bottom) {
		
		String[] textures = {
				right, left, top, bottom, back, front
		};
		
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
		
		for (int i = 0; i < textures.length; i++) {
			TextureData data = decodeTextureFile(textures[i]);
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		
		return texID;
		
	}
	
	private static TextureData decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}
	
}
