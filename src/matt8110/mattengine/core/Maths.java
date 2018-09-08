package matt8110.mattengine.core;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import matt8110.mattengine.lighting.Shadows;

public class Maths {
	
	private static Matrix4f mat;
	private static float aspectRatio, y_scale, x_scale, frustum_length;

	public static float getDistance(Vector3f pos1, Vector3f pos2) {
		return (float) Math.sqrt( ((pos2.x - pos1.x) * (pos2.x - pos1.x)) + ((pos2.y - pos1.y) * (pos2.y - pos1.y)) + ((pos2.z - pos1.z) * (pos2.z - pos1.z)));
	}
	
	public static float getDistance2D(Vector3f pos1, Vector3f pos2) {
		return (float) Math.sqrt( ((pos2.x - pos1.x) * (pos2.x - pos1.x)) + ((pos2.y - pos1.y) * (pos2.y - pos1.y)));
	}
	
	public static float getDistanceFast(Vector3f pos1, Vector3f pos2) {
		return ((pos2.x - pos1.x) * (pos2.x - pos1.x)) + ((pos2.y - pos1.y) * (pos2.y - pos1.y)) + ((pos2.z - pos1.z) * (pos2.z - pos1.z));
	}
	
	public static float getDistanceFast2D(Vector3f pos1, Vector3f pos2) {
		return ((pos2.x - pos1.x) * (pos2.x - pos1.x)) + ((pos2.y - pos1.y) * (pos2.y - pos1.y));
	}
	
	public static Matrix4f getProjectionMatrix(int fov, float NEAR_PLANE, float FAR_PLANE) {
		
		mat = new Matrix4f();
		
		aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		y_scale = (float) ((1f / Math.tan(Math.toRadians(fov / 2.0f))) * aspectRatio);
		x_scale = y_scale / aspectRatio;
		frustum_length = FAR_PLANE - NEAR_PLANE;
        
        
        mat.m00 = x_scale;
        mat.m11 = y_scale;
        mat.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        mat.m23 = -1;
        mat.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        mat.m33 = 0;
        
        
		return mat;
		
	}
	
	public static Matrix4f getShadowBiasMatrix() {
		
		mat = new Matrix4f();
		
		mat.m00 = 0.5f;
		mat.m11 = 0.5f;
		mat.m22 = 0.5f;
		mat.m30 = 0.5f;
		mat.m31 = 0.5f;
		mat.m32 = 0.5f;
		mat.m33 = 1.0f;
		
		return mat;
	}
	
	public static Matrix4f getTransformtionMatrix(Vector3f position, Vector3f rotation, float scale) {
		
		mat = new Matrix4f();
		mat.setIdentity();
		
		mat.translate(position);
		mat.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0));
		mat.rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
		mat.rotate((float)Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
		mat.scale(new Vector3f(scale, scale, scale));
		
		return mat;
		
	}
	
	public static Matrix4f getViewMatrix() {
		
		mat = new Matrix4f();
		mat.setIdentity();
		
		mat.rotate((float)Math.toRadians(Window.currentCamera.rotation.x), new Vector3f(1, 0, 0));
		mat.rotate((float)Math.toRadians(-Window.currentCamera.rotation.y + 90), new Vector3f(0, 1, 0));
		mat.rotate((float)Math.toRadians(Window.currentCamera.rotation.z), new Vector3f(0, 0, 1));
		mat.translate(new Vector3f(-Window.currentCamera.position.x, -Window.currentCamera.position.y, -Window.currentCamera.position.z));
		
		return mat;
		
	}
	
	public static Matrix4f getShadowViewMatrix(float rotX, float rotY){
		
		mat = new Matrix4f();
		mat.setIdentity();
		
		mat.rotate((float)Math.toRadians(rotX), new Vector3f(1, 0, 0));
		mat.rotate((float)Math.toRadians(-rotY+90), new Vector3f(0, 1, 0));
		mat.rotate((float)0, new Vector3f(0, 0, 1), mat);
		
		mat.translate(new Vector3f(-Window.currentCamera.position.x, -Window.currentCamera.position.y, -Window.currentCamera.getPosition().z));
		
		
		return mat;
		
	}
	
	public static Matrix4f createOrthoMatrix(){
		
		mat = new Matrix4f();
		
		float near = -(Shadows._orthoSize/2.0f + 20);
		float far = Shadows._orthoSize/2.0f + 20;
		float left = -(Shadows._orthoSize/2.0f);
		float right = Shadows._orthoSize/2.0f;
		float top = -(Shadows._orthoSize/2.0f);
		float bottom = Shadows._orthoSize/2.0f;
		
		
		mat.m00 = 2.0f / (right - left);
		mat.m01 = 0.0f;
		mat.m02 = 0.0f;
		mat.m03 = 0.0f;
				
		mat.m10 = 0.0f;
		mat.m11 = 2.0f / (top - bottom);
		mat.m12 = 0.0f;
		mat.m13 = 0.0f;
				
		mat.m20 = 0.0f;
		mat.m21 = 0.0f;
		mat.m22 = -2.0f / (far - near);
		mat.m23 = 0.0f;
				
		mat.m30 = -(right + left  ) / (right - left  );
		mat.m31 = -(top   + bottom) / (top   - bottom);
		mat.m32 = -(far   + near  ) / (far   - near  );
		mat.m33 = 1.0f;
        
        
		return mat;
		
	}
	
}
